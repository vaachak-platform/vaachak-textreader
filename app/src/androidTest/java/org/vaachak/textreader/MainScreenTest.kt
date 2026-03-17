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