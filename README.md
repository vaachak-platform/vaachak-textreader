# Vaachak Text Reader 📖

A professional-grade, open-source Android text-to-speech application built with **Jetpack Compose** and **Google ML Kit**. Vaachak (meaning "Reader" in Sanskrit) focuses on accessibility and seamless on-device translation for major Indian languages.

## ✨ Key Features

- **On-Device Translation:** Powered by Google ML Kit, support for English ➔ Hindi, Bengali, Gujarati, Kannada, Marathi, Tamil, Telugu, and Urdu—completely offline after initial model download.
- **Advanced TTS Controls:** Real-time adjustment of Speech Rate and Pitch, with the ability to select specific voice variants (Local vs. Cloud).
- **Modern UI/UX:** Built entirely with Jetpack Compose, featuring an edge-to-edge Material 3 design, automatic dark/light mode, and streamlined controls.
- **File & Clipboard Integration:** Seamlessly parse local `.txt` files using the Android Storage Access Framework (SAF) or read directly from the system clipboard.

## 🛠️ Tech Stack & Architecture
* **Language:** Kotlin
- **UI Framework:** Jetpack Compose (Material 3)
- **AI/ML:** Google ML Kit (Natural Language Translation)
- **CI/CD:** GitHub Actions (Automated build, test, and release signing)
- **Testing:** JUnit 4, Mockito, and Compose UI Test
* **Minimum SDK:** 24 (Android 7.0)
* **Target SDK:** 35 (Android 15)

## 🏗️ Architecture & Best Practices

- **Clean Architecture:** UI logic is decoupled from business logic using extracted Composables and utility classes.
- **Resource Management:** Safe handling of `InputStream` and memory-efficient `mutableFloatStateOf` for reactive state.
- **Automated Pipeline:** Every release is automatically built, tested, signed, and published via GitHub Actions.

## 🧪 Testing

The project includes a comprehensive testing suite:
- **Unit Tests:** Located in `src/test`, verifying core utility logic and locale mapping.
- **Instrumented Tests:** Located in `src/androidTest`, verifying UI interactions and component states using Compose Rule and Mockito.

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