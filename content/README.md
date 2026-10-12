# LivingRoom Move session production

Ten original timed session drafts are authored, totalling 100 minutes. They are **not approved workouts**. All app catalogue entries remain DRAFT/placeholder, with no published footage or licensed human narration. Synthetic review audio never satisfies the human-recorded publication gate.

`drafts/<id>/` contains a timeline, production script, sentence-aligned WebVTT captions and speech cue timing. `drafts/index.json` drives the editable Remotion studio. Local synthetic audio lives under `workout-studio/public/audio/`, excluded from Git and the source archive. Local review-video exports live under `workout-studio/out/`, also excluded from Git. No external media has been copied.

## Research and scope

Each session also includes `recording-script.md`, a proposed client-facing human narration script with warm introductions, movement cues, rests and closing words. Its repetitions and hold times must be specified by the reviewer. This script differs from the synthetic producer review track; approved recording requires a new audio track and newly aligned captions.

Background checks used the NHS [sitting](https://www.nhs.uk/live-well/exercise/sitting-exercises/), [balance](https://www.nhs.uk/live-well/exercise/balance-exercises/) and [flexibility](https://www.nhs.uk/live-well/exercise/flexibility-exercises/) pages, accessed 12 October 2026. These references do not endorse, certify or license this programme. Original concise production cues, adaptations and scheduling are proposed by the authoring process; a qualified professional must approve them. No NHS photos, footage or narration were used.

Timing is an editorial window containing instructions, optional movement and rest. A countdown is not a demand to keep moving or hold a stretch for its whole duration. The reviewer must set appropriate repetitions/holds and approve the final pacing. Standing-to-seated options propose separate seated activities, not equivalent balance training. There is no diagnosis-based prescription, guaranteed outcome or requirement to advance.

## Premium production treatment

- Consistent cream, forest-green and warm-gold palette; large readable headings and cues; generous TV safe areas.
- A clear chapter name, elapsed session indicator, upcoming chapter and short cue. Restrained motion supports reduced-motion preferences.
- Full narrator scripts, separate audio and sentence-aligned caption tracks, with quiet rest windows and no background music masking speech.
- A persistent unreviewed-draft label. Cue-card videos are production reviews, not substitute instructor demonstrations.
- Editable timelines and technical checks for duration, caption bounds, file integrity and publication denial.

## Rebuild

1. `python tools/create_workout_drafts.py` authors known draft fixtures only; never run it over professional edits without reviewing its inputs and changes.
2. On Windows, `powershell -File tools/synthesise_review_narration.ps1` uses Microsoft Hazel Desktop for **synthetic review speech**. No redistribution licence is claimed.
3. `python tools/assemble_review_audio.py` aligns exact sentence audio and emits WAV/VTT inventories.
4. In `workout-studio`, run `npm ci`, then `npm run dev -- --no-open` for editing and preview.
5. Use the review render tools for local exports. Published assets require professional sign-off, human narration, approved instructional footage and verified distribution rights.

See [the production brief](../docs/FREE_SESSION_PRODUCTION_BRIEF.md) for the handover and evidence requirements. Reviewer identity, credentials, dates, evidence references, footage and recording rights remain pending.
