package org.vaachak.textreader

import android.content.ClipboardManager
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.Voice
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import java.util.Locale

// Data class for the translation dropdown
data class TranslationOption(val label: String, val sourceLang: String, val targetLang: String)

class MainActivity : ComponentActivity(), TextToSpeech.OnInitListener {

    private lateinit var tts: TextToSpeech
    private var isTtsReady by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        tts = TextToSpeech(this, this)

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen(
                        ttsEngine = tts,
                        isTtsReady = isTtsReady,
                        onSpeakText = { text, locale -> speakOut(text, locale) }
                    )
                }
            }
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            isTtsReady = true
        }
    }

    private fun speakOut(text: String, locale: Locale) {
        if (isTtsReady && text.isNotEmpty()) {
            val result = tts.setLanguage(locale)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Toast.makeText(this, "Language not supported for TTS on this device.", Toast.LENGTH_SHORT).show()
            } else {
                tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
            }
        } else if (!isTtsReady) {
            Toast.makeText(this, "TTS engine not ready", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        if (::tts.isInitialized) {
            tts.stop()
            tts.shutdown()
        }
        super.onDestroy()
    }
}

// ---------------------------------------------------------------------------
// Extracted Composables for Clean Architecture
// ---------------------------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    ttsEngine: TextToSpeech,
    isTtsReady: Boolean,
    onSpeakText: (String, Locale) -> Unit
) {
    val context = LocalContext.current

    val (currentText, setCurrentText) = remember { mutableStateOf("") }
    val (currentTtsLocale, setCurrentTtsLocale) = remember { mutableStateOf(Locale.US) }
    val (showTtsSettings, setShowTtsSettings) = remember { mutableStateOf(false) }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let { safeUri ->
            context.contentResolver.openInputStream(safeUri)?.use { stream ->
                setCurrentText(TextReaderUtil.readTextFromStream(stream))
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Vaachak Text Reader", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            TextArea(
                text = currentText,
                onTextChange = setCurrentText,
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.height(12.dp))

            InputActionsRow(
                onPasteText = setCurrentText,
                onOpenFile = { filePickerLauncher.launch(arrayOf("text/plain")) },
                context = context
            )

            Spacer(modifier = Modifier.height(12.dp))

            TranslationRow(
                currentText = currentText,
                context = context,
                onTextTranslated = { translatedText, targetLang ->
                    setCurrentText(translatedText)
                    setCurrentTtsLocale(when (targetLang) {
                        TranslateLanguage.HINDI -> Locale.forLanguageTag("hi-IN")
                        TranslateLanguage.BENGALI -> Locale.forLanguageTag("bn-IN")
                        TranslateLanguage.GUJARATI -> Locale.forLanguageTag("gu-IN")
                        TranslateLanguage.KANNADA -> Locale.forLanguageTag("kn-IN")
                        TranslateLanguage.MARATHI -> Locale.forLanguageTag("mr-IN")
                        TranslateLanguage.TAMIL -> Locale.forLanguageTag("ta-IN")
                        TranslateLanguage.TELUGU -> Locale.forLanguageTag("te-IN")
                        TranslateLanguage.URDU -> Locale.forLanguageTag("ur-IN")
                        else -> Locale.US
                    })
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            AudioControlsRow(
                onPlay = { onSpeakText(currentText, currentTtsLocale) },
                onStop = { ttsEngine.stop() },
                onOpenSettings = { setShowTtsSettings(true) },
                isReady = isTtsReady
            )
        }
    }

    if (showTtsSettings) {
        TtsSettingsDialog(
            ttsEngine = ttsEngine,
            currentLocale = currentTtsLocale,
            onDismiss = { setShowTtsSettings(false) }
        )
    }
}

@Composable
fun TextArea(text: String, onTextChange: (String) -> Unit, modifier: Modifier = Modifier) {
    OutlinedTextField(
        value = text,
        onValueChange = onTextChange,
        modifier = modifier.fillMaxWidth(),
        label = { Text("Text Content") },
        trailingIcon = {
            if (text.isNotEmpty()) {
                IconButton(onClick = { onTextChange("") }) {
                    Icon(Icons.Filled.Clear, contentDescription = "Clear Text")
                }
            }
        }
    )
}

@Composable
fun InputActionsRow(onPasteText: (String) -> Unit, onOpenFile: () -> Unit, context: Context) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedButton(
            onClick = {
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val pastedText = clipboard.primaryClip?.getItemAt(0)?.text?.toString()
                if (!pastedText.isNullOrEmpty()) onPasteText(pastedText)
            },
            modifier = Modifier.weight(1f)
        ) { Text("Paste") }

        OutlinedButton(
            onClick = onOpenFile,
            modifier = Modifier.weight(1f)
        ) { Text("Open .txt") }
    }
}

@Composable
fun TranslationRow(
    currentText: String,
    context: Context,
    onTextTranslated: (String, String) -> Unit
) {
    val translationOptions = listOf(
        TranslationOption("English ➔ Hindi", TranslateLanguage.ENGLISH, TranslateLanguage.HINDI),
        TranslationOption("Hindi ➔ English", TranslateLanguage.HINDI, TranslateLanguage.ENGLISH),
        TranslationOption("English ➔ Bengali", TranslateLanguage.ENGLISH, TranslateLanguage.BENGALI),
        TranslationOption("Bengali ➔ English", TranslateLanguage.BENGALI, TranslateLanguage.ENGLISH),
        TranslationOption("English ➔ Gujarati", TranslateLanguage.ENGLISH, TranslateLanguage.GUJARATI),
        TranslationOption("Gujarati ➔ English", TranslateLanguage.GUJARATI, TranslateLanguage.ENGLISH),
        TranslationOption("English ➔ Kannada", TranslateLanguage.ENGLISH, TranslateLanguage.KANNADA),
        TranslationOption("Kannada ➔ English", TranslateLanguage.KANNADA, TranslateLanguage.ENGLISH),
        TranslationOption("English ➔ Marathi", TranslateLanguage.ENGLISH, TranslateLanguage.MARATHI),
        TranslationOption("Marathi ➔ English", TranslateLanguage.MARATHI, TranslateLanguage.ENGLISH),
        TranslationOption("English ➔ Tamil", TranslateLanguage.ENGLISH, TranslateLanguage.TAMIL),
        TranslationOption("Tamil ➔ English", TranslateLanguage.TAMIL, TranslateLanguage.ENGLISH),
        TranslationOption("English ➔ Telugu", TranslateLanguage.ENGLISH, TranslateLanguage.TELUGU),
        TranslationOption("Telugu ➔ English", TranslateLanguage.TELUGU, TranslateLanguage.ENGLISH),
        TranslationOption("English ➔ Urdu", TranslateLanguage.ENGLISH, TranslateLanguage.URDU),
        TranslationOption("Urdu ➔ English", TranslateLanguage.URDU, TranslateLanguage.ENGLISH)
    )

    val (dropdownExpanded, setDropdownExpanded) = remember { mutableStateOf(false) }
    val (selectedTranslation, setSelectedTranslation) = remember { mutableStateOf(translationOptions[0]) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.weight(1.2f)) {
            OutlinedButton(
                onClick = { setDropdownExpanded(true) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(selectedTranslation.label, maxLines = 1)
                Icon(Icons.Filled.ArrowDropDown, contentDescription = "Select Language")
            }
            DropdownMenu(
                expanded = dropdownExpanded,
                onDismissRequest = { setDropdownExpanded(false) }
            ) {
                translationOptions.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option.label) },
                        onClick = {
                            setSelectedTranslation(option)
                            setDropdownExpanded(false)
                        }
                    )
                }
            }
        }

        Button(
            onClick = {
                if (currentText.isBlank()) return@Button
                Toast.makeText(context, "Translating... (May require download)", Toast.LENGTH_SHORT).show()

                val options = TranslatorOptions.Builder()
                    .setSourceLanguage(selectedTranslation.sourceLang)
                    .setTargetLanguage(selectedTranslation.targetLang)
                    .build()
                val translator = Translation.getClient(options)
                val conditions = DownloadConditions.Builder().build()

                translator.downloadModelIfNeeded(conditions).addOnSuccessListener {
                    translator.translate(currentText).addOnSuccessListener { translatedText ->
                        onTextTranslated(translatedText, selectedTranslation.targetLang)
                        translator.close()
                    }
                }.addOnFailureListener {
                    Toast.makeText(context, "Translation Failed", Toast.LENGTH_SHORT).show()
                    translator.close()
                }
            },
            modifier = Modifier.weight(1f)
        ) {
            Icon(Icons.Filled.Translate, contentDescription = "Translate")
            Spacer(Modifier.width(4.dp))
            Text("Translate")
        }
    }
}

@Composable
fun AudioControlsRow(onPlay: () -> Unit, onStop: () -> Unit, onOpenSettings: () -> Unit, isReady: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = onPlay,
            modifier = Modifier.weight(1f),
            enabled = isReady,
            contentPadding = PaddingValues(vertical = 12.dp)
        ) {
            Icon(Icons.Filled.PlayArrow, contentDescription = "Play")
            Spacer(Modifier.width(4.dp))
            Text("Play")
        }

        Button(
            onClick = onStop,
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
            contentPadding = PaddingValues(vertical = 12.dp)
        ) {
            Icon(Icons.Filled.Stop, contentDescription = "Stop")
            Spacer(Modifier.width(4.dp))
            Text("Stop")
        }

        FilledIconButton(
            onClick = onOpenSettings,
            modifier = Modifier.size(48.dp)
        ) {
            Icon(Icons.Filled.Settings, contentDescription = "TTS Settings")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TtsSettingsDialog(
    ttsEngine: TextToSpeech,
    currentLocale: Locale,
    onDismiss: () -> Unit
) {
    val (ttsSpeechRate, setTtsSpeechRate) = remember { mutableFloatStateOf(1.0f) }
    val (ttsPitch, setTtsPitch) = remember { mutableFloatStateOf(1.0f) }

    val (availableVoices, setAvailableVoices) = remember { mutableStateOf<List<Voice>>(emptyList()) }
    val (selectedVoice, setSelectedVoice) = remember { mutableStateOf<Voice?>(null) }
    val (dropdownExpanded, setDropdownExpanded) = remember { mutableStateOf(false) }

    LaunchedEffect(currentLocale) {
        try {
            val allVoices = ttsEngine.voices ?: emptySet()
            val filteredVoices = allVoices
                .filter { it.locale.language == currentLocale.language }
                .sortedBy { it.name }

            setAvailableVoices(filteredVoices)
            setSelectedVoice(ttsEngine.voice)
        } catch (_: Exception) { }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("TTS Settings") },
        text = {
            Column {
                Text("Speech Rate: ${String.format(Locale.getDefault(), "%.1f", ttsSpeechRate)}x")
                Slider(
                    value = ttsSpeechRate,
                    onValueChange = setTtsSpeechRate,
                    onValueChangeFinished = { ttsEngine.setSpeechRate(ttsSpeechRate) },
                    valueRange = 0.5f..2.0f
                )
                Spacer(modifier = Modifier.height(16.dp))

                Text("Pitch: ${String.format(Locale.getDefault(), "%.1f", ttsPitch)}x")
                Slider(
                    value = ttsPitch,
                    onValueChange = setTtsPitch,
                    onValueChangeFinished = { ttsEngine.setPitch(ttsPitch) },
                    valueRange = 0.5f..2.0f
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (availableVoices.isNotEmpty()) {
                    Text("Voice Variant:")
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedButton(
                            onClick = { setDropdownExpanded(true) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            val displayName = selectedVoice?.let { VoiceNameMapper.getFriendlyName(it) } ?: "Default Voice"
                            Text(displayName, maxLines = 1)
                            Spacer(Modifier.weight(1f))
                            Icon(Icons.Filled.ArrowDropDown, contentDescription = "Select Voice")
                        }
                        DropdownMenu(
                            expanded = dropdownExpanded,
                            onDismissRequest = { setDropdownExpanded(false) }
                        ) {
                            availableVoices.forEach { voice ->
                                DropdownMenuItem(
                                    text = { Text(VoiceNameMapper.getFriendlyName(voice)) },
                                    onClick = {
                                        setSelectedVoice(voice)
                                        ttsEngine.voice = voice // Modern property access
                                        setDropdownExpanded(false)
                                    }
                                )
                            }
                        }
                    }
                } else {
                    Text(
                        text = "No alternate voices available for this language.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Done") }
        }
    )
}