"""Author review timelines; never confer approval or copy public exercise media."""
import json
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
OUT = ROOT / 'content' / 'drafts'
OUT.mkdir(parents=True, exist_ok=True)
SITTING = 'https://www.nhs.uk/live-well/exercise/sitting-exercises/'
BALANCE = 'https://www.nhs.uk/live-well/exercise/balance-exercises/'
FLEX = 'https://www.nhs.uk/live-well/exercise/flexibility-exercises/'

# Original concise production cues. Source links establish background only;
# adaptations, dose, timing and alternatives require session-specific review.
MOVES = {
    'seated-march': ('Small seated steps', 'Sit tall, feet resting on the floor. Hold the chair sides. Lift one bent knee a little, lower slowly, then change legs. Make each lift comfortable.', 'Small lift. Slow return.', SITTING),
    'ankles': ('Ankle movement', 'Holding the chair, extend one leg comfortably. Point the toes away, then draw them back. Return the foot to the floor before changing sides.', 'One side at a time.', SITTING),
    'turn': ('A comfortable turn', 'Sit upright with feet grounded. Cross your arms towards your shoulders. Keep the hips facing forward as the upper body turns gently. Return to centre before changing sides.', 'Keep the movement comfortable.', SITTING),
    'chest': ('Open across the chest', 'Sit upright. Ease the shoulders back and down. Open the arms to the sides and gently lift the chest, then relax. Avoid forcing the stretch.', 'Ease open. Relax fully.', SITTING),
    'neck': ('A gentle look around', 'From an upright seat, turn the head slowly within a comfortable range. Return to the middle before looking the other way. Keep the shoulders relaxed.', 'Comfortable range only.', FLEX),
    'side-bend': ('Easy side bend', 'Stand with feet apart and arms by your sides. Slide one hand a little down the same side, return upright, and change sides. Keep the movement small.', 'Return upright between sides.', FLEX),
    'calf': ('Wall-supported calf stretch', 'Hands against a solid wall, take one foot back and keep its heel grounded. Ease into a comfortable calf stretch, release, then change legs.', 'Release before changing legs.', FLEX),
    'side-step': ('Supported side steps', 'With stable support nearby, step to the side slowly and bring the other foot alongside. Change direction after a few comfortable steps. Keep the hips level.', 'Small steps. Stay near support.', BALANCE),
    'line-walk': ('Heel-to-toe steps', 'Beside a wall, place one heel just ahead of the other foot’s toes. Look forwards and take a few controlled steps, using the wall if needed.', 'Look forwards. Use support.', BALANCE),
    'stand': ('Supported one-leg practice', 'Face a wall with fingers resting against it. Keep the standing knee soft while lifting the other foot briefly. Put it down before changing sides.', 'Support first. Brief lift.', BALANCE),
}
SAFETY = [
    'This is an unreviewed production draft, not a session for exercise use.',
    'For a published seated session, use a stable chair without wheels, with feet comfortably supported. For standing content, use stable support and a clear floor.',
    'Move within a comfortable range. Stop if you feel pain, dizziness or unwell; do not continue through symptoms.',
    'If unsure whether an activity is suitable, ask a qualified health professional. This programme does not prescribe activity for a diagnosis.',
]
SERIES = {
    'chair-introduction': ['seated-march', 'ankles'],
    'chair-everyday': ['seated-march', 'ankles', 'turn', 'chest'],
    'chair-unhurried': ['seated-march', 'ankles', 'turn', 'chest', 'neck', 'seated-march'],
    'mobility-short': ['neck', 'chest'],
    'mobility-everyday': ['neck', 'turn', 'side-bend', 'calf'],
    'mobility-long': ['neck', 'chest', 'turn', 'side-bend', 'calf', 'ankles'],
    'balance-introduction': ['side-step', 'stand'],
    'balance-supported': ['side-step', 'stand', 'line-walk', 'side-step'],
    'balance-unhurried': ['side-step', 'stand', 'line-walk', 'side-step', 'stand', 'line-walk'],
    'chair-week-one': ['seated-march', 'ankles', 'chest', 'turn'],
}
ALT = {'side-bend': 'turn', 'calf': 'ankles', 'side-step': 'seated-march', 'line-walk': 'ankles', 'stand': 'seated-march'}

def timestamp(seconds):
    ms = round(seconds * 1000)
    return f'{ms // 3600000:02}:{ms // 60000 % 60:02}:{ms // 1000 % 60:02}.{ms % 1000:03}'

def generate():
    catalogue = json.loads((ROOT / 'app/src/main/assets/catalogue-v1.json').read_text(encoding='utf-8'))
    sessions = []
    for workout in catalogue['workouts']:
        total = workout['durationMinutes'] * 60
        ids = SERIES[workout['id']]
        chapters = []
        cursor = 0
        def chapter(kind, title, seconds, cue, script, **extra):
            nonlocal cursor
            chapters.append(dict(kind=kind, title=title, start=cursor, seconds=seconds, cue=cue, script=script, **extra))
            cursor += seconds
        chapter('intro', 'A moment to begin', 45, 'Stable support • Comfortable pace',
                f"Production review of {workout['title']}. This draft has not been professionally approved and is not for exercise use. The proposed session begins with a stable chair or wall, a clear space, and a comfortable pace. The reviewer must confirm the movements, alternatives and timing before filming.")
        # Timing allocates production windows, never mandatory repetitions.
        available = total - 45 - 30 - 20 * (len(ids) - 1)
        windows = [available // len(ids)] * len(ids)
        for i in range(available % len(ids)):
            windows[i] += 1
        for i, (move_id, seconds) in enumerate(zip(ids, windows)):
            title, script, cue, source = MOVES[move_id]
            alternative = ALT.get(move_id)
            chapter('movement', title, seconds, cue, script,
                    movementId=move_id, source=source,
                    alternativeId=alternative,
                    reviewerNote='Timing is a filming window including instruction, rest and optional repetitions. Reviewer sets dose; do not fill the window with continuous repetitions.',
                    alternativeNote=('Proposed separate seated activity; not an equivalent balance exercise. Reviewer must approve it.' if alternative else None))
            if i < len(ids) - 1:
                chapter('rest', 'Time to reset', 20, 'Rest is part of the session',
                        'A rest window follows. The final narrator will invite the viewer to settle, take a comfortable breath, or pause for longer. There is no target to chase.')
        chapter('outro', 'A calm finish', 30, 'A little time, at your own pace',
                'This is the proposed closing section. The final instructor will guide a comfortable finish and remind the viewer that resting or doing less is welcome. The session remains awaiting professional approval.')
        assert cursor == total
        session = dict(id=workout['id'], title=workout['title'], durationSeconds=total,
                       posture=workout['posture'], category=workout['category'], programmeId=workout['programmeId'],
                       status='DRAFT_REQUIRES_PROFESSIONAL_REVIEW', syntheticVoice=True, humanRecorded=False,
                       approved=False, safetyPrompts=SAFETY, chapters=chapters)
        sessions.append(session)
        folder = OUT / session['id']
        folder.mkdir(exist_ok=True)
        (folder / 'timeline.json').write_text(json.dumps(session, indent=2, ensure_ascii=False), encoding='utf-8')
        rows = ['# ' + session['title'], '', '**UNREVIEWED PRODUCTION DRAFT — NOT FOR EXERCISE USE**', '',
                'Timing windows include instruction, optional repetitions and rest. They are not a prescribed exercise dose.', '']
        for c in chapters:
            rows += [f"## {timestamp(c['start'])} · {c['title']} · {c['seconds']} seconds", '', c['script'], '']
            if c.get('source'): rows += [f"Background reference: [{c['movementId']}]({c['source']}). The NHS does not endorse this session.", '']
            if c.get('alternativeId'): rows += [f"Proposed seated option: {MOVES[c['alternativeId']][0]}. {c['alternativeNote']}", '']
            rows += ['Filming: approved instructor, full body and support visible; close-up only as a secondary view. No generated pose is an approved demonstration.', '']
        rows += ['## Sign-off', '', 'Reviewer / credentials / date / version / evidence reference: PENDING', '',
                 'Footage, performer and narration rights: PENDING', '']
        (folder / 'production-script.md').write_text('\n'.join(rows), encoding='utf-8')
        recording = ['# Human narration script · ' + session['title'], '',
                     '**DRAFT FOR PROFESSIONAL REVIEW AND RECORDING — NOT APPROVED FOR USE**', '',
                     'This proposed client-facing script differs from the synthetic producer review track. Record new audio and realign captions after approval. Bracketed dose notes are production instructions, not words to speak.', '']
        for c in chapters:
            if c['kind'] == 'intro':
                setup = ('Choose a stable chair without wheels, with your feet comfortably on the floor.' if session['posture']=='SEATED'
                         else 'Keep stable support within reach and your floor space clear. You can choose a reviewed seated session instead.')
                words = f"Welcome to {session['title']}. This is a little time at your own pace. {setup} Move only within a comfortable range. Stop if you feel pain, dizziness or unwell. Rest or pause whenever you need."
            elif c['kind'] == 'rest':
                words = 'Take a moment to settle. Rest your feet and let your shoulders relax. You can pause for longer. There is no need to keep up with a timer.'
            elif c['kind'] == 'outro':
                words = 'Our time together is coming to a close. Settle into a comfortable position and rest for as long as you need. Thank you for joining me. A little time, at your own pace.'
            else:
                words = c['script'] + '\n\n[REVIEWER: specify the approved repetitions, hold times, return-to-neutral cue and optional rest. Confirm the proposed seated option before recording it.]'
            recording += [f"## {timestamp(c['start'])} · {c['title']} · {c['seconds']} seconds", '', words, '']
        (folder / 'recording-script.md').write_text('\n'.join(recording), encoding='utf-8')
    studio = ROOT / 'workout-studio/src/sessions.json'
    studio.write_text(json.dumps(sessions, indent=2, ensure_ascii=False), encoding='utf-8')
    (OUT / 'index.json').write_text(json.dumps(sessions, indent=2, ensure_ascii=False), encoding='utf-8')
    # Fill reviewable content fields while keeping publication and media gates closed.
    for workout, session in zip(catalogue['workouts'], sessions):
        if workout['review']['status'] != 'DRAFT' or not workout['placeholder']:
            raise ValueError('Refusing to overwrite reviewed catalogue content')
        workout['description'] = f"Authored {workout['durationMinutes']}-minute production draft with timed movement cues, rest windows and a calm finish. Awaiting professional review, filmed demonstration and licensed human narration."
        workout['movements'] = [dict(id=c['movementId'] + '-' + str(i), title=c['title'],
                                     durationSeconds=c['seconds'], narration=c['script'], seatedAlternativeId=None)
                                 for i, c in enumerate(session['chapters']) if c['kind'] == 'movement']
        workout['safetyPrompts'] = SAFETY
        workout['contraindicationNotes'] = ['Individual suitability and contraindications require professional review. No diagnosis-specific prescription is provided.']
        workout['voices'] = [dict(language='en-GB', audioUri=None, captionsUri=None,
                                  script='\n'.join(c['script'] for c in session['chapters']),
                                  humanRecorded=False, approved=False, licenceReference='')]
    catalogue['version'] = max(catalogue['version'], 2)
    (ROOT / 'app/src/main/assets/catalogue-v1.json').write_text(json.dumps(catalogue, indent=2, ensure_ascii=False), encoding='utf-8')
    print(f'Authored {len(sessions)} review timelines, {sum(s["durationSeconds"] for s in sessions) // 60} minutes.')

if __name__ == '__main__': generate()
