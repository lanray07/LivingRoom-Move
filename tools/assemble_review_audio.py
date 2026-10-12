"""Align generated sentence speech to review chapters and emit accurate timed VTT."""
from pathlib import Path
import json, re, wave, hashlib
from create_workout_drafts import ROOT, OUT, timestamp

def assemble():
    sessions = json.loads((OUT / 'index.json').read_text(encoding='utf-8'))
    audio_folder = ROOT / 'workout-studio/public/audio'
    audio_folder.mkdir(parents=True, exist_ok=True)
    manifest, studio_cues = [], {}
    for session in sessions:
        folder = OUT / session['id']
        utterances, timeline = [], bytearray()
        audio_format = None
        for index, chapter in enumerate(session['chapters']):
            spoken_bytes = bytearray()
            offset = chapter['start']
            sentences = re.split(r'(?<=[.!?])\s+', chapter['script'])
            for si, sentence in enumerate(sentences):
                with wave.open(str(ROOT / f'artifacts/review-narration/{session["id"]}/{index:02}-{si:02}.wav'), 'rb') as speech:
                    fmt = (speech.getnchannels(), speech.getsampwidth(), speech.getframerate())
                    if audio_format is None: audio_format = fmt
                    assert fmt == audio_format, 'Speech formats differ'
                    seconds = speech.getnframes() / fmt[2]
                    data = speech.readframes(speech.getnframes())
                spoken_bytes.extend(data)
                # Every cue is aligned to the exact synthesised sentence file.
                utterances.append(dict(start=offset, end=offset + seconds, text=sentence))
                offset += seconds
            channels, width, rate = audio_format
            target_bytes = chapter['seconds'] * channels * width * rate
            if len(spoken_bytes) > target_bytes:
                raise ValueError(f'{session["id"]}: narration exceeds {chapter["title"]}')
            timeline.extend(spoken_bytes)
            timeline.extend(b'\0' * (target_bytes - len(spoken_bytes)))
        output = audio_folder / (session['id'] + '.wav')
        with wave.open(str(output), 'wb') as file:
            file.setnchannels(audio_format[0]); file.setsampwidth(audio_format[1]); file.setframerate(audio_format[2])
            file.writeframes(timeline)
        vtt = ['WEBVTT', '', 'NOTE Unreviewed production draft. Synthetic voice, not human narration.', '']
        for i, cue in enumerate(utterances):
            vtt.extend([str(i + 1), f"{timestamp(cue['start'])} --> {timestamp(cue['end'])}", cue['text'], ''])
        (folder / 'captions-en-GB.vtt').write_text('\n'.join(vtt), encoding='utf-8')
        (folder / 'speech-cues.json').write_text(json.dumps(utterances, indent=2, ensure_ascii=False), encoding='utf-8')
        studio_cues[session['id']] = utterances
        manifest.append(dict(id=session['id'], durationSeconds=session['durationSeconds'],
                             voice='Microsoft Hazel Desktop', synthetic=True, humanRecorded=False,
                             rights='REVIEW_ONLY_DISTRIBUTION_RIGHTS_NOT_VERIFIED',
                             audioSha256=hashlib.sha256(output.read_bytes()).hexdigest(),
                             captionsSha256=hashlib.sha256((folder / 'captions-en-GB.vtt').read_bytes()).hexdigest()))
        print('Aligned audio/captions:', session['id'])
    (OUT / 'media-manifest.json').write_text(json.dumps(manifest, indent=2), encoding='utf-8')
    (ROOT/'workout-studio/src/speech-cues.json').write_text(json.dumps(studio_cues, ensure_ascii=False), encoding='utf-8')

if __name__ == '__main__': assemble()
