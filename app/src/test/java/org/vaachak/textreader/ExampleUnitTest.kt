package org.vaachak.textreader

import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.ByteArrayInputStream
import java.io.InputStream

class TextReaderUtilTest {

    @Test
    fun `readTextFromStream returns correct string from valid stream`() {
        // Arrange
        val expectedText = "Hello World\nThis is a test."
        val inputStream: InputStream = ByteArrayInputStream(expectedText.toByteArray())

        // Act
        val result = TextReaderUtil.readTextFromStream(inputStream)

        // Assert
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
}