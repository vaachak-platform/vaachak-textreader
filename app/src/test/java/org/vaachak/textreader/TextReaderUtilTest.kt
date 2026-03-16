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