"""Developer-only provider-neutral translation. Output is NEVER installed into Android resources.

Provider contract: POST {locale, strings, glossary}; response {strings: {key: translated_text}}.
Environment: TRANSLATION_ENDPOINT (HTTPS), TRANSLATION_API_KEY. No credentials in command args/logs.
"""
import argparse
import hashlib
import json
import os
from pathlib import Path
import re
import urllib.request
import xml.etree.ElementTree as ET

ROOT = Path(__file__).resolve().parents[1]
LOCALES = ('fr', 'de', 'es', 'it', 'pt', 'nl')
def validate(source, result):
    if source.keys() != result.keys():
        raise ValueError('Missing or extra translation keys')
    for key in source:
        tokens = lambda text: sorted(re.findall(r'%\d+\$[\d.]*[sdf]|%%', text))
        if not isinstance(result[key], str) or not result[key].strip() or tokens(source[key]) != tokens(result[key]):
            raise ValueError(f'Invalid translation/format arguments: {key}')

def run(locale):
    endpoint = os.environ.get('TRANSLATION_ENDPOINT', '')
    secret = os.environ.get('TRANSLATION_API_KEY', '')
    if not endpoint.startswith('https://') or not secret:
        raise SystemExit('Configure HTTPS TRANSLATION_ENDPOINT and TRANSLATION_API_KEY locally.')
    source = {}
    for element in ET.parse(ROOT / 'app/src/main/res/values/strings.xml').getroot():
        if element.tag == 'string':
            source[element.attrib['name']] = element.text or ''
        elif element.tag == 'plurals':
            for form in element:
                source[f"plural.{element.attrib['name']}.{form.attrib['quantity']}"] = form.text or ''
    glossary = json.loads((ROOT / 'localisation/glossary.json').read_text(encoding='utf-8'))
    digest = hashlib.sha256(json.dumps([source, glossary, locale], sort_keys=True).encode()).hexdigest()
    memory = ROOT / 'localisation/memory' / f'{locale}-{digest}.json'
    if memory.exists():
        result = json.loads(memory.read_text(encoding='utf-8'))
    else:
        request = urllib.request.Request(endpoint, data=json.dumps(dict(locale=locale, strings=source, glossary=glossary)).encode(),
            headers={'Content-Type': 'application/json', 'Authorization': f'Bearer {secret}'}, method='POST')
        # A redirected request could expose credentials; disallow redirects.
        class NoRedirect(urllib.request.HTTPRedirectHandler):
            def redirect_request(self, *args, **kwargs):
                return None
        with urllib.request.build_opener(NoRedirect).open(request, timeout=90) as response:
            result = json.loads(response.read(2_000_000))['strings']
        validate(source, result)
        memory.parent.mkdir(parents=True, exist_ok=True)
        memory.write_text(json.dumps(result, ensure_ascii=False, indent=2), encoding='utf-8')
    validate(source, result)
    draft = dict(locale=locale, sourceDigest=digest, status='MACHINE_DRAFT', approved=False,
        humanReviewRequired=True, reviewRequiredFor=['safety', 'movement', 'purchases', 'all UI'], strings=result)
    destination = ROOT / 'localisation/drafts' / locale / 'strings.draft.json'
    destination.write_text(json.dumps(draft, ensure_ascii=False, indent=2), encoding='utf-8')
    print(f'Reviewable draft saved: {destination}. Nothing published.')

if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument('locale', choices=LOCALES)
    run(parser.parse_args().locale)
