# Implementation milestones

Delivery follows four milestones in order. Milestone 1 is the current focus. Existing foundations for later milestones remain available, but implementation alone does not establish milestone completion.

Status recorded on 12 October 2026: engineering preview builds successfully; 15 automated tests pass. No connected Fire TV, approved workout media, live billing configuration or human-reviewed translations are available. See [verification](VERIFICATION.md) for evidence and its limits.

| Milestone | Main deliverable | Current status |
| --- | --- | --- |
| 1. Functional MVP | Fire TV navigation, workout player, free sessions, accessibility | In progress: foundations implemented; approved free sessions and device verification outstanding |
| 2. Premium experience | Programme library, progress, voice narration, subscriptions | Foundations implemented; reviewed programmes, licensed narration and verified commerce outstanding |
| 3. Global launch assets | Localisation, human-reviewed translations, premium screenshots, SEO | Draft pipeline and assets prepared; human review and final application captures outstanding |
| 4. Release readiness | Real-device testing, billing verification, security checks, Amazon submission package | Build evidence and checklists prepared; release verification and submission outstanding |

## 1. Functional MVP — current focus

Complete the free experience before expanding premium scope:

- Verify home → browse → detail → safety acknowledgement → approved free workout → completion using a Fire TV remote.
- Verify player pause, resume, replay, media keys, lifecycle interruption, audio focus and natural-end completion with real approved media.
- Provide professionally reviewed free sessions with documented footage, narration and caption rights. Current draft records must remain blocked from playback.
- Verify visible focus, predictable D-pad/Back navigation, readable text at every supported size, captions and accessible labels on Fire TV.
- Confirm local favourites, preferences and resume behaviour across restart; validate operation without network access for bundled sessions.

Acceptance: the free journey works on a physical target Fire TV with approved content, accessibility checks pass, and results are recorded in the QA evidence. A successful build or JVM-rendered screenshot does not replace these checks.

Immediate dependency: obtain professionally reviewed content and licensed media through [the content workflow](CONTENT_WORKFLOW.md). The owner confirmed these are not currently available. Software fixes and test preparation can continue while this dependency is resolved; draft workouts must not be presented as approved exercise.

## 2. Premium experience

Begin milestone acceptance after the functional MVP passes:

- Populate the programme library with reviewed sessions and verify programme navigation and progression.
- Verify completion history, progress, repeat sessions, resume state and local deletion with actual playback.
- Integrate licensed human narration and captions; verify mute, speed and synchronisation on-device.
- Configure Amazon products, authentication and server receipt verification. Verify purchase, restore, renewal, cancellation, expiry and failure handling in the supported Amazon test environment.
- Display store-provided prices and grant access only after verified entitlement checks.

Acceptance: programmes and progress work end to end, narration passes media QA, and billing evidence demonstrates correct access across the subscription lifecycle. See [billing integration requirements](BILLING.md). Production sales remain disabled until these conditions pass.

## 3. Global launch assets

- Complete supported locale resources and listings, using the glossary and translation memory pipeline.
- Obtain human review of every translated UI string, safety statement, caption, narration script and listing. Draft translation files are not approval records.
- Test text expansion, remote navigation and accessibility in each launch locale.
- Capture premium screenshots from the final application with approved content and accurately represented features.
- Finalise store descriptions, discoverability keywords and any agreed public website metadata. Confirm rights for every launch asset.

Acceptance: launch locales have recorded human approval and application QA; final screenshots and listing copy match the release candidate. Existing English drafts, AI brand imagery and JVM captures are review material, not a completed global launch package. See [localisation](../localisation/README.md) and [screenshot requirements](../store/SCREENSHOTS.md).

## 4. Release readiness

- Execute the device matrix on physical supported Fire TVs, including remote, accessibility, playback, interruption, offline and performance checks.
- Repeat billing verification against the release candidate and confirm backend entitlement and receipt-verification behaviour.
- Complete security review of the application and any production backend, including catalogue signature enforcement, secrets, network configuration, privacy and deletion. A dependency scan alone is insufficient.
- Resolve release gates: approved content, rights, human-reviewed translations, legal documents, signing identity and required Amazon configuration.
- Assemble the signed APK, verified listing assets, privacy/support links, compatibility details and test evidence into the Amazon submission package.

Acceptance: all applicable [release checklist](RELEASE_CHECKLIST.md) items have evidence and the signed candidate passes the release content validator. Submit only after the release package is complete and publication is authorised. No Amazon submission has occurred.

## Working rule

Prioritise unresolved acceptance work in the current milestone. Carry useful later foundations forward without treating them as completed deliverables. Record newly verified results in [verification](VERIFICATION.md), and keep [known limitations](KNOWN_LIMITATIONS.md) aligned with the release candidate.
