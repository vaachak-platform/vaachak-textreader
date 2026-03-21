# Vaachak Text Reader 📖

[![Android CI](https://img.shields.io/github/actions/workflow/status/vaachak-platform/vaachak-textreader/android.yml?branch=main&label=build&logo=github)](https://github.com/vaachak-platform/vaachak-textreader/actions)
[![Latest Release](https://img.shields.io/github/v/release/vaachak-platform/vaachak-textreader?logo=github)](https://github.com/vaachak-platform/vaachak-textreader/releases)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0+-blueviolet.svg?logo=kotlin)](https://kotlinlang.org)
[![Android API](https://img.shields.io/badge/API-24%2B-brightgreen.svg?logo=android)](https://developer.android.com)
[![License: MIT](https://img.shields.io/github/license/vaachak-platform/vaachak-textreader)](https://opensource.org/licenses/MIT)

A professional-grade, open-source Android text-to-speech (TTS) and optical character recognition (OCR) application. Built with **Jetpack Compose** and **Google ML Kit**, Vaachak focuses on accessibility, seamless on-device translation, and real-time text scanning.

## 🚀 What's New in v1.4.0 (System Integration & Audio Export)

### ✨ Additions
- **Deep OS Integration ("Read in Vaachak"):** Vaachak now natively integrates into the Android OS context menu. Highlight text in any app (like Chrome or WhatsApp), tap the three dots, and select "Read in Vaachak" to instantly launch the reader.
- **Global Share Target:** Added support for `ACTION_SEND` intents, allowing users to send text directly to Vaachak from the standard Android Share sheet.
- **Audio File Export:** Users can now generate and save high-quality `.wav` audio files of their TTS dictations directly to their device, utilizing the native Android file picker to choose the save destination.
- **Dynamic Playback UI:** The media controls now react in real-time to the TTS engine state. The Play and Export buttons automatically disable during playback, while the Stop button highlights to provide immediate visual feedback.
- **Local Text File Parsing:** Fully wired the document picker to allow users to select and load local `.txt` files directly from their device storage.

### 🐛 Fixes & Polish
- **Voice Label Formatting:** Fixed a string parsing bug in the `VoiceNameMapper` to cleanly display system voice variants without redundant metadata tags.
- **Robust UI Testing:** Added comprehensive instrumented tests to verify the new dynamic playback button states and intent-handling logic.

---

## ⏪ Previous Release: v1.3.0 (Vision & Refactor)

### ✨ Additions
- **Live Camera & Gallery OCR:** Real-time text extraction using CameraX, plus the ability to parse text from saved images.
- **Devanagari Script Support:** Dynamic script toggling allows the OCR engine to read both Latin (English, Spanish) and Devanagari (Hindi, Marathi) scripts.
- **Live Magnifier:** A 1x-4x zoom slider and pinch-to-zoom support directly in the camera view.
- **Minimalist Adaptive UI:** Upgraded to an icon-driven, system-aware Material 3 design for seamless Light/Dark mode transitions.

### 🗑️ Deletions & Cleanup
- **Pruned Unsupported Languages:** Removed languages not natively supported by offline ML Kit models to ensure complete offline reliability.
- **Reduced Cognitive Complexity:** Extracted complex Composables into isolated, stateless widgets for strict Clean Architecture compliance.

---

## ⚠️ Current OCR Limitations (Edge vs. Cloud)

Vaachak is intentionally designed as an **offline-first, privacy-centric application**. Because it uses Google ML Kit's on-device models rather than cloud APIs, it has the following known limitations:

1. **Script Restrictions:** On-device OCR is currently limited to Latin and Devanagari scripts. Other Indian languages require cloud-based Vision APIs.
2. **Raw Output Accuracy:** Unlike Google Lens, which uses massive server-grade neural networks and LLMs for contextual post-processing, Vaachak outputs the raw string exactly as the edge model sees it. It may occasionally struggle with blurry text, low contrast, or similar-looking characters (e.g., confusing "5" and "S").
3. **Complex Layouts:** The current engine reads top-to-bottom, left-to-right. It may struggle to properly format complex multi-column documents (like restaurant menus with separated prices) without cloud-based layout-parsing algorithms.

---

## 🛠️ Tech Stack & Architecture

- **Language:** Kotlin 2.0+
- **UI Framework:** Jetpack Compose (Material 3)
- **Camera:** Android CameraX (Lifecycle & View)
- **AI/ML Vision:** Google ML Kit Text Recognition (Latin & Devanagari)
- **AI/ML NLP:** Google ML Kit Natural Language Translation
- **Permissions:** Accompanist Permissions library

## 🧪 Testing & CI/CD

The project enforces strict separation of concerns, allowing for rapid testing:
- **Pure JVM Unit Tests:** Located in `src/test`, testing data models and stream parsing without Android context overhead.
- **Stateless UI Tests:** Compose UI tests in `src/androidTest` that verify isolated components (like toolbars and settings dialogs) without relying on mocked hardware feeds.
- **Automated Pipeline:** Designed to be run via GitHub Actions to build, test, and sign the release APK on every push to the `main` branch.

To run tests locally:
```bash
./gradlew testReleaseUnitTest # Local JVM Tests
./gradlew connectedAndroidTest # UI Tests (Requires Emulator/Device)
```
## 🚀 Getting Started
1. Clone the repository: `git clone https://github.com/vaachak-platform/vaachak-textreader`
2. Open the project in **Android Studio**.
3. Sync Gradle dependencies.
4. Run on an emulator or physical device (API 24+).

## 🚀 Installation

You can download the latest signed APK from the [Releases](https://github.com/vaachak-platform/vaachak-textreader/releases) page.

## 📝 License
This project is licensed under the MIT License.