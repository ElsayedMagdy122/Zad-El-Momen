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
    <img src="https://img.shields.io/badge/License-Apache%202.0-blue" alt="Apache 2.0" />
  </p>

  <p>
    <a href="https://play.google.com/store/apps/details?id=dev.sayed.mehrabalmomen">
      <img src="https://play.google.com/intl/ar_ar/badges/static/images/badges/ar_badge_web_generic.png" alt="Google Play" height="50" />
    </a>
  </p>

  <br/>

  <img src="docs/images/banner.png" alt="Zad Al-Mu'min Banner" width="100%" />

</div>

---

<div id="arabic-version" dir="rtl">

## 🇸🇦 النسخة العربية

تطبيق إسلامي حديث لنظام أندرويد مفتوح المصدر ومبني بتقنية Jetpack Compose، يعمل دون إنترنت ويحتوي على ميزة تسميع القرآن بالذكاء الاصطناعي محلياً، ومواقيت صلاة دقيقة، ومشغل صوتيات وإذاعة قرآنية.

### المميزات

* **مراجعة الحفظ بالذكاء الاصطناعي دون إنترنت:** تسميع الآيات والتحقق من صحتها عبر نموذج تعرف صوتي مدمج داخل الهاتف من خلال Sherpa-ONNX، وتتم معالجة الصوت في ذاكرة الجهاز دون إرسال أي تسجيل لخوادم خارجية.
* **القرآن الكريم والتفسير:** المصحف الشريف كاملاً بالرسم العثماني مع التفسير، وإمكانية البحث في الآيات وحفظ العلامات المرجعية.
* **مواقيت الصلاة والأذان:** حسابات فلكية دقيقة مع دعم طرق الحساب والمذاهب وتنبيهات أذان منضبطة عبر منبهات النظام.
* **بوصلة القبلة:** تحديد اتجاه الكعبة المشرفة في الوقت الفعلي باستخدام حساسات الهاتف مع دليل لمعايرة الحساس.
* **الأذكار والأدعية:** أذكار الصباح والمساء وحصن المسلم مع سبحة إلكترونية.
* **رفيق المؤمن:** خواطر وتذكيرات إيمانية وسنن يومية مناسبة لأوقات اليوم.
* **إذاعة القرآن الكريم:** بث مباشر لإذاعات القرآن الكريم على مدار 24 ساعة مع تشغيل مستقر في الخلفية عبر Media3 ExoPlayer.
* **ويدجت الشاشة الرئيسية:** ويدجت خفيف لعرض مواعيد الصلاة والوقت المتبقي للصلاة القادمة.

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

### التقنيات المستخدمة

* **الواجهة:** Jetpack Compose, Material 3, Custom Design System, Coil.
* **المعمارية البرمجية:** Clean Architecture مع نمط MVI/MVVM مقسمة إلى ثلاث طبقات (`presentation`, `domain`, `data`).
* **حقن الاعتماديات:** Koin.
* **التخزين المحلي:** Room Database, Jetpack DataStore.
* **مشغل الوسائط:** AndroidX Media3 ExoPlayer.
* **محرك الصوت والذكاء الاصطناعي:** Sherpa-ONNX (تشغيل موديل التعرف الصوتي محلياً).
* **الخدمات الخلفية والمتابعة:** Supabase, Firebase (Cloud Messaging, Crashlytics, Performance).
* **التقويم:** Umm al-Qura Calendar.

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

</div>

---

<div id="english-version">

## 🇬🇧 English Version

An offline-first Android Islamic app built with Jetpack Compose, featuring on-device AI for Quran memorization check, accurate prayer times, and media playback.

### Features

* **Offline AI Recitation Check:** Test Quran memorization with on-device speech recognition via Sherpa-ONNX. Audio is processed strictly in memory on the device.
* **Holy Quran & Tafsir:** Complete Uthmani text with exegesis, word search, and bookmarks.
* **Prayer Times & Athan:** Astronomical calculation engine with multiple methods, schools of thought, and exact alarm notifications.
* **Qiblah Compass:** Real-time Kaaba direction using device geomagnetic sensors with sensor calibration support.
* **Adhkar & Supplications:** Fortress of the Muslim (Hisn al-Muslim) with a built-in counter.
* **Faithful Companion:** Daily religious reminders and sunnah practices.
* **Live Quran Radio:** 24/7 online Islamic radio stations with background playback via Media3 ExoPlayer.
* **Home Widget:** Compact home screen widget showing upcoming prayer times and countdown.

### Tech Stack

* **UI:** Jetpack Compose, Material 3, Custom Design System, Coil.
* **Architecture:** Clean Architecture with MVI/MVVM (`presentation`, `domain`, `data`).
* **Dependency Injection:** Koin.
* **Local Storage:** Room Database, Jetpack DataStore.
* **Media & Audio:** AndroidX Media3 ExoPlayer.
* **Speech & AI Engine:** Sherpa-ONNX (Embedded on-device ASR).
* **Backend & Monitoring:** Supabase, Firebase (Cloud Messaging, Crashlytics, Performance).
* **Date & Calendar:** Umm al-Qura Calendar library.

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

This project is open-source under the [Apache 2.0 License](LICENSE).

</div>
