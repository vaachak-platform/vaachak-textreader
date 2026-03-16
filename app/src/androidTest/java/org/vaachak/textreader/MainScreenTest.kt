package org.vaachak.textreader

import android.speech.tts.TextToSpeech
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock

@RunWith(AndroidJUnit4::class)
class MainScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun app_displays_main_title_and_empty_text_box_on_launch() {
        val mockTts: TextToSpeech = mock(TextToSpeech::class.java)

        composeTestRule.setContent {
            MainScreen(
                ttsEngine = mockTts,
                isTtsReady = true,
                onSpeakText = { _, _ -> }
            )
        }

        composeTestRule.onNodeWithText("Vaachak Text Reader").assertIsDisplayed()
        composeTestRule.onNodeWithText("Text Content").assertIsDisplayed()
    }

    @Test
    fun typing_text_shows_clear_button_and_clicking_it_clears_text() {
        val mockTts: TextToSpeech = mock(TextToSpeech::class.java)

        composeTestRule.setContent {
            MainScreen(
                ttsEngine = mockTts,
                isTtsReady = true,
                onSpeakText = { _, _ -> }
            )
        }

        composeTestRule.onNodeWithText("Text Content").performTextInput("Hello")
        composeTestRule.onNodeWithText("Hello").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Clear Text").assertIsDisplayed().performClick()
        composeTestRule.onNodeWithText("Hello").assertDoesNotExist()
    }

    @Test
    fun clicking_settings_icon_opens_tts_dialog() {
        val mockTts: TextToSpeech = mock(TextToSpeech::class.java)

        composeTestRule.setContent {
            MainScreen(
                ttsEngine = mockTts,
                isTtsReady = true,
                onSpeakText = { _, _ -> }
            )
        }

        composeTestRule.onNodeWithText("TTS Settings").assertDoesNotExist()
        composeTestRule.onNodeWithContentDescription("TTS Settings").performClick()
        composeTestRule.onNodeWithText("TTS Settings").assertIsDisplayed()
        composeTestRule.onNodeWithText("Done").assertIsDisplayed()
    }
}