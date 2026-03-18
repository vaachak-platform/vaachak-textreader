# Vaachak Text Reader 📖

A professional-grade, open-source Android text-to-speech (TTS) and optical character recognition (OCR) application. Built with **Jetpack Compose** and **Google ML Kit**, Vaachak focuses on accessibility, seamless on-device translation, and real-time text scanning.

## 🚀 What's New in v1.3.0 (Phase 3: Vision & Refactor)

### ✨ Additions
- **Live Camera OCR:** Integrated Android CameraX with Google ML Kit for real-time text extraction.
- **Gallery OCR:** Users can now select screenshots or photos from their gallery to extract text.
- **Devanagari Script Support:** Added a dynamic script toggle allowing the OCR engine to read both Latin (English, Spanish) and Devanagari (Hindi, Marathi) scripts.
- **Live Magnifier:** Added a 1x-4x zoom slider and pinch-to-zoom support directly in the camera view to help users read small menus or fine print.
- **Minimalist Adaptive UI:** Upgraded to a system-aware Material 3 design. Heavy text buttons were replaced with a sleek, icon-driven interface that seamlessly transitions between Light and Dark modes.
- **Google Translate-Style Controls:** Implemented independent source/target translation dropdowns with a quick-swap button and a top-bar OCR language selector.

### 🗑️ Deletions & Cleanup
- **Pruned Unsupported Languages:** Removed Bengali, Gujarati, Kannada, Tamil, Telugu, and Urdu from the active list to strictly align with what Google ML Kit can support offline.
- **Removed Bulky UI Elements:** Dropped solid-color default Material buttons in favor of transparent, outlined components for a lighter footprint.
- **Reduced Cognitive Complexity:** Refactored massive Composable functions into isolated, stateless widgets (`SharedComponents.kt`, `ScannerTopBar`, etc.) for strict Clean Architecture compliance.

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