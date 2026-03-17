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
   
<table style="width: 100%; border-collapse: collapse;"><tbody><tr><th style="width: 20%; text-align: center; border: 1px solid #ccc; padding: 8px;">Al Quran Al-Kareem Radio</th><th style="width: 20%; text-align: center; border: 1px solid #ccc; padding: 8px;">Azkar</th><th style="width: 20%; text-align: center; border: 1px solid #ccc; padding: 8px;">Bookmarks</th><th style="width: 20%; text-align: center; border: 1px solid #ccc; padding: 8px;">Dark Theme</th><th style="width: 20%; text-align: center; border: 1px solid #ccc; padding: 8px;">Full support English language</th></tr><tr><td style="width: 20%; text-align: center; border: 1px solid #ccc; padding: 8px;"><img style="max-width: 100%; height: auto;" alt="Al Quran Al-Kareem Radio" src="https://github.com/user-attachments/assets/94452d8f-a36a-4a71-ac3b-1668660d17f5"></td><td style="width: 20%; text-align: center; border: 1px solid #ccc; padding: 8px;"><img style="max-width: 100%; height: auto;" alt="Azkar" src="https://github.com/user-attachments/assets/5dabfcf2-2a23-48ac-bd7b-87be0800f3fe"></td><td style="width: 20%; text-align: center; border: 1px solid #ccc; padding: 8px;"><img style="max-width: 100%; height: auto;" alt="Bookmarks" src="https://github.com/user-attachments/assets/dad4ebed-df7e-4379-a76a-0865af95a2cb"></td><td style="width: 20%; text-align: center; border: 1px solid #ccc; padding: 8px;"><img style="max-width: 100%; height: auto;" alt="Dark Theme" src="https://github.com/user-attachments/assets/703fd512-28af-45f3-9f88-054cfdf266b4"></td><td style="width: 20%; text-align: center; border: 1px solid #ccc; padding: 8px;"><img style="max-width: 100%; height: auto;" alt="Full support English language" src="https://github.com/user-attachments/assets/5e5a56e1-d204-4371-9fe8-7b2988e5fbc2"></td></tr><tr><th style="width: 20%; text-align: center; border: 1px solid #ccc; padding: 8px;">Holy Quran</th><th style="width: 20%; text-align: center; border: 1px solid #ccc; padding: 8px;">Prayer Times</th><th style="width: 20%; text-align: center; border: 1px solid #ccc; padding: 8px;">Qibla Finder</th><th style="width: 20%; text-align: center; border: 1px solid #ccc; padding: 8px;">Smart Search</th><th style="width: 20%; text-align: center; border: 1px solid #ccc; padding: 8px;">Tafsir</th></tr><tr><td style="width: 20%; text-align: center; border: 1px solid #ccc; padding: 8px;"><img style="max-width: 100%; height: auto;" alt="Holy Quran" src="https://github.com/user-attachments/assets/3a09a6d5-2f87-401f-88bf-e235339594d0"></td><td style="width: 20%; text-align: center; border: 1px solid #ccc; padding: 8px;"><img style="max-width: 100%; height: auto;" alt="Prayer Times" src="https://github.com/user-attachments/assets/69c104aa-e993-4fe7-b1ec-18e4fe3afd5c"></td><td style="width: 20%; text-align: center; border: 1px solid #ccc; padding: 8px;"><img style="max-width: 100%; height: auto;" alt="Qibla Finder" src="https://github.com/user-attachments/assets/5768207b-2214-40a3-8095-da99d92e72a5"></td><td style="width: 20%; text-align: center; border: 1px solid #ccc; padding: 8px;"><img style="max-width: 100%; height: auto;" alt="Smart Search" src="https://github.com/user-attachments/assets/e262ea50-4317-4194-a7bd-b8587f63f2f3"></td><td style="width: 20%; text-align: center; border: 1px solid #ccc; padding: 8px;"><img style="max-width: 100%; height: auto;" alt="Tafsir" src="https://github.com/user-attachments/assets/14fb7e18-a4fb-47d6-a739-c5a521e030d9"></td></tr></tbody></table>
## 🤝 Contributing

Contributions are what make the open-source community such an amazing place to learn, inspire, and create. Any contributions you make are **greatly appreciated**.

If you have a suggestion that would make this better, please fork the repo and create a pull request. You can also simply open an issue with the tag "enhancement".
Don't forget to give the project a star! Thanks again!

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/Wird`)
3. Commit your Changes (`git commit -m 'Add wird section'`)
4. Push to the Branch (`git push origin feature/Wird`)
5. Open a Pull Request


