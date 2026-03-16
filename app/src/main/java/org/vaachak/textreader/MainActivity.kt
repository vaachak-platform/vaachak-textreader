package org.vaachak.textreader

import android.content.ClipboardManager
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.util.Locale

class MainActivity : ComponentActivity(), TextToSpeech.OnInitListener {

    private lateinit var tts: TextToSpeech
    private var isTtsReady = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Enable Edge-To-Edge to prevent UI from hiding behind the system navigation bar
        enableEdgeToEdge()

        tts = TextToSpeech(this, this)

        setContent {
            // Your default theme wrapper automatically handles Light/Dark mode
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TextReaderApp(
                        onSpeakText = { text -> speakOut(text) },
                        ttsEngine = tts
                    )
                }
            }
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts.setLanguage(Locale.US)
            if (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED) {
                isTtsReady = true
            }
        }
    }

    private fun speakOut(text: String) {
        if (isTtsReady && text.isNotEmpty()) {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextReaderApp(onSpeakText: (String) -> Unit, ttsEngine: TextToSpeech) {
    val context = LocalContext.current
    var currentText by remember { mutableStateOf("Your text will appear here...") }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            val inputStream = context.contentResolver.openInputStream(it)
            currentText = TextReaderUtil.readTextFromStream(inputStream)
        }
    }

    // 2. Scaffold provides the professional Top Bar and screen structure
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Vaachak Text Reader",
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        // innerPadding applies the exact spacing needed to clear the top and bottom system bars!
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Text Display Area
            OutlinedTextField(
                value = currentText,
                onValueChange = { currentText = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                label = { Text("Text Content") },
                // Use dynamic theme colors for the text field
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(onClick = {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val pastedText = clipboard.primaryClip?.getItemAt(0)?.text?.toString()
                    if (!pastedText.isNullOrEmpty()) {
                        currentText = pastedText
                    } else {
                        Toast.makeText(context, "No text in clipboard", Toast.LENGTH_SHORT).show()
                    }
                }) {
                    Text("Paste")
                }

                Button(onClick = { filePickerLauncher.launch(arrayOf("text/plain")) }) {
                    Text("Open .txt")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // TTS Buttons
            Button(
                onClick = { onSpeakText(currentText) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Read Aloud")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { ttsEngine.stop() },
                modifier = Modifier.fillMaxWidth(),
                // Uses the dynamic 'error' color standard in Material 3 (red in light mode, dark red/pink in dark mode)
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError
                )
            ) {
                Text("Stop Audio")
            }
        }
    }
}