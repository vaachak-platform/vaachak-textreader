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

import android.Manifest
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TextSnippet
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.devanagari.DevanagariTextRecognizerOptions
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.util.Locale

class MainActivity : ComponentActivity(), TextToSpeech.OnInitListener {
    private lateinit var tts: TextToSpeech
    private var isTtsReady by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        tts = TextToSpeech(this, this)

        setContent {
            val isDark = isSystemInDarkTheme()
            MaterialTheme(colorScheme = if (isDark) darkColorScheme() else lightColorScheme()) {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    MainScreen(
                        ttsEngine = tts,
                        isTtsReady = isTtsReady,
                        onSpeakText = { text, locale ->
                            if (isTtsReady && text.isNotBlank()) {
                                tts.language = locale
                                tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
                            }
                        }
                    )
                }
            }
        }
    }

    override fun onInit(status: Int) { if (status == TextToSpeech.SUCCESS) isTtsReady = true }
    override fun onDestroy() { tts.stop(); tts.shutdown(); super.onDestroy() }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun MainScreen(ttsEngine: TextToSpeech, isTtsReady: Boolean, onSpeakText: (String, Locale) -> Unit) {
    val context = LocalContext.current
    val (currentText, setCurrentText) = remember { mutableStateOf("") }
    val (currentTtsLocale, setCurrentTtsLocale) = remember { mutableStateOf(Locale.US) }
    val (showTtsSettings, setShowTtsSettings) = remember { mutableStateOf(false) }
    val (isScannerOpen, setScannerOpen) = remember { mutableStateOf(false) }

    // Global OCR Script Toggle State
    val (useDevanagari, setUseDevanagari) = remember { mutableStateOf(false) }

    val cameraPermission = rememberPermissionState(Manifest.permission.CAMERA)

    val fileLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        uri?.let { context.contentResolver.openInputStream(it)?.use { stream -> setCurrentText(TextReaderUtil.readTextFromStream(stream)) } }
    }

    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { safeUri ->
            Toast.makeText(context, "Analyzing image...", Toast.LENGTH_SHORT).show()
            try {
                val image = InputImage.fromFilePath(context, safeUri)
                val options = if (useDevanagari) DevanagariTextRecognizerOptions.Builder().build() else TextRecognizerOptions.DEFAULT_OPTIONS
                val recognizer = TextRecognition.getClient(options)

                recognizer.process(image)
                    .addOnSuccessListener { visionText ->
                        if (visionText.text.isNotBlank()) {
                            setCurrentText(visionText.text)
                        } else {
                            Toast.makeText(context, "No text found in image.", Toast.LENGTH_SHORT).show()
                        }
                        if (isScannerOpen) setScannerOpen(false) // Auto-close scanner if opened from camera view
                    }
                    .addOnFailureListener {
                        Toast.makeText(context, "Failed to recognize text.", Toast.LENGTH_SHORT).show()
                    }
            } catch (e: Exception) {
                Toast.makeText(context, "Error loading image.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    if (isScannerOpen) {
        CameraScannerView(
            isDevanagari = useDevanagari,
            onScriptChange = setUseDevanagari,
            onTextScanned = { setCurrentText(it) },
            onOpenGallery = { galleryLauncher.launch("image/*") },
            onDismiss = { setScannerOpen(false) }
        )
    } else {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("V A A C H A K", style = MaterialTheme.typography.labelLarge.copy(letterSpacing = 4.sp), fontWeight = FontWeight.Light) },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = MaterialTheme.colorScheme.background)
                )
            }
        ) { padding ->
            Column(modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 20.dp, vertical = 10.dp)) {

                // Sleek Main Screen OCR Selector
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.CenterVertically) {
                    var expanded by remember { mutableStateOf(false) }
                    TextButton(onClick = { expanded = true }) {
                        Text(if (useDevanagari) "OCR: Hindi/Marathi" else "OCR: English/Spanish", fontSize = 12.sp)
                        Icon(Icons.Default.ArrowDropDown, null)
                    }
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        DropdownMenuItem(text = { Text("English/Spanish") }, onClick = { setUseDevanagari(false); expanded = false })
                        DropdownMenuItem(text = { Text("Hindi/Marathi") }, onClick = { setUseDevanagari(true); expanded = false })
                    }
                }

                OutlinedTextField(
                    value = currentText,
                    onValueChange = setCurrentText,
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    placeholder = { Text("Scan, paste or upload text...") },
                    shape = MaterialTheme.shapes.large,
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                        focusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    ActionIcon(Icons.Default.ContentPaste, "Paste") {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.primaryClip?.getItemAt(0)?.text?.let { setCurrentText(it.toString()) }
                    }
                    ActionIcon(Icons.AutoMirrored.Filled.TextSnippet, "Files") { fileLauncher.launch(arrayOf("text/plain")) }
                    ActionIcon(Icons.Default.CameraAlt, "Scan") {
                        if (cameraPermission.status.isGranted) setScannerOpen(true) else cameraPermission.launchPermissionRequest()
                    }
                    ActionIcon(Icons.Default.PhotoLibrary, "Gallery") {
                        galleryLauncher.launch("image/*")
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                ProfessionalTranslationRow(currentText) { translated, tag ->
                    setCurrentText(translated)
                    setCurrentTtsLocale(Locale.forLanguageTag(tag))
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { ttsEngine.stop() }, modifier = Modifier.size(60.dp)) {
                        Icon(Icons.Default.StopCircle, "Stop", modifier = Modifier.size(42.dp), tint = MaterialTheme.colorScheme.outline)
                    }
                    IconButton(
                        onClick = { onSpeakText(currentText, currentTtsLocale) },
                        modifier = Modifier.size(80.dp),
                        enabled = isTtsReady && currentText.isNotBlank()
                    ) {
                        Icon(
                            Icons.Default.PlayCircleFilled, "Play",
                            modifier = Modifier.size(72.dp),
                            tint = if (currentText.isNotBlank()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
                        )
                    }
                    IconButton(onClick = { setShowTtsSettings(true) }, modifier = Modifier.size(60.dp)) {
                        Icon(Icons.Default.Tune, "Settings", modifier = Modifier.size(42.dp), tint = MaterialTheme.colorScheme.outline)
                    }
                }
            }
        }
    }

    if (showTtsSettings) {
        TtsSettingsDialog(ttsEngine, currentTtsLocale) { setShowTtsSettings(false) }
    }
}