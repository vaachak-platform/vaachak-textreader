package org.vaachak.textreader

import java.io.BufferedReader
import java.io.InputStream

object TextReaderUtil {
    /**
     * Reads text from an InputStream.
     * Extracted from Android context to allow pure JVM unit testing.
     */
    fun readTextFromStream(inputStream: InputStream?): String {
        if (inputStream == null) return "Error: File stream is null."

        val stringBuilder = java.lang.StringBuilder()
        try {
            inputStream.bufferedReader().use { reader ->
                var line: String? = reader.readLine()
                while (line != null) {
                    stringBuilder.append(line).append("\n")
                    line = reader.readLine()
                }
            }
        } catch (e: Exception) {
            return "Error reading file: ${e.message}"
        }
        return stringBuilder.toString().trim()
    }
}