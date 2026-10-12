"""Capture actual Android UI via adb. Usage: python tools/capture_ui.py home-preview.png"""
import os
from pathlib import Path
import subprocess
import sys
from PIL import Image

ROOT = Path(__file__).resolve().parents[1]
sdk = Path(os.environ.get('ANDROID_HOME', Path.home() / 'AppData/Local/Android/Sdk'))
adb = sdk / 'platform-tools' / ('adb.exe' if os.name == 'nt' else 'adb')
name = Path(sys.argv[1]).name
if not name.endswith('.png'):
    raise SystemExit('Use a PNG filename.')
folder = ROOT / 'assets/screenshots'
folder.mkdir(parents=True, exist_ok=True)
data = subprocess.run([str(adb), 'exec-out', 'screencap', '-p'], check=True, capture_output=True).stdout
target = folder / name
target.write_bytes(data)
with Image.open(target) as im:
    if im.size != (1920,1080):
        raise SystemExit(f'Actual capture is {im.size}; configure device output to 1920x1080 before store export. Source retained.')
    im.convert('RGB').save(target)
print(f'Actual UI captured: {target}')
