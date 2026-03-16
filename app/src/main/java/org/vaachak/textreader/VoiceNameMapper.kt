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

