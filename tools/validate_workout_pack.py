"""Verify review media integrity and confirm that app publication remains denied."""
from pathlib import Path
import json, re, wave, argparse, subprocess
from create_workout_drafts import ROOT, OUT

def seconds(value):
    h,m,s = value.split(':')
    return int(h)*3600+int(m)*60+float(s)

def validate(media=False):
    sessions = json.loads((OUT/'index.json').read_text(encoding='utf-8'))
    catalogue = json.loads((ROOT/'app/src/main/assets/catalogue-v1.json').read_text(encoding='utf-8'))
    assert len(sessions) == 10 and {s['id'] for s in sessions} == {w['id'] for w in catalogue['workouts']}
    for session in sessions:
        assert session['durationSeconds'] in (300,600,900)
        end = 0
        for c in session['chapters']:
            assert c['start'] == end and c['seconds'] > 0 and c['script'].strip()
            end += c['seconds']
        assert end == session['durationSeconds']
        folder = OUT/session['id']
        previous_end = 0
        cues = re.findall(r'(\d{2}:\d{2}:\d{2}\.\d{3}) --> (\d{2}:\d{2}:\d{2}\.\d{3})', (folder/'captions-en-GB.vtt').read_text(encoding='utf-8'))
        assert cues
        for begin,finish in cues:
            start,end = seconds(begin), seconds(finish)
            assert previous_end <= start < end <= session['durationSeconds']
            previous_end = end
        record = next(w for w in catalogue['workouts'] if w['id']==session['id'])
        assert record['placeholder'] and record['review']['status']=='DRAFT'
        assert record['review']['reviewer'] is None and record['review']['evidenceId'] is None
        assert all(not v['approved'] and not v['humanRecorded'] for v in record['voices'])
        assert record['videoUri'] is None and not record['licence']['reference']
        if media:
            with wave.open(str(ROOT/f'workout-studio/public/audio/{session["id"]}.wav')) as audio:
                assert abs(audio.getnframes()/audio.getframerate()-session['durationSeconds']) < .001
            for filename in ['silent-review.mp4','narrated-review.mp4']:
                file=ROOT/f'workout-studio/out/{session["id"]}/{filename}'
                assert file.is_file() and file.stat().st_size > 1000, file
                import imageio_ffmpeg
                result=subprocess.run([imageio_ffmpeg.get_ffmpeg_exe(),'-hide_banner','-i',str(file)],capture_output=True,text=True)
                duration=re.search(r'Duration: (\d{2}:\d{2}:\d{2}\.\d{2})',result.stderr)
                assert duration and abs(seconds(duration.group(1))-session['durationSeconds']) < .1
                assert '1920x1080' in result.stderr and 'Video: h264' in result.stderr
                assert ('Audio:' in result.stderr) == (filename=='narrated-review.mp4')
    print(f'Validated {len(sessions)} exact timelines, caption bounds and draft publication denial' + ('; review audio/video files present.' if media else '.'))

if __name__ == '__main__':
    p=argparse.ArgumentParser(); p.add_argument('--media',action='store_true')
    validate(p.parse_args().media)
