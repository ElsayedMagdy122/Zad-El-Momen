<div align="center" dir="rtl">

  <a href="https://github.com/ElsayedMagdy122/Zad-El-Momen">
    <img src="docs/images/app_icon.png" alt="شعار تطبيق زاد المؤمن" width="96" height="96" />
  </a>

  # زاد المؤمن | Zad Al-Mu'min

  تطبيق إسلامي حديث لنظام أندرويد مفتوح المصدر ومبني بتقنية Jetpack Compose، يعمل دون إنترنت ويحتوي على ميزة تسميع القرآن بالذكاء الاصطناعي محلياً، ومواقيت صلاة دقيقة، ومشغل صوتيات وإذاعة قرآنية.

  <p>
    <a href="README.md">English</a> •
    <a href="#المميزات">المميزات</a> •
    <a href="#معرض-الشاشات">معرض الشاشات</a> •
    <a href="#التقنيات-المستخدمة">التقنيات</a> •
    <a href="#تشغيل-المشروع-محليا">التشغيل</a> •
    <a href="#المساهمة-في-المشروع">المساهمة</a> •
    <a href="#مصادر-البيانات-والحقوق">المصادر والحقوق</a>
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
      <img src="https://play.google.com/intl/ar_ar/badges/static/images/badges/ar_badge_web_generic.png" alt="متوفر على Google Play" height="50" />
    </a>
  </p>

  <br/>

  <img src="docs/images/banner.png" alt="بانر تطبيق زاد المؤمن" width="100%" />

</div>

## المميزات

* **مراجعة الحفظ بالذكاء الاصطناعي دون إنترنت:** تسميع الآيات والتحقق من صحتها عبر نموذج تعرف صوتي مدمج داخل الهاتف من خلال Sherpa-ONNX، وتتم معالجة الصوت في ذاكرة الجهاز دون إرسال أي تسجيل لخوادم خارجية.
* **القرآن الكريم والتفسير:** المصحف الشريف كاملاً بالرسم العثماني مع التفسير، وإمكانية البحث في الآيات وحفظ العلامات المرجعية.
* **مواقيت الصلاة والأذان:** حسابات فلكية دقيقة مع دعم طرق الحساب والمذاهب وتنبيهات أذان منضبطة عبر منبهات النظام.
* **بوصلة القبلة:** تحديد اتجاه الكعبة المشرفة في الوقت الفعلي باستخدام حساسات الهاتف مع دليل لمعايرة الحساس.
* **الأذكار والأدعية:** أذكار الصباح والمساء وحصن المسلم مع سبحة إلكترونية.
* **رفيق المؤمن:** خواطر وتذكيرات إيمانية وسنن يومية مناسبة لأوقات اليوم.
* **إذاعة القرآن الكريم:** بث مباشر لإذاعات القرآن الكريم على مدار 24 ساعة مع تشغيل مستقر في الخلفية عبر Media3 ExoPlayer.
* **ويدجت الشاشة الرئيسية:** ويدجت خفيف لعرض مواعيد الصلاة والوقت المتبقي للصلاة القادمة.

## معرض الشاشات

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

## التقنيات المستخدمة

* **الواجهة:** Jetpack Compose, Material 3, Custom Design System, Coil.
* **المعمارية البرمجية:** Clean Architecture مع نمط MVI/MVVM مقسمة إلى ثلاث طبقات (`presentation`, `domain`, `data`).
* **حقن الاعتماديات:** Koin.
* **التخزين المحلي:** Room Database, Jetpack DataStore.
* **مشغل الوسائط:** AndroidX Media3 ExoPlayer.
* **محرك الصوت والذكاء الاصطناعي:** Sherpa-ONNX (تشغيل موديل التعرف الصوتي محلياً).
* **الخدمات الخلفية والمتابعة:** Supabase, Firebase (Cloud Messaging, Crashlytics, Performance).
* **التقويم:** Umm al-Qura Calendar.

## تشغيل المشروع محلياً

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

## المساهمة في المشروع

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

للمزيد من التفاصيل، راجع دليل المساهمة [CONTRIBUTING.md](CONTRIBUTING.md) وميثاق السلوك [CODE_OF_CONDUCT.md](CODE_OF_CONDUCT.md).

## مصادر البيانات والحقوق

* **نص القرآن الكريم:** مجمع الملك فهد لطباعة المصحف الشريف ([موقع المجمع الرسمي](https://qurancomplex.gov.sa/)).
* **تفسير القرآن الكريم:** مجتمع إتقان للبرمجيات الإسلامية ([itqan.org](https://itqan.org/)).
* **صوتيات وتلاوات القرآن الكريم:** موقع [quranmp3.com](https://quranmp3.com/).
* **موديل الذكاء الاصطناعي للتسميع:** [Quralab.ai](https://quralab.ai/) مدمج من خلال [Sherpa-ONNX](https://github.com/k2-fsa/sherpa-onnx).

## الترخيص

هذا المشروع متاح للجميع تحت رخصة [Apache 2.0](LICENSE).
