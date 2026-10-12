# LivingRoom Move

Gentle movement. Right from your living room.

Native Kotlin/Compose TV app for Android-based Amazon Fire TV. **Engineering preview, not ready for sale or exercise use.** Ten catalogue records are explicit placeholders. The owner confirmed there is no reviewed/licensed exercise media. Playback gates prevent drafts from playing; no review, clinical validation, translation approval or hardware QA has been fabricated.

## Delivery milestones

The [four-milestone plan](docs/IMPLEMENTATION_PLAN.md) is the delivery sequence: functional MVP → premium experience → global launch assets → release readiness. **Milestone 1 is in progress.** Approved free workout content and physical Fire TV verification are required before it is complete; later foundations already in the preview do not establish completion of those milestones.

## Build and run

Install JDK 17 and Android SDK platform 36 / build-tools 36.0.0. Set `ANDROID_HOME` or create untracked `local.properties` with `sdk.dir=...`. The checked-in Gradle wrapper downloads Gradle 8.13.

```powershell
.\gradlew.bat :core:test :app:testDebugUnitTest :app:assembleDebug :app:lintDebug
.\gradlew.bat :app:connectedDebugAndroidTest
python tools\validate_release.py
```

macOS/Linux: use `./gradlew` instead. APK: `app/build/outputs/apk/debug/app-debug.apk`. Install with `adb install -r <apk>` and launch `adb shell am start -n uk.co.livingroommove/.MainActivity`. Min SDK 25 excludes Fire OS 5 devices. There are no Google Play Services or Google Play Billing dependencies. Vega OS is outside this Android binary's scope.

Release build: `./gradlew :app:assembleRelease` creates an unsigned APK. Signing keys, release identity, publication and Amazon console changes require owner configuration. The preview package ID is `uk.co.livingroommove`; confirm ownership before publication.

## What is implemented

Remote-first home, sessions, category browsing, favourites, programmes, membership information, progress, consent-based family setup, local deletion and preferences. DataStore keeps state on-device. Adjustable text, captions, synchronised narration/video speed, welcome reminders and English locale selection are available. UI motion is already restrained/static.

Media3 player implements pause/resume/replay, hardware media-key handling, captions, narration muting, progress persistence, natural-end completion and lifecycle/audio-focus handling. Approved videos must have **no original audio**, with separate licensed human-recorded narration and VTT captions. Bundled `asset:///` sessions can work offline; no reviewed session is bundled yet. The debug-only 12-second original audio test is available for transport QA, never counts as exercise and its media/UI entry are excluded from release builds. Playback execution still needs a usable emulator or physical device.

Amazon SDK transport and restoration pagination are isolated behind a fail-closed billing gateway. Sales are deliberately disabled: the app public key, actual subscription SKUs and a verified backend are missing. Proposed prices are labelled planning prices.

Versioned catalogue, publication checks, ECDSA metadata verifier, translation drafts pipeline, original assets, five actual JVM-rendered application captures, listing/legal drafts, manual QA and CI are included. Download management, remote catalogue installation and production commerce are documented integration work, not claimed as shipped.

## Deliverables and evidence

- [Implementation plan](docs/IMPLEMENTATION_PLAN.md), [architecture](docs/ARCHITECTURE.md), [verification](docs/VERIFICATION.md).
- [Official requirements research](docs/OFFICIAL_REQUIREMENTS.md), [billing blockers](docs/BILLING.md), [content review workflow](docs/CONTENT_WORKFLOW.md).
- [Free-session production brief](docs/FREE_SESSION_PRODUCTION_BRIEF.md): commissioning scope, media handover and review evidence for the first free library.
- [Asset manifest](assets/manifest.json), [asset rights](assets/RIGHTS.md), [localisation workflow](localisation/README.md).
- [UK store listing](store/en-GB.json), [screenshot capture plan](store/SCREENSHOTS.md), [privacy draft](docs/PRIVACY_DRAFT.md), [terms draft](docs/TERMS_DRAFT.md).
- [Manual QA](docs/QA.md), [release checklist](docs/RELEASE_CHECKLIST.md), [measurement plan](docs/MEASUREMENT.md), [known limitations](docs/KNOWN_LIMITATIONS.md).

Do not run `tools/scaffold_content.py` over edited professional content: it intentionally regenerates preview fixtures. `python tools/validate_release.py --release` is expected to fail on the current catalogue.
