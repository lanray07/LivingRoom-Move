"""Release gate. Default validates preview fixtures; --release rejects every unapproved session."""
import argparse
import json
from pathlib import Path
import re
import sys
import xml.etree.ElementTree as ET

ROOT = Path(__file__).resolve().parents[1]
def validate(release=False):
    data = json.loads((ROOT / 'app/src/main/assets/catalogue-v1.json').read_text(encoding='utf-8'))
    assert data['schemaVersion'] == 1
    if not release:
        assert 8 <= len(data['workouts']) <= 12
    ids = [w['id'] for w in data['workouts']]
    assert len(ids) == len(set(ids))
    for w in data['workouts']:
        assert w['durationMinutes'] in (5, 10, 15)
        assert w['posture'] in ('SEATED', 'STANDING', 'MIXED')
        if release:
            assert not w['placeholder'], f"{w['id']}: placeholder cannot ship"
            assert w['review']['status'] == 'APPROVED', f"{w['id']}: review missing"
            assert all(w['review'].get(k) for k in ('date', 'reviewer', 'credentials', 'evidenceId'))
            assert w['videoUri'] and w['licence']['reference'] and w['movements'] and w['safetyPrompts']
            assert any(v['approved'] and v['humanRecorded'] and v['licenceReference'] for v in w['voices'])
    def resources(path):
        result = {}
        for element in ET.parse(path).getroot():
            if element.tag == 'string':
                result[element.attrib['name']] = element.text or ''
            elif element.tag == 'plurals':
                for form in element:
                    result[f"plural.{element.attrib['name']}.{form.attrib['quantity']}"] = form.text or ''
        return result
    base = resources(ROOT / 'app/src/main/res/values/strings.xml')
    for path in (ROOT / 'app/src/main/res').glob('values-*/strings.xml'):
        translated = resources(path)
        assert translated.keys() == base.keys(), f'{path}: resource mismatch'
        for key in base:
            tokens = lambda s: sorted(re.findall(r'%\d+\$[\d.]*[sdf]|%%', s))
            assert tokens(base[key]) == tokens(translated[key]), f'{path}: {key} format mismatch'
    print('Preview catalogue and resource checks passed.' if not release else 'Content release checks passed; external sign-offs still required.')

if __name__ == '__main__':
    args = argparse.ArgumentParser()
    args.add_argument('--release', action='store_true')
    try:
        validate(args.parse_args().release)
    except (AssertionError, KeyError, ValueError) as error:
        print(f'BLOCKED: {error}', file=sys.stderr)
        sys.exit(1)
