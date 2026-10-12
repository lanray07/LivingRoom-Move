# Verification record — 12 October 2026

This is an engineering preview. No professional exercise review, translation certification, live commerce transaction, physical Fire TV test or store submission is claimed.

## Initial baseline checks

`gradlew.bat :core:test :app:testDebugUnitTest :app:assembleDebug :app:assembleRelease :app:lintDebug :app:assembleDebugAndroidTest --console=plain`

**BUILD SUCCESSFUL in 3m 4s.** Debug APK, unsigned minified release APK and Android instrumentation-test APK were generated from the final application source. Java 17, Android platform 36/build tools 36.0.0, Gradle 8.13 and AGP 8.11.1 were used.

| Check | Exact result |
|---|---|
| Domain ContentTest | 7 tests, 0 failures |
| Domain SignedCatalogueTest | 3 tests, 0 failures |
| Android JVM rendered journey | 1 test, 0 failures |
| Python translation contract | 4 tests, 0 failures |
| Preview catalogue/resource validation | Passed |
| Release content validation | Expected exit 1: `chair-introduction: placeholder cannot ship` |
| Android lint | 0 errors, 24 warnings |
| Device instrumentation | Compiled; not executed locally |
| CI workflows | See follow-up record below |

The rendered journey used the real MainActivity/Compose UI under Robolectric API34/native graphics at 1920×1080. It verified initial focus, remote Select, catalogue opening, draft playback denial, return focus and consent before family setup. Five captures were visually inspected/exported for engineering review. SDK package instrumentation fixes Amazon receiver stack-map compatibility inside this JVM harness only; it does not establish commerce/device compatibility.

The local emulator failed to start with hardware acceleration because the Android emulator hypervisor driver is unavailable. A software-emulation attempt produced no usable adb device. No hardware driver settings were changed. Physical Fire TV tests and playback decoder tests remain outstanding. No approved workout media was supplied; the owner explicitly confirmed this.

## Dependency advisory query

Resolved `releaseRuntimeClasspath` was exported and queried through [OSV querybatch](https://google.github.io/osv.dev/post-v1-querybatch/). **123 unique Maven packages, 0 indexed advisory matches.** This covers indexed metadata for the resolved inventory, including BOM/constraints; it does not analyse bundled SDK internals, unindexed vulnerabilities or the application for security flaws. No broad security-audit claim is made. `tools/scan_dependencies.py` fails on incomplete responses/pagination and on matches.

The Amazon 3.0.9 SDK emits D8/R8 stack-map warnings; the final release build succeeds despite them. Lint warnings include newer dependency versions, deliberate TV landscape orientation, unused strings/resources and a KTX suggestion. The tested baseline is pinned, rather than silently updated during release work. Native-symbol stripping warnings preserve the libraries unchanged. Hardware/R8 testing of Amazon SDK functionality is still required.

## Milestone 1 follow-up

The native JVM journey now also verifies explicit family consent, all three text sizes, favourite add/remove and cancellation of local-data deletion. Preference updates are awaited through UI state rather than assumed synchronous. The +30% label now rounds correctly instead of displaying +29% due to floating-point truncation.

The full baseline Gradle command was rerun against these changes: **BUILD SUCCESSFUL in 1m 17s**, including domain/JVM tests, debug and unsigned release APKs, lint and instrumentation compilation. Lint remains 0 errors / 24 warnings. The four Python tests and preview validator also passed. The combined count remains 15 tests; the existing JVM test now covers more interactions. Follow-up log: `artifacts/mvp-followup.log`, included in packaged deliverables.

The first [GitHub run](https://github.com/lanray07/LivingRoom-Move/actions/runs/38287282330) failed in SDK setup before app checks: the action's default package list requested the unavailable `tools` package. Commit `58d7e31` explicitly requests `platform-tools` and retains reports on failures. The [replacement run](https://github.com/lanray07/LivingRoom-Move/actions/runs/38289231473) completed its build job successfully; its emulator job failed before test execution because the downloaded Google APIs system-image archive was invalid.

The workflow now uses an available Android TV API34/x86 image and enables hosted KVM acceleration and hardware keyboard input. The image identifier was checked against Google's Android TV repository metadata; acceleration follows the [emulator runner's documented Linux setup](https://github.com/ReactiveCircus/android-emulator-runner#running-hardware-accelerated-emulators-on-linux-runners). The [Android TV run](https://github.com/lanray07/LivingRoom-Move/actions/runs/38290041449) passed its build job but failed both device UI tests. Assertions immediately after navigation did not find the expected session title or Family setup. The test now awaits the destination UI after navigation; this change compiled successfully in 48s but has not yet passed an emulator rerun. Emulator execution is not physical Fire TV evidence.

The [free-session production brief](FREE_SESSION_PRODUCTION_BRIEF.md) specifies commissioning scope and media/review handover. It supplies no exercise instructions or fabricated approval.

## Current output integrity

- Debug APK SHA256: `a3339558a7bb7d84c18ea2deda210b515c7033c6b7b91fe94dc585bcb29c6fec`
- Unsigned release APK SHA256: `da4f923cc19e60ad4307247ca63a4e7f28336ab9d88fac21a14dcaf43e45df77`

## Workout production drafts

All ten catalogue sessions now have authored production drafts: 100 minutes, 90 chapters and 290 sentence-aligned caption cues. Each has a timed timeline, filming/review script, proposed human-recording script, synthetic review WAV, WebVTT captions and two full-duration 1920×1080 review MP4s (silent and narrated). Repetition and hold-time decisions remain explicit reviewer tasks. These are cue-card production previews with no instructor demonstration. Studio compositions run at 30fps; static-card MP4 exports run at 1fps with a seconds timer.

`python tools/validate_workout_pack.py --media` passed all ten sessions, including uninterrupted duration, caption bounds, audio length, MP4 duration/tracks/resolution and preservation of publication denial. Actual encoded frames from seated, mobility and balance sessions were visually inspected. The review ZIP passed its integrity check and contains a per-file hash manifest. Synthetic Windows review narration has no asserted distribution licence and is excluded from the app and source archive. Human exercise review, licensed human narration and approved filmed instruction are still absent.

The app build, domain and JVM tests passed in 1m 50s after the catalogue update; the four Python translation tests and preview validator also passed (15 tests total). Release content validation still rejects the draft placeholders as intended. App lint evidence above is from the earlier baseline. Studio TypeScript checking passed and the final npm audit reported 0 indexed vulnerabilities; this is a dependency query, not a security audit. Logs are `artifacts/workout-content-build.log`, `artifacts/workout-instrumentation-build.log`, `artifacts/workout-render.log` and `artifacts/workout-studio-audit-final.json`.

The debug technical audio is excluded from release assets. All ten exercise records remain DRAFT placeholders. Purchase calls are disabled. Legal and store copy are marked for review. The final listing draft is 192 UTF-8 bytes short / 1,166 characters long, below the researched description limits. Exported Fire TV icon/background and five opaque review captures have the documented dimensions; no fabricated exercise screen is included.

Build/test logs and XML reports are available in the local `artifacts/`, `core/build/test-results/` and `app/build/test-results/` folders. A compact evidence copy is included alongside packaged deliverables. Consult `KNOWN_LIMITATIONS.md` and `RELEASE_CHECKLIST.md` before proceeding.
