# Zad El Momen - Your Favourite Islamic App

<div align="center">
  <img src="https://img.shields.io/badge/Platform-Android-green.svg" alt="Platform">
  <img src="https://img.shields.io/badge/Language-Kotlin-blue.svg" alt="Language">
  <img src="https://img.shields.io/badge/UI-Jetpack%20Compose-orange.svg" alt="UI">
  <img src="https://img.shields.io/badge/Architecture-MVVM-red.svg" alt="Architecture">
  <img src="https://img.shields.io/badge/DI-Koin-yellow.svg" alt="DI">
</div>

**Zad El Momen** is an Islamic application providing everything a Muslim needs to perform worship easily and accurately. It offers the complete Holy Quran with Tafsir, highly accurate daily prayer times based on your geographical location, and a Qibla finder to facilitate prayer anywhere.

The app also features Muslim Azkar, Azan alerts, Al Quran Al-Kareem Radio and customizable settings for calculation methods and school of thought (Mathhab), providing a personalized and comfortable user experience through a simple and smooth interface.

## 🚀 Features

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

### ⚙️ **Personalized Experience**
- **Localization Support**: Full support for both **Arabic** and **English** languages.
- **Quran Font Size Control**: Easily adjust the Holy Quran text size for comfortable reading.
- **Customizable Settings**: Adjust calculation methods and schools of thought (Mathhab) to suit your preferences.
- **Modern UI**: A simple, smooth, and responsive interface built with Jetpack Compose.
- **Dark/Light Theme**: Support for system-wide theme preferences.

### 🔐 **Technical Excellence**
- **Offline Support**: Access essential features and previously loaded content without an internet connection.
- **Supabase Backend**: Robust data management and services.
- **Firebase Integration**: Real-time analytics, crash reporting, and cloud messaging.

## 🛠️ Tech Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Architecture**: MVVM + Clean Architecture
- **Dependency Injection**: Koin
- **Backend**: Supabase
- **Local Database**: Room
- **Media**: ExoPlayer for audio content
- **Calendar**: Umm al-Qura Calendar library
- **Networking**: Ktor / Retrofit (via Supabase SDK)
- **Image Loading**: Coil
- **Maps & Location**: Google Maps & Location services

## 🏗️ App Architecture
The app follows **Clean Architecture** principles with **MVVM** pattern, organized into layers:

- **Presentation**: Jetpack Compose UI, ViewModels, and State management.
- **Domain**: Pure business logic, Use Cases, and Repository interfaces.
- **Data**: Repository implementation, Supabase API, and Room database.
- **Design System**: Reusable UI components and design tokens.

## 🔧 Setup Instructions

### 1. Clone the Repository

```bash
git clone https://github.com/sayed-dev/Zad-El-Momen.git
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
   
<table style="width: 100%; border-collapse: collapse;"><tbody><tr><th style="width: 20%; text-align: center; border: 1px solid #ccc; padding: 8px;">Al Quran Al-Kareem Radio</th><th style="width: 20%; text-align: center; border: 1px solid #ccc; padding: 8px;">Athkar</th><th style="width: 20%; text-align: center; border: 1px solid #ccc; padding: 8px;">Bookmarks</th><th style="width: 20%; text-align: center; border: 1px solid #ccc; padding: 8px;">Dark Theme</th><th style="width: 20%; text-align: center; border: 1px solid #ccc; padding: 8px;">Full support for English language</th></tr><tr><td style="width: 20%; text-align: center; border: 1px solid #ccc; padding: 8px;"><img style="max-width: 100%; height: auto;" alt="Al Quran Al-Kareem Radio" src="![Al Quran Al-Kareem Radio](https://github.com/user-attachments/assets/a8d8623d-a45f-478d-90fa-d67a0666bd2e)"></td><td style="width: 20%; text-align: center; border: 1px solid #ccc; padding: 8px;"><img style="max-width: 100%; height: auto;" alt="Athkar" src="![Athkar](https://github.com/user-attachments/assets/95ab5dc8-a85c-42c0-9305-81818a6f28b0)"></td><td style="width: 20%; text-align: center; border: 1px solid #ccc; padding: 8px;"><img style="max-width: 100%; height: auto;" alt="Bookmarks" src="![Bookmarks](https://github.com/user-attachments/assets/7be67d17-8900-4e0e-8eee-56261c111b23)"></td><td style="width: 20%; text-align: center; border: 1px solid #ccc; padding: 8px;"><img style="max-width: 100%; height: auto;" alt="Dark Theme" src="![dark theme](https://github.com/user-attachments/assets/54bccf22-0c21-457c-ad97-98238d15a547)"></td><td style="width: 20%; text-align: center; border: 1px solid #ccc; padding: 8px;"><img style="max-width: 100%; height: auto;" alt="Full support for English language" src="![Full support English language](https://github.com/user-attachments/assets/66a65fb4-5bee-43d7-9048-fa694e0ee85a)"></td></tr><tr><th style="width: 20%; text-align: center; border: 1px solid #ccc; padding: 8px;">Holy Quran</th><th style="width: 20%; text-align: center; border: 1px solid #ccc; padding: 8px;">Prayer Times</th><th style="width: 20%; text-align: center; border: 1px solid #ccc; padding: 8px;">Qibla Finder</th><th style="width: 20%; text-align: center; border: 1px solid #ccc; padding: 8px;">Smart Search</th><th style="width: 20%; text-align: center; border: 1px solid #ccc; padding: 8px;">Tafsir</th></tr><tr><td style="width: 20%; text-align: center; border: 1px solid #ccc; padding: 8px;"><img style="max-width: 100%; height: auto;" alt="Holy Quran" src="![Holy Quran](https://github.com/user-attachments/assets/d8eb1ecf-11be-424d-b64e-d5ec44768cd7)"></td><td style="width: 20%; text-align: center; border: 1px solid #ccc; padding: 8px;"><img style="max-width: 100%; height: auto;" alt="Prayer Times" src="![Prayer Times](https://github.com/user-attachments/assets/8dfa20bf-4835-428f-9a7e-b171d109f029)"></td><td style="width: 20%; text-align: center; border: 1px solid #ccc; padding: 8px;"><img style="max-width: 100%; height: auto;" alt="Qibla Finder" src="![Qibla Finder](https://github.com/user-attachments/assets/6b965276-fc94-4e7a-ad76-467ad672495e)"></td><td style="width: 20%; text-align: center; border: 1px solid #ccc; padding: 8px;"><img style="max-width: 100%; height: auto;" alt="Smart Search" src="![smart search](https://github.com/user-attachments/assets/a527cba3-bff1-434e-b8d0-95559161f9a8)"></td><td style="width: 20%; text-align: center; border: 1px solid #ccc; padding: 8px;"><img style="max-width: 100%; height: auto;" alt="Tafsir" src="![Tafsir](https://github.com/user-attachments/assets/75faa214-0125-4a5b-8150-c79a2a25a4fa)"></td></tr></tbody></table>

## 🤝 Contributing

Contributions are what make the open-source community such an amazing place to learn, inspire, and create. Any contributions you make are **greatly appreciated**.

If you have a suggestion that would make this better, please fork the repo and create a pull request. You can also simply open an issue with the tag "enhancement".
Don't forget to give the project a star! Thanks again!

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/Wird`)
3. Commit your Changes (`git commit -m 'Add wird section'`)
4. Push to the Branch (`git push origin feature/Wird`)
5. Open a Pull Request


