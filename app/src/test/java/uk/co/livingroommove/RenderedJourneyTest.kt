package uk.co.livingroommove

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.input.key.Key
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import java.io.File

/** Real application composables rendered by Android native graphics in a JVM test.
 * These are engineering captures, not screenshots from physical Fire TV hardware. */
@RunWith(RobolectricTestRunner::class)
@OptIn(ExperimentalTestApi::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [34], qualifiers = "w960dp-h540dp-land-xhdpi", instrumentedPackages = ["com.amazon"])
class RenderedJourneyTest {
    @get:Rule val rule = createAndroidComposeRule<MainActivity>()
    private fun capture(name: String) {
        val folder = File("../assets/screenshots/jvm").apply { mkdirs() }
        rule.onRoot().captureRoboImage(File(folder, "$name.png").absolutePath)
    }
    @Test fun renderHomeBrowseDraftAndConsent() {
        rule.onNodeWithText("Start a gentle session").assertIsFocused()
        capture("01-home-preview")
        rule.onNodeWithText("Start a gentle session").performKeyInput { pressKey(Key.DirectionCenter) }
        rule.onNodeWithText("A gentle seated start", substring = true).assertIsFocused()
        capture("02-sessions-drafts")
        rule.onNodeWithText("A gentle seated start", substring = true).performClick()
        rule.onNodeWithText("Save as a favourite").assertIsFocused()
        rule.onNodeWithText("Play free session").assertDoesNotExist()
        capture("03-draft-detail")
        rule.onNodeWithText("Back to sessions").performScrollTo().performClick()
        rule.onNodeWithText("A gentle seated start", substring = true).assertIsFocused()
        rule.onNodeWithText("Settings", useUnmergedTree = true).performClick()
        capture("04-settings")
        rule.onNode(hasScrollToIndexAction()).performScrollToNode(hasText("Family setup"))
        rule.onNodeWithText("Family setup").performClick()
        rule.onNodeWithText("Yes, help me with my settings").assertIsFocused()
        rule.onNodeWithText("Finish family setup").assertDoesNotExist()
        capture("05-family-consent")
    }
}
