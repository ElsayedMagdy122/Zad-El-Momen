<div align="center">

  <a href="https://github.com/ElsayedMagdy122/Zad-El-Momen">
    <img src="docs/images/app_icon.png" alt="Zad Al-Mu'min Logo" width="96" height="96" />
  </a>

  # Zad Al-Mu'min | زاد المؤمن

  <p>
    <a href="#arabic-version"><strong>🇸🇦 العربية (اضغط هنا للنسخة العربية)</strong></a> •
    <a href="#english-version"><strong>🇬🇧 English Version</strong></a>
  </p>

  <p>
    <img src="https://img.shields.io/badge/Android-8.0%2B-3DDC84?logo=android&logoColor=white" alt="Android 8.0+" />
    <img src="https://img.shields.io/badge/Kotlin-2.0.21-7F52FF?logo=kotlin&logoColor=white" alt="Kotlin 2.0.21" />
    <img src="https://img.shields.io/badge/Compose-Material%203-4285F4?logo=jetpackcompose&logoColor=white" alt="Jetpack Compose" />
    <img src="https://img.shields.io/badge/Architecture-Clean%20%2F%20MVI-D32F2F" alt="Clean Architecture" />
    <img src="https://img.shields.io/badge/DI-Koin-F57C00" alt="Koin" />
    <img src="https://img.shields.io/badge/License-CC%20BY--NC--SA%204.0%20(Educational)-blue" alt="License: CC BY-NC-SA 4.0" />
  </p>

  <p>
    <a href="https://play.google.com/store/apps/details?id=dev.sayed.mehrabalmomen">
      <img src="https://play.google.com/intl/ar_ar/badges/static/images/badges/ar_badge_web_generic.png" alt="Google Play" height="70" />
    </a>
  </p>

  <br/>

  <img src="docs/images/banner.png" alt="Zad Al-Mu'min Banner" width="100%" />

</div>

---

<div id="arabic-version" dir="rtl">

## 🇸🇦 النسخة العربية

تطبيق إسلامي مفتوح المصدر لنظام أندرويد مبني بالكامل بتقنيات أندرويد الحديثة (Kotlin و Jetpack Compose). يجمع التطبيق بين التصميم الهادئ والمميزات الدقيقة مع الحفاظ على الخصوصية التامة والعمل دون إنترنت.

### المميزات الفعلية في التطبيق

* **تسميع القرآن الكريم ومراجعة الحفظ بالذكاء الاصطناعي (Tasmee3):** مراجعة الحفظ لحظياً عبر نموذج الذكاء الاصطناعي المدمج محلياً (Sherpa-ONNX). يتم تحليل الصوت ومطابقة الآيات واكتشاف الأخطاء وتنبيه القارئ عند النسيان أو السهو دون الحاجة للاتصال بالإنترنت، ودون حفظ أو إرسال الصوت لأي خادم.
* **المصحف الشريف والتلاوات الصوتية:** قراءة القرآن الكريم كاملاً بالرسم العثماني، والاستماع لتلاوات أشهر القراء لجميع الآيات مع إمكانية التكرار والتشغيل المتواصل، بالإضافة إلى ميزة حفظ الآيات في المفضلة وتفسير الآيات.
* **البحث في آيات القرآن الكريم:** إمكانية البحث النصي في المصحف كاملاً أو البحث داخل سورة بعينها للوصول السريع لأي موضع.
* **مواقيت الصلاة وحساب الأذان:** حساب مواقيت الصلاة بدقة فلكية باستخدام مكتبة Adhan مع دعم مختلف طرق الحساب المعتمدة والمذاهب (الشافعي والحنبلي والمالكي / الحنفي)، مع خريطة تفاعلية لتحديد واختيار الموقع عبر MapLibre.
* **شاشة أذان كاملة وتنبيهات منضبطة:** منبهات نظام دقيقة (AlarmManager) ترفع الأذان وتظهر واجهة الأذان حتى لو كانت الشاشة مغلقة أو كان الجهاز في وضع توفير الطاقة (Doze Mode).
* **بوصلة القبلة:** تحديد اتجاه الكعبة المشرفة في الوقت الفعلي عبر دمج حساسات الهاتف (التسارع والمجال المغناطيسي)، مع شاشة معايرة تفاعلية لحساس البوصلة.
* **الأذكار والأدعية اليومية وإحصائيات الالتزام:** أذكار الصباح والمساء وبقية الأبواب مع عداد لكل ذكر، وخريطة التزام يومية (Contribution Heatmap) لتتبع القراءة، مع إمكانية إضافة أقسام أذكار مخصصة للمستخدم.
* **رفيق المؤمن (المرافق التفاعلي):** شخصية تفاعلية مرسومة بـ Canvas (Bird Animation) تتفاعل مع المستخدم حسب أوقات اليوم والحالة (أذكار الصباح والمساء، أوقات الصلاة، التسبيح).
* **إذاعة القرآن الكريم (Live Radio):** استماع مباشر لإذاعات القرآن الكريم المتنوعة مع مشغل يعمل في الخلفية عبر Media3 ExoPlayer وإشعارات التحكم بالوسائط.
* **ويدجت مواقيت الصلاة (AppWidget):** ويدجت للشاشة الرئيسية يعرض صلوات اليوم والوقت المتبقي حتى موعد الصلاة القادمة.
* **إعدادات ومظهر مخصص:** دعم كامل للغة العربية والإنجليزية، والوضع الليلي (Dark Mode) والوضع الفاتح، والتحكم في إعدادات تنبيهات الصلاة.

### معرض الشاشات

<div align="center">
  <table>
    <tr>
      <td width="50%"><img src="docs/images/preview_quran.png" alt="المصحف الشريف" width="100%" /></td>
      <td width="50%"><img src="docs/images/preview_memorization.png" alt="التسميع بالذكاء الاصطناعي" width="100%" /></td>
    </tr>
    <tr>
      <td width="50%"><img src="docs/images/preview_prayer_qiblah.png" alt="الصلاة والقبلة" width="100%" /></td>
      <td width="50%"><img src="docs/images/preview_azkar.png" alt="الأذكار" width="100%" /></td>
    </tr>
    <tr>
      <td width="50%"><img src="docs/images/preview_companion.png" alt="رفيق المؤمن" width="100%" /></td>
      <td width="50%"><img src="docs/images/preview_recitations.png" alt="التلاوات الصوتية" width="100%" /></td>
    </tr>
    <tr>
      <td width="50%"><img src="docs/images/preview_radio.png" alt="إذاعة القرآن" width="100%" /></td>
      <td width="50%"><img src="docs/images/preview_customization.png" alt="الإعدادات والتخصيص" width="100%" /></td>
    </tr>
  </table>
</div>

### حزمة التقنيات والمكتبات المستخدمة

* **اللغة والأساسيات:** Kotlin 2.0.21، Coroutines، Flow، Kotlinx Serialization، Kotlinx Datetime.
* **واجهة المستخدم والتصميم:** Jetpack Compose (BOM)، Material 3، نظام تصميم مخصص (Custom Design System)، Coil لتحميل الصور، رسوم متحركة مخصصة بـ Canvas.
* **المعمارية البرمجية:** Clean Architecture بنمط MVI/MVVM وفصل كامل بين الطبقات (`presentation`, `domain`, `data`).
* **حقن الاعتماديات:** Koin (koin-core, koin-android, koin-androidx-compose).
* **قاعدة البيانات والتخزين المحلي:** Room Database 2.8.4 مع مولد الأكواد KSP، و Jetpack DataStore Preferences لتخزين الإعدادات.
* **محرك حساب مواقيت الصلاة:** مكتبة Adhan 2 (Batoul Apps) لحساب أوقات الصلاة فلكياً.
* **الخرائط والموقع:** MapLibre Compose و Google Play Services Location لتحديد الموقع الجغرافي.
* **محرك الصوت والذكاء الاصطناعي:** مكتبة Sherpa-ONNX المدمجة داخل التطبيق للتعرف الصوتي ومطابقة التلاوة محلياً على الجهاز.
* **تشغيل الصوتيات والوسائط:** AndroidX Media3 ExoPlayer لتشغيل تلاوات الآيات والإذاعات المباشرة في الخلفية عبر Foreground Service.
* **الخدمات السحابية والتحليلات:** Supabase Kotlin SDK للبيانات السحابية والمزامنة وبلاغات الأخطاء، و Firebase (Crashlytics, Performance, Cloud Messaging, Analytics).
* **التقويم الهجري والميلادي:** مكتبة Umm al-Qura Calendar.
* **عمليات الشراء والدعم:** Google Play Billing Client (billing-ktx).

### تشغيل المشروع محلياً

1. استنسخ المستودع:
   ```bash
   git clone https://github.com/ElsayedMagdy122/Zad-El-Momen.git
   cd Zad-El-Momen
   ```

2. أنشئ ملف `local.properties` في المجلد الرئيسي وضع فيه مسار الـ SDK وبيانات الربط:
   ```properties
   sdk.dir=/path/to/your/android-sdk
   SUPABASE_URL="your_supabase_url"
   SUPABASE_KEY="your_supabase_key"
   SUPPORT_1="support_1"
   SUPPORT_5="support_5"
   SUPPORT_10="support_10"
   SUPPORT_25="support_25"
   SUPPORT_100="support_100"
   ```

3. ضع ملف `google-services.json` داخل مجلد `app/`.

4. قم ببناء نسخة التجربة (Debug APK):
   ```bash
   ./gradlew assembleDebug
   ```

### المساهمة في المشروع

نرحب بمساهمات الجميع لتطوير التطبيق، للمشاركة يرجى اتباع الخطوات التالية:

1. قم بعمل Fork للمستودع على حسابك، ثم استنسخه محلياً على جهازك.
2. انتقل لفرع التطوير الأساسي `develop`:
   ```bash
   git checkout develop
   git pull origin develop
   ```
3. أنشئ فرعاً جديداً بالتنسيق المطلوب المعتمد في فحص الـ CI:
   `type/branch-name` حيث أن الـ `type` يكون أحد الأنواع التالية: `feature`, `feat`, `fix`, `test`, `refactor`, `chore`, `style`, `screen`, `docs`, `component`, `ci`.
   ```bash
   git checkout -b feature/quran-audio-player
   ```
4. اكتب تعديلاتك، واحرص على كتابة رسائل Commit واضحة ومباشرة.
5. ادفع الفرع لحسابك وافتح Pull Request موجهاً إلى فرع `develop`.

### مصادر البيانات والحقوق

* **نص القرآن الكريم:** مجمع الملك فهد لطباعة المصحف الشريف ([موقع المجمع الرسمي](https://qurancomplex.gov.sa/)).
* **تفسير القرآن الكريم:** مجتمع إتقان للبرمجيات الإسلامية ([community.itqan.dev](https://community.itqan.dev/)).
* **صوتيات وتلاوات القرآن الكريم:** شبكة [mp3quran.net](https://www.mp3quran.net/ar).
* **موديل الذكاء الاصطناعي للتسميع:** [QuranLab.ai](https://quranlab.ai/) مدمج من خلال [Sherpa-ONNX](https://github.com/k2-fsa/sherpa-onnx).

### الترخيص وحقوق الاستخدام

هذا المشروع متاح للأغراض التعليمية والشخصية فقط تحت رخصة **[CC BY-NC-SA 4.0](LICENSE)**. يُمنع منعاً باتاً استغلال المشروع تجارياً أو إعادة رفعه وبيعه ونشره على المتاجر (مثل Google Play أو غيرها).

</div>

---

<div id="english-version">

## 🇬🇧 English Version

An offline-first Android Islamic app built with Kotlin and Jetpack Compose. Combines a clean user interface with accurate prayer calculations, Quran recitation assistance via on-device AI, and media playback.

### Actual App Features

* **On-Device AI Quran Memorization Check (Tasmee3):** Real-time recitation verification powered by an embedded Sherpa-ONNX speech recognition model. Detects mistakes and hesitations offline with complete privacy—no audio is ever recorded in the background or uploaded to remote servers.
* **Holy Quran & Audio Recitations:** Complete Uthmani script text, verse-by-verse audio playback from well-known reciters with continuous and repeat modes, verse bookmarks, and exegesis (Tafsir).
* **Quran Verse Search:** Instant text search across the entire Quran or within a specific Surah to find any verse quickly.
* **Prayer Times Calculation:** Accurate astronomical prayer times powered by the Adhan 2 library, with customizable calculation methods, Juristic schools (Hanafi / Shafi'i), and interactive map selection via MapLibre.
* **Full-Screen Athan & Precise Alarms:** Uses `AlarmManager` with exact alarms to trigger the Athan call and full-screen notification popup even in Doze mode or when the screen is locked.
* **Qiblah Compass:** Real-time direction to the Kaaba using fused accelerometer and geomagnetic sensor data, with an interactive sensor calibration guide.
* **Adhkar & Supplications:** Fortress of the Muslim (Hisn al-Muslim) categories with a counter, a daily reading contribution heatmap, and the ability to add custom user Adhkar categories.
* **Faithful Companion (Interactive Overlay):** Canvas-animated character (BirdCanvas) providing contextual reminders for daily Adhkar, prayer times, and sunnah habits.
* **Live Quran Radio:** 24/7 live streaming Islamic radio channels running as an Android Foreground Service powered by Media3 ExoPlayer.
* **Home Screen Widget:** Glanceable AppWidget displaying today's prayer times and a countdown timer to the upcoming prayer.
* **Customization:** Full Arabic and English localization, dark/light theme options, and customizable notification settings.

### Tech Stack & Libraries

* **Language & Core:** Kotlin 2.0.21, Coroutines, Flow, Kotlinx Serialization, Kotlinx Datetime.
* **UI & Design:** Jetpack Compose (BOM), Material 3, Custom Design System, Coil for image loading, Canvas custom drawing.
* **Architecture:** Clean Architecture with MVI/MVVM (`presentation`, `domain`, `data`).
* **Dependency Injection:** Koin (koin-core, koin-android, koin-androidx-compose).
* **Storage & Persistence:** Room Database 2.8.4 (KSP) and Jetpack DataStore Preferences.
* **Prayer Calculations:** Adhan 2 (Batoul Apps).
* **Maps & Location:** MapLibre Compose, Google Play Services Location.
* **Speech Recognition & AI:** Embedded Sherpa-ONNX runtime for offline Quran ASR.
* **Media & Audio:** AndroidX Media3 ExoPlayer for verse recitation and live radio streams.
* **Backend & Monitoring:** Supabase Kotlin SDK, Firebase (Crashlytics, Performance, Messaging, Analytics).
* **Calendar:** Umm al-Qura Calendar library.
* **In-App Billing:** Google Play Billing Client (billing-ktx).

### Getting Started

1. Clone the project:
   ```bash
   git clone https://github.com/ElsayedMagdy122/Zad-El-Momen.git
   cd Zad-El-Momen
   ```

2. Add a `local.properties` file in the root directory:
   ```properties
   sdk.dir=/path/to/your/android-sdk
   SUPABASE_URL="your_supabase_url"
   SUPABASE_KEY="your_supabase_key"
   SUPPORT_1="support_1"
   SUPPORT_5="support_5"
   SUPPORT_10="support_10"
   SUPPORT_25="support_25"
   SUPPORT_100="support_100"
   ```

3. Place your `google-services.json` inside the `app/` folder.

4. Build the debug APK:
   ```bash
   ./gradlew assembleDebug
   ```

### Contributing

1. Fork the repository and checkout `develop`:
   ```bash
   git checkout -b feature/your-feature
   ```
   Branch naming types allowed by CI: `feature`, `feat`, `fix`, `test`, `refactor`, `chore`, `style`, `screen`, `docs`, `component`, `ci`.
2. Commit and open a Pull Request against `develop`.

### Data Sources & Credits

* **Quran Text:** King Fahd Glorious Quran Printing Complex ([qurancomplex.gov.sa](https://qurancomplex.gov.sa/)).
* **Quran Tafsir:** Itqan Community ([community.itqan.dev](https://community.itqan.dev/)).
* **Quran Audio & Recitations:** [MP3Quran.net](https://www.mp3quran.net/).
* **On-Device AI Model:** [QuranLab.ai](https://quranlab.ai/) integrated via [Sherpa-ONNX](https://github.com/k2-fsa/sherpa-onnx).

### License

This project is licensed strictly for non-commercial educational and learning purposes under the **[Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International (CC BY-NC-SA 4.0)](LICENSE)** license. Re-publishing or re-distributing this application on app stores (e.g., Google Play) or using it for commercial purposes is strictly prohibited.

</div>
