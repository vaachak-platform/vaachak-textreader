# Vaachak Text Reader (Phase 1) 📱🗣️

A lightweight, modern Android application built with **Kotlin** and **Jetpack Compose** that allows users to extract text from plain files or their clipboard and reads it aloud using the native Android Text-To-Speech (TTS) engine.

## ✨ Features
* **File Reading:** Securely parse `.txt` files using the Android Storage Access Framework (SAF) without requiring intrusive storage permissions.
* **Clipboard Integration:** Instantly paste text directly from the system clipboard.
* **Text-to-Speech (TTS):** Read text aloud with system-level TTS integration, complete with play and stop controls.
* **Modern UI:** Responsive, declarative UI built entirely in Jetpack Compose.

## 🛠️ Tech Stack & Architecture
* **Language:** Kotlin
* **UI Toolkit:** Jetpack Compose (Material Design 3)
* **Architecture:** Separation of concerns (UI layer vs. Utility logic) for testability.
* **Testing:** JUnit4 for pure JVM logic tests.
* **Minimum SDK:** 24 (Android 7.0)
* **Target SDK:** 35 (Android 15)

## 🚀 Getting Started
1. Clone the repository: `git clone https://github.com/vaachak-platform/vaachak-textreader`
2. Open the project in **Android Studio**.
3. Sync Gradle dependencies.
4. Run on an emulator or physical device (API 24+).

## 🧪 Running Tests
Unit tests are included for the core text-parsing utilities to ensure reliability.
Run `./gradlew test` in your terminal or right-click the `test` directory in Android Studio and select **Run Tests**.

## 🔮 Roadmap (Phase 2)
* Integration of **Google ML Kit** for on-device, offline translation.
* Supported language pipeline: English ↔ Hindi, English ↔ Gujarati.

## 📝 License
This project is licensed under the MIT License.