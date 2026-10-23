# Zad El Momen (زاد المؤمن)

> A comprehensive, modern Islamic companion mobile application crafted with cutting-edge Android technologies and Clean Architecture principles to serve as your daily digital companion for worship and remembrance.

---

## 🌟 App Cover & Banner

> ![Zad El Momen Cover](docs/images/cover.png)

---

## 📖 Overview

**Zad El Momen** is a professional Android application designed to fulfill a Muslim's daily spiritual needs, including accurate prayer times, Holy Quran recitation and reading, Azkar and Duas, Qiblah direction, Islamic radio streaming, reminders, and cloud-backed support features. Built using modern Android development best practices (Clean Architecture, MVVM), Kotlin, and Jetpack Compose.

---

## ✨ Key Features

| Feature | Technical & Functional Description |
| :--- | :--- |
| **Prayer Times & Alarms** | Precise prayer time calculation based on location and calculation methods with automated Azan notifications and alarm scheduling via `AlarmManager` and `Foreground Service`. |
| **Qiblah Direction** | Accurate Qiblah compass utilizing device sensors and location services (`LocationRepository`). |
| **Holy Quran** | Uthmani script Quran browsing, Tafseer (Al-Muyassar & Al-Mukhtasar), bookmarks (`BookmarkDao`), and reading progress tracking. |
| **Audio Recitations** | High-quality audio recitations by renowned reciters with offline downloading and background playback support powered by `ExoPlayer` (Media3) and `WorkManager`. |
| **Azkar & Duas** | Comprehensive collection of supplications and remembrances organized by categories with history and usage statistics (`AzkarDao` & Room Database). |
| **Islamic Radio** | Live streaming of Islamic radio stations and lectures via a dedicated media player and background service (`RadioPlayerService`). |
| **Companion & Mood** | Interactive UI states and dynamic content reflecting daily spiritual states and personalized reminders. |
| **Support & Bug Reporting** | Seamless error reporting and donation support integration via `Supabase` and `Google Play Billing`. |

---

## 🏛️ Architecture & Tech Stack

The project adheres to **Clean Architecture**, divided into three core layers:
1. **Presentation Layer**: Jetpack Compose UI & Material 3, ViewModel state management.
2. **Domain Layer**: Core domain entities, business use cases, and repository interfaces.
3. **Data Layer**: Repositories, local data sources (Room Database), and remote data sources (`Supabase` & `Firebase`).

### Tech Stack & Libraries:
- **Language**: Kotlin 2.0+
- **UI Toolkit**: Jetpack Compose & Material 3
- **Dependency Injection**: Koin BOM
- **Local Database**: Room with KSP
- **Backend & Cloud**: Supabase (PostgreSQL, RPC, Storage)
- **Firebase Services**: Firebase Analytics, Crashlytics, Cloud Messaging (FCM), Performance Monitoring
- **Media & Audio**: AndroidX Media3 ExoPlayer
- **Background Work**: WorkManager

---

## ⚙️ Prerequisites

- **Android Studio**: Ladybug or newer.
- **JDK**: Java 11 or higher.
- **Android SDK**: Compile SDK 36, Min SDK 26.
- **Gradle**: 8.x+

---

## 📱 App Screenshots & Showcase

### 1. Holy Quran Reader
> ![Holy Quran](docs/images/quran.png)
> *Browse Surahs and read verses with ease.*

### 2. Audio Recitations & Reciters
> ![Audio Recitations](docs/images/recitations.png)
> *Choose your favorite reciter and listen to audio.*

### 3. Islamic Radio
> ![Islamic Radio](docs/images/radio.png)
> *Live streaming of Quran and Islamic lectures.*

### 4. Personalization & Settings
> ![Settings](docs/images/settings.png)
> *Customize font, Tafseer, and theme preferences.*

### 5. Prayer Times & Azan
> ![Prayer Times](docs/images/prayer.png)
> *Track prayer times and countdowns.*

### 6. Daily Azkar
> ![Daily Azkar](docs/images/azkar.png)
> *Morning and evening remembrances and progress tracker.*

### 7. Search & Bookmarks
> ![Search & Bookmarks](docs/images/search.png)
> *Instant verse search and position saving.*

### 8. Daily Companion
> ![Daily Companion](docs/images/companion.png)
> *Your prayers and Quranic daily goal in one place.*

---

## ⚖️ License
This project is licensed under the **GNU General Public License v3.0 (GPLv3)**. This license permits code study, personal learning, and usage while legally prohibiting anyone from rebranding it as proprietary, closing its source, or publishing it under their own name on app stores without open-sourcing their work and preserving attribution. See [LICENSE.md](LICENSE.md) for details.

---

## 🔗 Quick Links

- [English Documentation (README_EN.md)](README_EN.md)
- [Setup & Build Guide (SETUP.md)](SETUP.md)
- [Contributing Guidelines (CONTRIBUTING.md)](CONTRIBUTING.md)
- [License (LICENSE.md)](LICENSE.md)
