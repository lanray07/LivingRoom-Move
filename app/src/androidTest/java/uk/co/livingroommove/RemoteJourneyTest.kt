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
class RemoteJourneyTest {
    @get:Rule val rule = createAndroidComposeRule<MainActivity>()
    @Test fun remoteSelectOpensDraftAndBackReturnsToHome() {
        rule.onNodeWithText("Start a gentle session").assertIsFocused()
        InstrumentationRegistry.getInstrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_DPAD_CENTER)
        rule.onNodeWithText("A gentle seated start", substring = true).assertExists()
        rule.onNodeWithText("A gentle seated start", substring = true).performClick()
        rule.onNodeWithText("Save as a favourite").assertExists()
        rule.onNodeWithText("Play free session").assertDoesNotExist()
        InstrumentationRegistry.getInstrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_BACK)
        rule.onNodeWithText("A gentle seated start", substring = true).assertExists()
        InstrumentationRegistry.getInstrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_BACK)
        rule.onNodeWithText("Start a gentle session").assertIsFocused()
    }
    @Test fun familySetupRequiresConsent() {
        rule.onNodeWithText("Settings", useUnmergedTree = true).performClick()
        rule.onNode(hasScrollToIndexAction()).performScrollToNode(hasText("Family setup"))
        rule.onNodeWithText("Family setup").performClick()
        rule.onNodeWithText("Yes, help me with my settings").assertExists()
        rule.onNodeWithText("Finish family setup").assertDoesNotExist()
    }
}
