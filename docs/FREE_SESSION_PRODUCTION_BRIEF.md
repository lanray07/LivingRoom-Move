# Free-session production brief

Status: procurement and production specification only. No exercise instructions or professional approval are supplied by this document. The owner has no reviewed/licensed workout media yet. Do not record proposed movements until the appointed professional has approved the session plan.

## First delivery

Produce a small free library before premium programmes. Proposed commissioning scope, subject to professional review:

| Session | Target length | Intended catalogue category | Review decision required |
| --- | --- | --- | --- |
| A gentle seated start | 5 minutes | Chair / seated | Audience suitability, movements, pacing and safety script |
| Seated everyday mobility | 10 minutes | Mobility / seated | Range, alternatives, suitability and rest intervals |
| Everyday movement | 15 minutes | Mobility / mixed | Whether standing content belongs in the first free release; seated alternatives and support requirements |

The professional may change or reject any proposed scope. Session names and lengths describe a product request, not a prescription. If the professional chooses a different duration, update catalogue validation and UI filters before integration.

## Responsibilities and review evidence

- Owner: appoint the reviewer and production team, agree budget and distribution scope, confirm rights, and retain signed records.
- Exercise professional: document qualifications and scope; approve audience suitability, the complete movement plan, alternatives, timing, contraindication notes and safety prompts. Review the final video, narration and captions together, including slower playback.
- Production team: record the approved demonstration and human narration, prepare timed captions, and provide model/performer releases and distribution licences.
- Engineering: validate metadata, media files, offline behaviour and player controls; preserve the publication gate until all records are complete.

No external contact or purchase has been made. Credentials, insurance and contractual terms require owner verification; this brief does not certify them.

## Deliverables per session

| Item | Required delivery |
| --- | --- |
| Silent demonstration | Landscape MP4, preferably 1920×1080 H.264; remove all original audio tracks, keep the full body and any support equipment visible, and avoid small embedded text |
| Human narration | Separate audio track matched to the video timeline; English UK first, no background music, clear cues and reviewed safety wording |
| Captions | UTF-8 WebVTT, accurate words and timing, readable line breaks; include meaningful audio cues where applicable |
| Session metadata | Stable ID, title, description, duration, posture, category, ordered movements with durations and narration, reviewed alternatives, safety prompts and contraindication notes |
| Professional approval | Named reviewer, qualifications, review date, approved version and signed evidence reference covering the final assets |
| Rights | Footage and narration licence references, performer releases, territories/languages, offline permission, term/expiry and retained supporting records |
| Integrity inventory | Final filenames, byte sizes, durations and SHA-256 checksums; no unexplained changes after approval |

MP4/H.264 is a proposed production format; validate the final encode on the supported device matrix before acceptance. Video, audio and captions must share one timeline. Include introduction, transitions, rests and ending within the stated session duration; do not pad missing approved content with improvised instructions.

Suggested handover folder: `content-delivery/<session-id>/<version>/`, containing `video.mp4`, `narration-en-GB.wav`, `captions-en-GB.vtt`, `metadata.json` and `inventory.json`. Keep signed professional and contractual records in the owner's controlled evidence store, with references in metadata rather than private records committed to GitHub.

## Acceptance and integration

1. Professional signs off the exact final assets and scripts; owner confirms rights cover intended distribution and bundled offline use.
2. Engineering checks file presence, decode, durations, caption timing and references against the handover inventory. Capture any difference and obtain renewed approval for content changes.
3. Map metadata to [the catalogue schema](../core/src/main/kotlin/uk/co/livingroommove/core/Content.kt). Supply approved narration and captions in the workout's language, and set the licence expiry where applicable.
4. Set approval fields and remove the placeholder flag only after evidence exists. Run the catalogue validator and publication gate tests. Release validation still fails while other bundled catalogue records remain placeholders; exclude unfinished records from the release catalogue rather than approving them artificially.
5. Verify video/narration synchronisation, captions, speed, pause/resume/replay, media keys, interruptions, natural-end completion and offline playback on physical target Fire TV hardware.

Final acceptance requires professional/rights records and device evidence. Automated checks can establish file and software correctness but cannot establish exercise safety or licence ownership. See [content workflow](CONTENT_WORKFLOW.md) and [Milestone 1 criteria](IMPLEMENTATION_PLAN.md).
