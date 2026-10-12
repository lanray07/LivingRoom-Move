package uk.co.livingroommove

import android.view.KeyEvent
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@OptIn(ExperimentalTestApi::class)
class RemoteJourneyTest {
    @get:Rule val rule = createAndroidComposeRule<MainActivity>()
    private fun awaitText(text: String, substring: Boolean = false) {
        rule.waitUntil(timeoutMillis = 10_000) {
            rule.onAllNodesWithText(text, substring = substring).fetchSemanticsNodes().isNotEmpty()
        }
    }
    @Test fun remoteSelectOpensDraftAndBackReturnsToHome() {
        rule.onNodeWithText("Start a gentle session").assertIsFocused()
        InstrumentationRegistry.getInstrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_DPAD_CENTER)
        awaitText("A gentle seated start", substring = true)
        rule.onNodeWithText("A gentle seated start", substring = true).assertExists()
        rule.onNodeWithText("A gentle seated start", substring = true).performClick()
        rule.onNodeWithText("Save as a favourite").assertExists()
        rule.onNodeWithText("Play free session").assertDoesNotExist()
        InstrumentationRegistry.getInstrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_BACK)
        awaitText("A gentle seated start", substring = true)
        rule.onNodeWithText("A gentle seated start", substring = true).assertExists()
        InstrumentationRegistry.getInstrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_BACK)
        awaitText("Start a gentle session")
        rule.onNodeWithText("Start a gentle session").assertIsFocused()
    }
    @Test fun familySetupRequiresConsent() {
        rule.onNodeWithText("Settings", useUnmergedTree = true).performClick()
        awaitText("Text size: +0%")
        rule.onNode(hasScrollToIndexAction()).performScrollToNode(hasText("Family setup"))
        rule.onNodeWithText("Family setup").performClick()
        awaitText("Yes, help me with my settings")
        rule.onNodeWithText("Yes, help me with my settings").assertExists()
        rule.onNodeWithText("Finish family setup").assertDoesNotExist()
    }
}
