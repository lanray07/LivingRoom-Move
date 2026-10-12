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

The first [GitHub run](https://github.com/lanray07/LivingRoom-Move/actions/runs/38287282330) failed in SDK setup before app checks: the action's default package list requested the unavailable `tools` package. Commit `58d7e31` explicitly requests `platform-tools` and retains reports on failures. The [replacement run](https://github.com/lanray07/LivingRoom-Move/actions/runs/38289231473) passed SDK setup in both jobs and reached build/emulator execution; its final result was pending at this record's update. Emulator execution is not physical Fire TV evidence.

The [free-session production brief](FREE_SESSION_PRODUCTION_BRIEF.md) specifies commissioning scope and media/review handover. It supplies no exercise instructions or fabricated approval.

## Current output integrity

- Debug APK SHA256: `57d4e6a7409ae5b11602c43b12bf60bcbad2e3793108a26ff78e34877f4b5414`
- Unsigned release APK SHA256: `257599dc933900e1c6bd47d6c104b98c4ab5296e8bec71a32f83efacb75c24da`

The debug technical audio is excluded from release assets. All ten exercise records remain DRAFT placeholders. Purchase calls are disabled. Legal and store copy are marked for review. The final listing draft is 192 UTF-8 bytes short / 1,166 characters long, below the researched description limits. Exported Fire TV icon/background and five opaque review captures have the documented dimensions; no fabricated exercise screen is included.

Build/test logs and XML reports are available in the local `artifacts/`, `core/build/test-results/` and `app/build/test-results/` folders. A compact evidence copy is included alongside packaged deliverables. Consult `KNOWN_LIMITATIONS.md` and `RELEASE_CHECKLIST.md` before proceeding.
