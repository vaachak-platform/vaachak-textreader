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

import android.speech.tts.Voice
import java.util.Locale

object VoiceNameMapper {

    /**
     * Converts raw voice names like "en-au-x-aud-local" into
     * "English (Australia) - Voice 1"
     */
    fun getFriendlyName(voice: Voice): String {
        val locale = voice.locale
        val language = locale.getDisplayLanguage(Locale.US)
        val country = locale.getDisplayCountry(Locale.US)

        // Extract the unique identifier part of the name (e.g., 'aud' or 'network')
        // Most system names look like: en-au-x-aud-local or hi-in-x-hie-network
        val rawName = voice.name.lowercase()
        val variantId = rawName.split("-").lastOrNull()?.replaceFirstChar { it.uppercase() } ?: "Default"

        // Determine Gender if metadata is available (Android 5.0+)
        val gender = when (voice.latency) {
            // Note: Modern engines don't always expose gender directly via API,
            // but we can infer 'Cloud' status which is useful for the user.
            else -> if (voice.isNetworkConnectionRequired) "Cloud" else "Local"
        }

        return buildString {
            append(language)
            if (country.isNotEmpty()) append(" ($country)")
            append(" - $variantId ($gender)")
        }
    }
}

