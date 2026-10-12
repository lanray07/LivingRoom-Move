"""Export captured UI as opaque 24-bit PNG; never fabricate or redraw application screens."""
from pathlib import Path
from PIL import Image
import hashlib
import json

ROOT = Path(__file__).resolve().parents[1]
destination = ROOT / 'assets/screenshots/review'
destination.mkdir(parents=True, exist_ok=True)
manifest = []
for source in sorted((ROOT / 'assets/screenshots/jvm').glob('*.png')):
    with Image.open(source) as image:
        if image.size != (1920,1080):
            raise ValueError(f'Unexpected capture size: {source}')
        target = destination / source.name
        image.convert('RGB').save(target)
    manifest.append(dict(file=str(target.relative_to(ROOT)),source=str(source.relative_to(ROOT)),
        capture='Actual Kotlin app, Android JVM native graphics, API34, xhdpi',
        physicalFireTV=False,status='ENGINEERING_PREVIEW_REQUIRES_REVIEW',width=1920,height=1080,
        sha256=hashlib.sha256(target.read_bytes()).hexdigest()))
(ROOT / 'assets/screenshots/manifest.json').write_text(json.dumps(manifest,indent=2),encoding='utf-8')
print(f'Exported {len(manifest)} actual UI previews; all require release-content/hardware review.')
