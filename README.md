<div align="center">

  <img src="docs/images/banner.png" alt="Zad Al-Mu'min Banner" width="100%" />

  <br/><br/>

  <a href="https://github.com/ElsayedMagdy122/Zad-El-Momen">
    <img src="docs/images/app_icon.png" alt="Zad Al-Mu'min Logo" width="96" height="96" />
  </a>

  # Zad Al-Mu'min | زاد المؤمن

  **A modern, offline-first Islamic companion featuring on-device AI Quran recitation verification, precise prayer times, and Jetpack Compose UI.**

  [**العربية**](README.ar.md) • [**Features**](#-features) • [**Screenshots**](#-screenshots) • [**Tech Stack**](#-tech-stack) • [**Architecture**](#-architecture) • [**Quickstart**](#-quickstart)

  <br/>

  [![Platform](https://img.shields.io/badge/Platform-Android%208.0%2B-3DDC84?style=flat-square&logo=android&logoColor=white)](https://developer.android.com)
  [![Kotlin](https://img.shields.io/badge/Kotlin-2.0.21-7F52FF?style=flat-square&logo=kotlin&logoColor=white)](https://kotlinlang.org)
  [![Compose](https://img.shields.io/badge/UI-Jetpack%20Compose-4285F4?style=flat-square&logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)
  [![Clean Architecture](https://img.shields.io/badge/Architecture-Clean%20%2B%20MVI-F44336?style=flat-square)](#-architecture)
  [![Koin](https://img.shields.io/badge/DI-Koin%204.x-B89B00?style=flat-square)](https://insert-koin.io/)
  [![CI](https://img.shields.io/github/actions/workflow/status/ElsayedMagdy122/Zad-El-Momen/build-ci.yml?branch=main&label=CI&style=flat-square)](https://github.com/ElsayedMagdy122/Zad-El-Momen/actions)
  [![License](https://img.shields.io/badge/License-Apache%202.0-blue?style=flat-square)](LICENSE)

  <a href="https://play.google.com/store/apps/details?id=dev.sayed.mehrabalmomen">
    <img src="https://play.google.com/intl/en_us/badges/static/images/badges/en_badge_web_generic.png" alt="Get it on Google Play" height="52" />
  </a>

</div>

---

## 📸 Screenshots

<div align="center">
  <table>
    <tr>
      <td width="50%"><img src="docs/images/preview_quran.png" alt="Quran" width="100%" /></td>
      <td width="50%"><img src="docs/images/preview_memorization.png" alt="AI Tasmee3" width="100%" /></td>
    </tr>
    <tr>
      <td width="50%"><img src="docs/images/preview_prayer_qiblah.png" alt="Prayers & Qiblah" width="100%" /></td>
      <td width="50%"><img src="docs/images/preview_azkar.png" alt="Azkar" width="100%" /></td>
    </tr>
    <tr>
      <td width="50%"><img src="docs/images/preview_companion.png" alt="Faithful Companion" width="100%" /></td>
      <td width="50%"><img src="docs/images/preview_recitations.png" alt="Audio Recitations" width="100%" /></td>
    </tr>
    <tr>
      <td width="50%"><img src="docs/images/preview_radio.png" alt="Quran Radio" width="100%" /></td>
      <td width="50%"><img src="docs/images/preview_customization.png" alt="Settings" width="100%" /></td>
    </tr>
  </table>
</div>

---

## ✨ Features

- 🎙️ **On-Device AI Tasmee3:** Memorization verification powered by **Sherpa-ONNX**. Runs completely offline; zero voice data is sent to external servers.
- 📖 **Holy Quran & Tafsir:** Complete Uthmani text, verse-by-verse Tafsir, search, and bookmarks.
- 🕌 **Accurate Prayer Times & Azan:** Astronomical calculation with customizable Mathhab, exact alarms (`AlarmManager`), and full-screen Azan popup.
- 🧭 **Sensor-Fused Qiblah Compass:** Real-time Kaaba direction using device geomagnetic sensors with calibration guide.
- 📿 **Daily Adhkar:** Comprehensive Hisn al-Muslim supplications with haptic digital counter.
- 🤝 **Faithful Companion (الرفيق):** Contextual daily reminders, sunnah habits, and spiritual guidance.
- 📻 **24/7 Quran Radio:** Low-bandwidth live streaming and recitation playback via **Media3 ExoPlayer**.
- 📱 **Home Screen Widget:** Glanceable upcoming prayer countdown right on your home screen.

---

## 🛠️ Tech Stack

| Domain | Technologies |
| :--- | :--- |
| **Language & Core** | Kotlin 2.0.21, Coroutines, Flow |
| **UI Framework** | Jetpack Compose (BOM), Custom Design System, Coil |
| **Architecture** | Clean Architecture (Domain / Data / Presentation), MVI / MVVM |
| **Dependency Injection** | Koin 4.x |
| **Database & Storage** | Room Database 2.8.4 (KSP), Jetpack DataStore |
| **On-Device AI** | Sherpa-ONNX 2.0.1 (Offline ASR for recitation check) |
| **Media & Audio** | AndroidX Media3 ExoPlayer |
| **Backend & Sync** | Supabase Kotlin SDK, Firebase (FCM, Crashlytics, Perf) |

---

## 🏛️ Architecture

The app follows **Clean Architecture** with strict layer separation:

```text
dev.sayed.mehrabalmomen/
├── presentation/   # Jetpack Compose UI, ViewModels, Navigation, Widgets
├── domain/         # Pure Kotlin: Entities, Repository Interfaces, Use Cases
├── data/           # Repository Impls, Room DB, Supabase, Sherpa-ONNX, Platform services
└── design_system/  # Colors, Typography, Reusable Compose components
```

📖 *For detailed data flow diagrams and layer explanations, see [**docs/ARCHITECTURE.md**](docs/ARCHITECTURE.md).*

---

## ⚡ Quickstart

```bash
# 1. Clone the repository
git clone https://github.com/ElsayedMagdy122/Zad-El-Momen.git
cd Zad-El-Momen

# 2. Add local.properties (Supabase credentials & Android SDK path)
# 3. Add app/google-services.json (from Firebase)

# 4. Build Debug APK
./gradlew assembleDebug --build-cache
```

📖 *For full setup instructions and troubleshooting, see [**docs/SETUP.md**](docs/SETUP.md).*

---

## 🔒 Privacy & Permissions

Zad Al-Mu'min is **100% ad-free and privacy-first**:
- `RECORD_AUDIO`: Used strictly for local, on-device AI recitation checking. **Audio is never recorded in the background or uploaded to any server.**
- `ACCESS_FINE_LOCATION`: Used solely on-device to calculate astronomical prayer times and the Qiblah angle.

📖 *Full permission justifications are documented in [**docs/PERMISSIONS.md**](docs/PERMISSIONS.md).*

---

## 🤝 Contributing

Contributions are welcome!
- Please read our [**CONTRIBUTING.md**](CONTRIBUTING.md) before opening a PR.
- Ensure branch names match CI format: `feature/xyz`, `fix/xyz`.
- Check our [**CODE_OF_CONDUCT.md**](CODE_OF_CONDUCT.md).

---

## 📄 License

Licensed under the [Apache License 2.0](LICENSE).
