"""Deterministic sizes/type, source files and provenance for original identity exports."""
from pathlib import Path
from PIL import Image, ImageDraw, ImageFont, ImageOps
import hashlib
import json

ROOT = Path(__file__).resolve().parents[1]
SOURCE = ROOT / 'assets/source'
STORE = ROOT / 'assets/store'
SOURCE.mkdir(parents=True, exist_ok=True)
STORE.mkdir(parents=True, exist_ok=True)
FONT = Path('C:/Windows/Fonts/segoeui.ttf')
if not FONT.exists():
    FONT = Path('/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf')
def font(size):
    return ImageFont.truetype(str(FONT), size)
green, cream = '#294C3B', '#F5F1E8'
svg = '''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 512 512"><rect width="512" height="512" fill="#294C3B"/><path d="M114 261L256 137L398 261M161 242V370H351V242M218 313L256 270L294 313" fill="none" stroke="#F5F1E8" stroke-width="28" stroke-linecap="round" stroke-linejoin="round"/></svg>'''
(SOURCE / 'identity.svg').write_text(svg, encoding='utf-8')
def identity(size):
    factor = 4
    image = Image.new('RGB', (size*factor, size*factor), green)
    d = ImageDraw.Draw(image)
    def line(points):
        points = [(int(x*size*factor/512), int(y*size*factor/512)) for x,y in points]
        width = int(28*size*factor/512)
        d.line(points, fill=cream, width=width, joint='curve')
        for x,y in points:
            d.ellipse((x-width/2,y-width/2,x+width/2,y+width/2), fill=cream)
    line([(114,261),(256,137),(398,261)])
    line([(161,242),(161,370),(351,370),(351,242)])
    line([(218,313),(256,270),(294,313)])
    return image.resize((size,size), Image.Resampling.LANCZOS)
for size in (114,512):
    identity(size).convert('RGBA').save(STORE / f'icon-{size}.png')
icon = Image.new('RGB', (1280,720), green)
icon.paste(identity(240), (520,120))
d = ImageDraw.Draw(icon)
d.text((640,440), 'LivingRoom Move', font=font(76), fill=cream, anchor='mm')
icon.save(STORE / 'fire-tv-icon-1280x720.png')
banner_folder = ROOT / 'app/src/main/res/drawable-nodpi'
banner_folder.mkdir(parents=True, exist_ok=True)
icon.resize((320,180), Image.Resampling.LANCZOS).save(banner_folder / 'tv_banner.png')
icon.resize((1920,1080), Image.Resampling.LANCZOS).save(STORE / 'splash-1920x1080.png')
portrait_path = SOURCE / 'brand-portrait-ai.png'
if portrait_path.exists():
    portrait = Image.open(portrait_path).convert('RGB')
    ImageOps.fit(portrait,(1920,1080),method=Image.Resampling.LANCZOS).save(STORE / 'fire-tv-background-1920x1080.png')
    portrait.crop((int(portrait.width*.36),0,portrait.width,portrait.height)).resize((640,540),Image.Resampling.LANCZOS).save(banner_folder / 'brand_portrait.png')
for id, title in [('seated-foundations','Seated foundations'),('everyday-mobility','Everyday ease'),('session','At your own pace'),('empty','Room for a new beginning')]:
    cover = Image.new('RGB',(1280,720),cream)
    cover.paste(identity(240),(520,90))
    ImageDraw.Draw(cover).text((640,455),title,font=font(60),fill=green,anchor='mm')
    cover.save(STORE / f'{id}-cover.png')
manifest = []
for path in STORE.glob('*.png'):
    im = Image.open(path)
    manifest.append(dict(file=str(path.relative_to(ROOT)),width=im.width,height=im.height,mode=im.mode,
        sha256=hashlib.sha256(path.read_bytes()).hexdigest(),status='REVIEW_REQUIRED',
        source='AI-generated fictional non-instructional portrait' if 'background' in path.name else 'Original scripted identity and typography'))
(ROOT / 'assets/manifest.json').write_text(json.dumps(manifest,indent=2),encoding='utf-8')
print(f'Exported {len(manifest)} identity/store assets.')
