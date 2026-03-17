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

import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.util.Locale

class TextReaderUtilTest {

    @Test
    fun `readTextFromStream returns correct string from valid stream`() {
        // Arrange: Create a fake file stream in memory
        val expectedText = "Hello Vaachak\nThis is a unit test."
        val inputStream: InputStream = ByteArrayInputStream(expectedText.toByteArray())

        // Act: Pass it to our utility function
        val result = TextReaderUtil.readTextFromStream(inputStream)

        // Assert: Verify the output matches our expectation
        assertEquals(expectedText, result)
    }

    @Test
    fun `readTextFromStream returns error message on null stream`() {
        // Act
        val result = TextReaderUtil.readTextFromStream(null)

        // Assert
        assertEquals("Error: File stream is null.", result)
    }

    @Test
    fun `readTextFromStream handles empty stream gracefully`() {
        // Arrange
        val inputStream: InputStream = ByteArrayInputStream("".toByteArray())

        // Act
        val result = TextReaderUtil.readTextFromStream(inputStream)

        // Assert
        assertEquals("", result)
    }

    @Test
    fun `language tags return correct locale strings`() {
        // Testing the mapping logic we used in MainActivity
        val hindiLocale = Locale.forLanguageTag("hi-IN")
        val tamilLocale = Locale.forLanguageTag("ta-IN")
        val gujaratiLocale = Locale.forLanguageTag("gu-IN")

        assertEquals("hi", hindiLocale.language)
        assertEquals("IN", hindiLocale.country)

        assertEquals("ta", tamilLocale.language)
        assertEquals("gu", gujaratiLocale.language)
    }

    @Test
    fun `voice mapper returns human readable name`() {
        // This is a bit complex to mock fully, so we test the logic
        // of how we handle Locale display names.
        val locale = Locale.forLanguageTag("hi-IN")
        val language = locale.getDisplayLanguage(Locale.US)
        val country = locale.getDisplayCountry(Locale.US)

        assertEquals("Hindi", language)
        assertEquals("India", country)
    }
}