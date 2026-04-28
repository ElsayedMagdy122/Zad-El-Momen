# Zad El Momen - Your Favourite Islamic App

<div align="center">
  <img src="https://img.shields.io/badge/Platform-Android-green.svg" alt="Platform">
  <img src="https://img.shields.io/badge/Language-Kotlin-blue.svg" alt="Language">
  <img src="https://img.shields.io/badge/UI-Jetpack%20Compose-orange.svg" alt="UI">
  <img src="https://img.shields.io/badge/Architecture-MVVM-red.svg" alt="Architecture">
  <img src="https://img.shields.io/badge/DI-Koin-yellow.svg" alt="DI">
  <a href='https://play.google.com/store/apps/details?id=dev.sayed.mehrabalmomen&pcampaignid=web_share'><img alt='Get it on Google Play' src='https://img.shields.io/badge/Google%20Play-Get%20it%20on-black?logo=google-play&logoColor=white' vertical-align='middle' /></a>

</div>

**Zad El Momen** is an Islamic application providing everything a Muslim needs to perform worship easily and accurately. It offers the complete Holy Quran with Tafsir, highly accurate daily prayer times based on your geographical location, and a Qibla finder to facilitate prayer anywhere.

The app also features Muslim Azkar, Azan alerts, Al Quran Al-Kareem Radio and customizable settings for calculation methods and school of thought (Mathhab), providing a personalized and comfortable user experience through a simple and smooth interface.

##  Features

### 📖 **Holy Quran & Tafsir**
- **Complete Quran**: Full access to the Holy Quran.
- **Tafsir Integration**: Detailed interpretation and meanings for better understanding.
- **Bookmarks**: Mark and save your favorite verses for quick access anytime.
- **Smart Search**: Quickly find verses by keywords, words, or phrases across the Holy Quran.

### 🕋 **Worship & Devotion**
- **Accurate Prayer Times**: High-precision timings based on your specific location.
- **Azan Alerts**: Stay notified for every prayer with timely alerts.
- **Qibla Finder**: Easily locate the direction of the Kaaba from anywhere in the world.
- **Azkar**: A comprehensive collection of morning, evening, and daily supplications.

###  **Personalized Experience**
- **Localization Support**: Full support for both **Arabic** and **English** languages.
- **Quran Font Size Control**: Easily adjust the Holy Quran text size for comfortable reading.
- **Customizable Settings**: Adjust calculation methods and schools of thought (Mathhab) to suit your preferences.
- **Modern UI**: A simple, smooth, and responsive interface built with Jetpack Compose.
- **Dark/Light Theme**: Support for system-wide theme preferences.

###  **Technical Excellence**
- **Offline Support**: Access essential features and previously loaded content without an internet connection.
- **Supabase Backend**: Robust data management and services.
- **Firebase Integration**: Real-time analytics, crash reporting, and cloud messaging.

##  Tech Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Architecture**: MVVM + Clean Architecture
- **Dependency Injection**: Koin
- **Backend**: Supabase (PostgREST, Realtime, Storage, Functions)
- **Local Database**: Room + DataStore Preferences
- **Networking**: Ktor (via Supabase SDK)
- **Asynchronous**: Kotlin Coroutines & Flow
- **Media**: Media3 ExoPlayer (audio playback)
- **Notifications**: Firebase Cloud Messaging (FCM)
- **Analytics & Monitoring**: Firebase Crashlytics + Performance
- **Maps & Location**: MapLibre Compose + Google Location Services
- **Calendar**: Umm al-Qura Calendar
- **Billing**: Google Play Billing

##  App Architecture
The app follows **Clean Architecture** principles with **MVVM** pattern, organized into layers:

- **Presentation**: Jetpack Compose UI, ViewModels, and State management.
- **Domain**: Pure business logic, Use Cases, and Repository interfaces.
- **Data**: Repository implementations, Remote (Supabase) and Local (Room) data sources, and mappers.
- **Design System**: Reusable UI components and design tokens.

##  Setup Instructions

### 1. Clone the Repository

```bash
git clone https://github.com/ElsayedMagdy122/Zad-El-Momen.git
cd Zad-El-Momen
```

### 2. Create Configuration Files
Create a `local.properties` file in the root directory (if not present) and add your Supabase credentials:
```properties
SUPABASE_URL="your_supabase_url"
SUPABASE_KEY="your_supabase_key"
# Support levels (IDs for in-app or tracking)
SUPPORT_1="id_1"
SUPPORT_5="id_5"
SUPPORT_10="id_10"
SUPPORT_25="id_25"
SUPPORT_100="id_100"
```

### 3. Add Firebase Configuration
Add your `google-services.json` file to the `app/` directory.

### 4. Build and Run
1. Open the project in Android Studio  
2. Sync the project with Gradle files  
3. Build the project (**Build > Make Project**)  
4. Run on device or emulator (**Run > Run 'app'**)

## 📸 Screenshots
   
<table style="width: 100%; border-collapse: collapse;"><tbody><tr><th style="width: 20%; text-align: center; border: 1px solid #ccc; padding: 8px;">Home Screen</th><th style="width: 20%; text-align: center; border: 1px solid #ccc; padding: 8px;">Prayers times</th><th style="width: 20%; text-align: center; border: 1px solid #ccc; padding: 8px;">Qibla</th><th style="width: 20%; text-align: center; border: 1px solid #ccc; padding: 8px;">Azkar</th><th style="width: 20%; text-align: center; border: 1px solid #ccc; padding: 8px;">Tafsir</th></tr><tr><td style="width: 20%; text-align: center; border: 1px solid #ccc; padding: 8px;"><img style="max-width: 100%; height: auto;" alt="Home Screen" src="https://github.com/user-attachments/assets/7cce4da5-dbc6-41e7-be75-6c56de76d85c"></td><td style="width: 20%; text-align: center; border: 1px solid #ccc; padding: 8px;"><img style="max-width: 100%; height: auto;" alt="Prayers times" src="https://github.com/user-attachments/assets/5822aa2c-396e-4cdb-9b0e-c4a9ab970892"></td><td style="width: 20%; text-align: center; border: 1px solid #ccc; padding: 8px;"><img style="max-width: 100%; height: auto;" alt="Qibla" src="https://github.com/user-attachments/assets/11c1d188-e1ba-4a21-a6c5-0acb34cbda7c"></td><td style="width: 20%; text-align: center; border: 1px solid #ccc; padding: 8px;"><img style="max-width: 100%; height: auto;" alt="Azkar" src="https://github.com/user-attachments/assets/58b6ffa6-4ea1-468e-98d4-a0151e1ebef4"></td><td style="width: 20%; text-align: center; border: 1px solid #ccc; padding: 8px;"><img style="max-width: 100%; height: auto;" alt="Tafsir" src="https://github.com/user-attachments/assets/70573336-85ec-46c0-a56b-fc71088ba7e0"></td></tr><tr><th style="width: 20%; text-align: center; border: 1px solid #ccc; padding: 8px;">Surah list</th><th style="width: 20%; text-align: center; border: 1px solid #ccc; padding: 8px;">Reading mode</th><th style="width: 20%; text-align: center; border: 1px solid #ccc; padding: 8px;">Radio</th><th style="width: 20%; text-align: center; border: 1px solid #ccc; padding: 8px;">Settings</th><th style="width: 20%; text-align: center; border: 1px solid #ccc; padding: 8px;"></th></tr><tr><td style="width: 20%; text-align: center; border: 1px solid #ccc; padding: 8px;"><img style="max-width: 100%; height: auto;" alt="Surah list" src="https://github.com/user-attachments/assets/63324b1f-d3d5-461a-a893-bfa19b46d1ee"></td><td style="width: 20%; text-align: center; border: 1px solid #ccc; padding: 8px;"><img style="max-width: 100%; height: auto;" alt="Reading mode" src="https://github.com/user-attachments/assets/4cb991e3-cbd1-47fb-b212-1b614945e73e"></td><td style="width: 20%; text-align: center; border: 1px solid #ccc; padding: 8px;"><img style="max-width: 100%; height: auto;" alt="Radio" src="https://github.com/user-attachments/assets/90e08c9d-ff79-421f-a2ac-13c2d12cc52a"></td><td style="width: 20%; text-align: center; border: 1px solid #ccc; padding: 8px;"><img style="max-width: 100%; height: auto;" alt="Settings" src="https://github.com/user-attachments/assets/0f339955-21bd-45a8-b36d-f0e77c314694"></td><td style="width: 20%; text-align: center; border: 1px solid #ccc; padding: 8px;"></td></tr></tbody></table>

## 🤝 Contributing

Contributions are what make the open-source community such an amazing place to learn, inspire, and create. Any contributions you make are **greatly appreciated**.

If you have a suggestion that would make this better, please fork the repo and create a pull request. You can also simply open an issue with the tag "enhancement".
Don't forget to give the project a star! Thanks again!

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/Wird`)
3. Commit your Changes (`git commit -m 'Add wird section'`)
4. Push to the Branch (`git push origin feature/Wird`)
5. Open a Pull Request


