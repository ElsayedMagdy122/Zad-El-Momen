# Setup and Build Guide

Follow this guide to set up your development environment, configure local properties, and build **Zad El Momen**.

---

## 📋 Prerequisites

1. **Android Studio**: Ladybug or newer.
2. **Java Development Kit (JDK)**: JDK 11 or higher.
3. **Android SDK**:
   - Compile SDK: 36
   - Min SDK: 26
   - Target SDK: 36

---

## 📥 Cloning and Opening the Project

1. Clone the repository:
   ```bash
   git clone https://github.com/your-org/Zad-El-Momen.git
   ```
2. Open Android Studio, select **Open**, and choose the project root folder.
3. Allow Gradle sync to complete.

---

## 🔑 Local Properties and Secrets Configuration

The project requires local configuration properties for Supabase integration and donation support keys.

Create a `local.properties` file in the project root directory (if it does not already exist) and add the following keys:

```properties
SUPABASE_URL=your_supabase_url_here
SUPABASE_KEY=your_supabase_key_here
SUPPORT_1=support_product_id_1
SUPPORT_5=support_product_id_5
SUPPORT_10=support_product_id_10
SUPPORT_25=support_product_id_25
SUPPORT_100=support_product_id_100
```

---

## 🏗️ Building and Running the App

### Debug Build
To run the debug variant on a connected device or emulator:
```bash
./gradlew app:assembleDebug
```
Or click the **Run** button in Android Studio.

### Release Build
To build a release Android App Bundle (`.aab`):
```bash
./gradlew app:bundleRelease
```
*(Note: Release signing requires valid keystore configuration in `app/build.gradle.kts` and environment variables for passwords).*
