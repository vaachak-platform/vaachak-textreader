/*
 *
 *  *
 *  *  *  Copyright (c) 2026 Piyush Daiya
 *  *  *  *
 *  *  *  * Permission is hereby granted, free of charge, to any person obtaining a copy
 *  *  *  * of this software and associated documentation files (the "Software"), to deal
 *  *  *  * in the Software without restriction, including without limitation the rights
 *  *  *  * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  *  *  * copies of the Software, and to permit persons to whom the Software is
 *  *  *  * furnished to do so, subject to the following conditions:
 *  *  *  *
 *  *  *  * The above copyright notice and this permission notice shall be included in all
 *  *  *  * copies or substantial portions of the Software.
 *  *  *  *
 *  *  *  * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  *  *  * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  *  *  * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  *  *  * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  *  *  * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  *  *  * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  *  *  * SOFTWARE.
 *  *
 *
 */

package org.vaachak.textreader

import android.speech.tts.TextToSpeech
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
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
    fun app_displays_minimalist_title_and_empty_text_box_on_launch() {
        val mockTts: TextToSpeech = mock(TextToSpeech::class.java)

        composeTestRule.setContent {
            MainScreen(
                ttsEngine = mockTts,
                isTtsReady = true,
                onSpeakText = { _, _ -> }
            )
        }

        // Verify the Phase 3 minimalist branding
        composeTestRule.onNodeWithText("V A A C H A K").assertIsDisplayed()

        // Verify the updated placeholder text exists
        composeTestRule.onNodeWithText("Scan, paste or upload text...").assertIsDisplayed()
    }

    @Test
    fun play_button_is_disabled_initially_and_enables_when_text_is_typed() {
        val mockTts: TextToSpeech = mock(TextToSpeech::class.java)

        composeTestRule.setContent {
            MainScreen(
                ttsEngine = mockTts,
                isTtsReady = true,
                onSpeakText = { _, _ -> }
            )
        }

        // Find the Play button by its content description
        val playButton = composeTestRule.onNodeWithContentDescription("Play")

        // Assert it is disabled when the box is empty
        playButton.assertIsNotEnabled()

        // Find the text field using its placeholder and type into it
        composeTestRule.onNodeWithText("Scan, paste or upload text...")
            .performTextInput("Testing the TTS engine.")

        // Assert the Play button is now enabled
        playButton.assertIsEnabled()
    }

    @Test
    fun clicking_settings_icon_opens_audio_settings_dialog() {
        val mockTts: TextToSpeech = mock(TextToSpeech::class.java)

        composeTestRule.setContent {
            MainScreen(
                ttsEngine = mockTts,
                isTtsReady = true,
                onSpeakText = { _, _ -> }
            )
        }

        // The dialog shouldn't exist yet
        composeTestRule.onNodeWithText("Audio Settings").assertDoesNotExist()

        // Click the settings icon
        composeTestRule.onNodeWithContentDescription("Settings").performClick()

        // Verify the Phase 3 dialog appears
        composeTestRule.onNodeWithText("Audio Settings").assertIsDisplayed()
        composeTestRule.onNodeWithText("Done").assertIsDisplayed()
    }
}