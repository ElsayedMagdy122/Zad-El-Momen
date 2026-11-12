# 🛠️ Local Environment & Build Setup

Detailed instructions to clone, configure, build, and debug **Zad Al-Mu'min**.

---

## 📋 Prerequisites

| Requirement | Minimum Version | Recommended |
| :--- | :--- | :--- |
| **Android Studio** | Ladybug (2024.2.1+) | Latest Stable |
| **JDK** | JDK 17 | Eclipse Temurin 17 |
| **Android SDK** | Min SDK 26 (Android 8.0) | Compile/Target SDK 36 |
| **NDK / CMake** | Required for Sherpa-ONNX | NDK bundled with SDK |

---

## ⚡ Step-by-Step Instructions

### 1. Clone the Repository
```bash
git clone https://github.com/ElsayedMagdy122/Zad-El-Momen.git
cd Zad-El-Momen
```

### 2. Configure `local.properties`
Create or edit `local.properties` in the project root:
```properties
sdk.dir=/path/to/your/android-sdk

# Supabase Credentials (Required for remote sync)
SUPABASE_URL="https://your-project.supabase.co"
SUPABASE_KEY="your-anon-key"

# In-App Support IDs (Mock strings are fine for local debug)
SUPPORT_1="id_1"
SUPPORT_5="id_5"
SUPPORT_10="id_10"
SUPPORT_25="id_25"
SUPPORT_100="id_100"
```

### 3. Add `google-services.json`
Place your `google-services.json` inside the `app/` folder:
```bash
cp /path/to/google-services.json app/google-services.json
```
> **Note:** If you don't have Firebase configured, a minimal valid JSON with dummy credentials will allow the project to compile for UI work.

### 4. Build via Gradle
```bash
# Linux / macOS
./gradlew assembleDebug --build-cache

# Windows PowerShell
.\gradlew.bat assembleDebug --build-cache
```

### 5. Install to Device
```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

---

## 🔧 Troubleshooting

- **Room Schema Warnings:** Schema directory is set to `app/schemas`. Ensure KSP is running properly.
- **Sherpa-ONNX .aar:** The local library is located at `app/libs/sherpa-onnx-2.0.1.aar`. Supported ABIs are `armeabi-v7a` and `arm64-v8a`.
