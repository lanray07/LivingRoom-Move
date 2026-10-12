"""Package the completed source and evidence without local secrets, SDK paths or build caches."""
from pathlib import Path
import hashlib
import json
import shutil
import zipfile
import os

ROOT = Path(__file__).resolve().parents[1]
OUT = ROOT / 'deliverables'
OUT.mkdir(exist_ok=True)
exclude = {'build', '.gradle', '.kotlin', '.idea', '.git', '.remotion', 'artifacts', 'deliverables', '__pycache__', 'node_modules', 'out'}
with zipfile.ZipFile(OUT / 'LivingRoomMove-source.zip', 'w', zipfile.ZIP_DEFLATED) as archive:
    for folder, directories, files in os.walk(ROOT):
        directories[:] = sorted(d for d in directories if d not in exclude)
        if Path(folder).relative_to(ROOT).parts == ('workout-studio', 'public'):
            directories[:] = [d for d in directories if d != 'audio']
        for name in sorted(files):
            path = Path(folder) / name
            if path.name == 'local.properties' or path.suffix in {'.pem','.jks','.keystore'} or path.name.startswith('.env'):
                continue
            archive.write(path, Path('LivingRoomMove') / path.relative_to(ROOT))
for path in [ROOT / 'app/build/outputs/apk/debug/app-debug.apk', ROOT / 'app/build/outputs/apk/release/app-release-unsigned.apk',
             ROOT / 'docs/VERIFICATION.md', ROOT / 'artifacts/dependency-scan.json', ROOT / 'artifacts/final-verification.log',
             ROOT / 'artifacts/mvp-followup.log', ROOT / 'artifacts/workout-content-build.log', ROOT / 'artifacts/workout-render.log',
             ROOT / 'artifacts/workout-instrumentation-build.log', ROOT / 'artifacts/workout-studio-audit-final.json']:
    if path.exists():
        shutil.copy2(path, OUT / path.name)
manifest = []
for path in sorted(OUT.iterdir()):
    if path.is_file() and path.name != 'manifest.json':
        manifest.append({'file':path.name,'bytes':path.stat().st_size,'sha256':hashlib.sha256(path.read_bytes()).hexdigest()})
(OUT / 'manifest.json').write_text(json.dumps({'status':'ENGINEERING_PREVIEW_NOT_FOR_SUBMISSION','files':manifest},indent=2),encoding='utf-8')
print(f'Packaged source, APKs and evidence in {OUT}')
