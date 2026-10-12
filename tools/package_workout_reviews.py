"""Package locally generated review media without claiming exercise or rights approval."""
import hashlib,json,zipfile
from pathlib import Path
from validate_workout_pack import validate
from create_workout_drafts import ROOT,OUT

def package():
    validate(media=True)
    destination=ROOT/'deliverables/LivingRoomMove-workout-reviews.zip'
    destination.parent.mkdir(exist_ok=True)
    inventory=[]
    with zipfile.ZipFile(destination,'w',zipfile.ZIP_DEFLATED) as archive:
        def add(file,relative):
            archive.write(file,relative)
            inventory.append(dict(file=relative.as_posix(),bytes=file.stat().st_size,sha256=hashlib.sha256(file.read_bytes()).hexdigest()))
        add(ROOT/'content/README.md',Path('README.md'))
        sessions=json.loads((OUT/'index.json').read_text(encoding='utf-8'))
        for session in sessions:
            folder=Path(session['id'])
            for name in ['timeline.json','production-script.md','recording-script.md','captions-en-GB.vtt','speech-cues.json']:
                add(OUT/session['id']/name,folder/name)
            add(ROOT/f'workout-studio/public/audio/{session["id"]}.wav',folder/'synthetic-review-narration.wav')
            for name in ['silent-review.mp4','narrated-review.mp4','chapter-00.png']:
                add(ROOT/f'workout-studio/out/{session["id"]}/{name}',folder/name)
        manifest=dict(status='UNREVIEWED_NOT_FOR_EXERCISE_USE',syntheticVoice=True,humanRecorded=False,
                      distributionRights='NOT_VERIFIED_REVIEW_ONLY',durationMinutes=100,sessions=10,files=inventory)
        archive.writestr('manifest.json',json.dumps(manifest,indent=2))
    with zipfile.ZipFile(destination) as archive:
        assert archive.testzip() is None
    print(f'Packaged {len(sessions)} full-duration review sessions: {destination}')

if __name__=='__main__': package()
