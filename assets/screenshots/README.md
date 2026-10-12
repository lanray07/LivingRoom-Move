# Captured application UI — engineering previews

`jvm/` contains five 1920×1080 captures of the actual Kotlin application using Robolectric Android native graphics and Roborazzi. Screens were navigated by the rendered journey test; they are not painted mockups, and no finished exercise playback was fabricated.

These are **not physical Fire TV screenshots**. They include explicit preview/draft-content notices and require replacement/review for store submission after professional content, billing and hardware gates pass. The test config uses API34 at 960×540 dp / xhdpi. Amazon classes are instrumented in the JVM test to handle SDK stack-map bytecode incompatibility; this does not validate Amazon commerce.

Reproduce: `./gradlew :app:testDebugUnitTest`. Captures are generated as part of the test, using the same application code. See `store/SCREENSHOTS.md` for the physical-device capture plan and reviewed headline requirements.

`review/` contains identical captures exported to opaque 24-bit PNG with `python tools/export_screenshots.py`; the originals are preserved in `jvm/`. `manifest.json` records size, source, hashes and review status. No headline or feature screen has been composited into these captures.
