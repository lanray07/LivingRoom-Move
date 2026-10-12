"""Read-only OSV query of the resolved Maven runtime inventory. No credentials required.
Usage: ./gradlew :app:dependencies --configuration releaseRuntimeClasspath > artifacts/dependencies.txt
       python tools/scan_dependencies.py artifacts/dependencies.txt
This detects indexed advisories, not every possible vulnerability.
"""
import json
from pathlib import Path
import re
import sys
import urllib.request

source = Path(sys.argv[1]).read_text(encoding='utf-8-sig')
packages = {}
for match in re.finditer(r'(?:\+---|\\---)\s+([\w.-]+):([\w.-]+):([^\s]+)(?:\s+->\s+([^\s]+))?', source):
    group, artifact, version, resolved = match.groups()
    selected = resolved or version
    if ':' in selected:
        continue
    packages[f'{group}:{artifact}'] = selected
if not packages:
    raise SystemExit('No resolved Maven packages found. Supply the Gradle runtime dependency report.')
queries = [{'package': {'name': name, 'ecosystem': 'Maven'}, 'version': version} for name, version in sorted(packages.items())]
request = urllib.request.Request('https://api.osv.dev/v1/querybatch', data=json.dumps({'queries':queries}).encode(),
    headers={'Content-Type':'application/json'},method='POST')
with urllib.request.urlopen(request,timeout=60) as response:
    result = json.loads(response.read(5_000_000))
if len(result.get('results',[])) != len(queries):
    raise SystemExit('OSV response incomplete; do not treat as a successful scan.')
if any(r.get('next_page_token') for r in result['results']):
    raise SystemExit('OSV requires pagination; results are incomplete and must be investigated.')
entries = [{'package': q['package']['name'], 'version':q['version'], 'advisories':r.get('vulns',[])} for q,r in zip(queries,result['results'])]
report = {'source':'https://api.osv.dev/v1/querybatch','scope':'Resolved release Maven dependencies; excludes unindexed flaws and bundled SDK internals',
    'packages':entries,'advisoryCount':sum(len(p['advisories']) for p in entries)}
destination = Path(sys.argv[1]).parent / 'dependency-scan.json'
destination.write_text(json.dumps(report,indent=2),encoding='utf-8')
print(f"OSV queried {len(entries)} packages; {report['advisoryCount']} indexed advisory matches. Report: {destination}")
if report['advisoryCount']:
    raise SystemExit(1)
