# 📖 دليل المرجع الأصلي — زاد المؤمن (Android → CMP Migration Reference)

> **الغرض من هذا الملف:** مرجع ثابت لأي أداة ذكاء اصطناعي أو مطور يعمل على ترحيل مشروع زاد المؤمن من Android إلى Compose Multiplatform (CMP).
> المصدر الأصلي: `E:\Zad-El-Momen-Original` (تطبيق Android نقي — المرجع الذهبي).
> المشروع الجديد: `E:\Zad-El-Momen` (CMP multiplatform — الهدف).

---

## 🔴 القاعدة الذهبية الصارمة

> [!CAUTION]
> **السلوك والديزاين يجب أن يكونا مطابقَيْن 100% للأصل بعد الترحيل — لا تحسينات، لا تعديلات في الواجهة، لا تغيير في تدفق التنقل.**
>
> المسموح به فقط:
> - استبدال مكتبات `androidx.compose.*` بـ `org.jetbrains.compose.*`
> - استبدال `androidx.navigation` بـ `org.jetbrains.androidx.navigation`
> - استبدال `androidx.lifecycle.ViewModel` بـ `org.jetbrains.androidx.lifecycle.ViewModel`
> - استبدال `coil-compose 2.x` بـ `coil3-compose 3.x`
> - استبدال موارد `R.*` بـ `Res.*` من `composeResources`
> - فصل كود الأندرويد في `androidMain` وكود iOS في `iosMain`
> - **كل شيء آخر يبقى مطابقاً للأصل حرفياً**

---

## 🗺️ خريطة المشروع الأصلي

```
E:\Zad-El-Momen-Original\
└── app/src/main/java/dev/sayed/mehrabalmomen/
    ├── data/              ← طبقة البيانات (Room, Ktor, Supabase, Repositories)
    ├── domain/            ← طبقة المجال (Interfaces, UseCases, Models)
    ├── presentation/      ← طبقة العرض (Screens, ViewModels, Navigation)
    └── design_system/     ← نظام التصميم (Colors, Typography, Components)
```

---

## 🏗️ هيكل موديولات مشروع CMP الهدف

```
E:\Zad-El-Momen\
├── :domain/        ← Pure Kotlin KMP (commonMain فقط، صفر مكتبات Android)
├── :designSystem/  ← Compose Multiplatform (commonMain + composeResources)
├── :data/          ← KMP (commonMain + androidMain + iosMain)
├── :presentation/  ← Compose Multiplatform (commonMain + composeResources)
├── :shared/        ← KMP DI Glue (Koin modules + MehrabApp composable)
└── :app/           ← Android Application Target (Receivers, Services, Widget)
```

---

## 📦 الشاشات الكاملة (يجب ترحيل كل واحدة بدون تغيير سلوكها)

| الشاشة | المسار الأصلي | الموديول المستهدف |
|:---|:---|:---|
| `HomeScreen` | `presentation/screen/home/` | `:presentation` |
| `SurahListScreen` | `presentation/screen/quran/SurahList/` | `:presentation` |
| `SurahAyatScreen` | `presentation/screen/quran/SurahAyat/` | `:presentation` |
| `Tasmee3Screen` (التسميع) | `presentation/screen/quran/tasmee3/` | `:presentation` (Android + iOS مدعوم بالكامل) |
| `RecitersScreen` | `presentation/screen/quran/reciters/` | `:presentation` |
| `RecitersSearchScreen` | `presentation/screen/quran/reciters_search/` | `:presentation` |
| `SearchAyahScreen` | `presentation/screen/SearchAyah/` | `:presentation` |
| `AzkarScreen` | `presentation/screen/azkar/` | `:presentation` |
| `AzkarDetailsScreen` | `presentation/screen/AzkarDetails/` | `:presentation` |
| `QiblahScreen` | `presentation/screen/qiblah/` | `:presentation` |
| `PrayerTimesScreen` | `presentation/screen/prayers/` | `:presentation` |
| `RadioScreen` | `presentation/screen/radio/` | `:presentation` |
| `MapsScreen` | `presentation/screen/maps/` | `:presentation` |
| `SettingsScreen` | `presentation/screen/settings/` | `:presentation` |
| `ContactUsScreen` | `presentation/screen/settings/contact_us/` | `:presentation` |
| `FAQScreen` | `presentation/screen/settings/faq/` | `:presentation` |
| `PrivacyPolicyScreen` | `presentation/screen/settings/privacy/` | `:presentation` |
| `BookmarksListScreen` | `presentation/screen/bookmarks/` | `:presentation` |
| `ReminderSettingsScreen` | `presentation/screen/reminders/` | `:presentation` |
| `ReportBugScreen` | `presentation/screen/ReportBug/` | `:presentation` |
| `CompanionOverlay` | `presentation/screen/companion/` | `:presentation` |
| `PermissionsScreen` (Onboarding) | `presentation/screen/onBoarding/permissions/` | `:presentation` |
| `CalculationMethodScreen` (Onboarding) | `presentation/screen/onBoarding/calculation_method/` | `:presentation` |
| `BatteryOptimizationScreen` (Onboarding) | `presentation/screen/onBoarding/batteryOptimization/` | `:presentation` |
| `Figure8CalibrationScreen` | `presentation/screen/calibrate_device/` | `:presentation` |
| `AzanScreen` + `AzanActivity` | `presentation/screen/azan/` | `:app` (Android-only) |
| `HighlightingViewModel` | `presentation/recitation/` | `:presentation` |

---

## ⚙️ المكونات المنصية الأندرويد (تبقى في `:app` — لا ترحيل لـ iOS)

| المكون | المسار الأصلي | ملاحظات |
|:---|:---|:---|
| `PrayerTimesWidgetProvider` | `presentation/widget/` | RemoteViews — أندرويد فقط |
| `PrayerTimesWidgetPinReceiver` | `presentation/widget/` | أندرويد فقط |
| `BootReceiver` | `presentation/reciver/` | أندرويد فقط |
| `AzanAlarmReceiver` | `presentation/reciver/` | أندرويد فقط |
| `DailyRefreshReceiver` | `presentation/reciver/` | أندرويد فقط |
| `ReminderAlarmReceiver` | `presentation/reciver/` | أندرويد فقط |
| `PrayerAlarmService` | `presentation/service/` | Foreground Service — أندرويد فقط |
| `MyFirebaseMessagingService` | `presentation/service/` | FCM — أندرويد فقط |
| `RadioPlayerService` | `presentation/screen/radio/player/` | Media3 Service — أندرويد فقط |
| `AzanActivity` | `presentation/screen/azan/` | Activity — أندرويد فقط |
| `MainActivity` | `presentation/base/` | يُبقى في `:app` يستدعي `MehrabApp()` |
| `MehrabApplication` | `presentation/base/` | Koin init — أندرويد فقط |

---

## 🔄 جدول استبدال المكتبات (Android → CMP)

| المكتبة الأصلية (Android) | البديل CMP | الموديول |
|:---|:---|:---|
| `androidx.compose.*` | `org.jetbrains.compose.*` | commonMain |
| `androidx.compose.material3` | `org.jetbrains.compose.material3` | commonMain |
| `androidx.navigation:navigation-compose` | `org.jetbrains.androidx.navigation:navigation-compose` | commonMain |
| `androidx.lifecycle:lifecycle-viewmodel-compose` | `org.jetbrains.androidx.lifecycle:lifecycle-viewmodel-compose` | commonMain |
| `io.coil-kt:coil-compose:2.7.0` | `io.coil-kt.coil3:coil-compose:3.0.4` + `coil-network-ktor3` | commonMain |
| `androidx.room:room-*` | `androidx.room:room-runtime:2.8.4` + `androidx-sqlite-bundled` | KMP |
| `koin-android` + `koin-androidx-compose` | `koin-compose` + `koin-compose-viewmodel` | commonMain |
| `kotlinx-datetime` | `kotlinx-datetime` (بدون تغيير) | commonMain |
| `kotlinx-serialization-json` | `kotlinx-serialization-json` (بدون تغيير) | commonMain |
| `ktor-client-android` | `ktor-client-darwin` (iOS) + `ktor-client-android` (Android) | منصي |
| `androidx.datastore:datastore-preferences` | `datastore-preferences` (expect/actual) | منصي |
| `play-services-location` | يبقى في `androidMain` | Android-only |
| `billing-ktx` | يبقى في `androidMain` | Android-only |
| `media3-exoplayer` | يبقى في `androidMain` | Android-only |
| `work-runtime-ktx` | يبقى في `androidMain` | Android-only |
| `sherpa-onnx-2.0.1.aar` | يبقى في `androidMain` | Android-only |
| `maplibre-compose` | يبقى في `androidMain` (أو KMP wrapper إن وُجد) | Android-only |
| `com.github.msarhan:ummalqura-calendar` | يبقى في `androidMain` (تُنشأ interface في domain) | منصي |
| `firebase-*` | يبقى في `androidMain` | Android-only |

---

## 📁 الموارد الأصلية وأماكن ترحيلها

| نوع المورد | المسار الأصلي | المسار الجديد في CMP |
|:---|:---|:---|
| `strings.xml` (EN + AR) | `res/values/strings.xml` + `res/values-ar/strings.xml` | `presentation/src/commonMain/composeResources/values/strings.xml` |
| `font/*.ttf` | `res/font/hafs.ttf`, `poppins_*.ttf` | `designSystem/src/commonMain/composeResources/font/` |
| `drawable/*.xml` + `*.png` + `*.webp` | `res/drawable/` | `presentation/src/commonMain/composeResources/drawable/` |
| `raw/*.mp3` (أصوات الأذان) | `res/raw/` | `presentation/src/commonMain/composeResources/files/raw/` |
| `layout/widget_prayer_times.xml` | `res/layout/` | يبقى في `:app/src/main/res/layout/` (Widget — Android-only) |
| `xml/network_security_config.xml` | `res/xml/` | يبقى في `:app/src/main/res/xml/` |
| `mipmap-*/ic_launcher.*` | `res/mipmap-*/` | يبقى في `:app/src/main/res/mipmap-*/` |

---

## 🏛️ قواعد المعمارية Interface vs expect/actual

| المكون | النمط المعتمد | السبب |
|:---|:---|:---|
| `AudioPlayer` (ExoPlayer/AVPlayer) | `Interface + Impl` في `domain`/`data` | Stateful Service |
| `LocationRepository` (GPS) | `Interface + Impl` | Stateful Flow |
| `CompassSensorManager` (Qiblah) | `Interface + Impl` | Stateful Sensor |
| `SherpaEngine` (AI Recitation) | `Interface + Impl` | Stateful + Android AAR |
| `BillingManager` (Play Billing) | `Interface + Impl` | Platform Store |
| `AlarmScheduler` / `NotificationScheduler` | `Interface + Impl` | Platform OS Services |
| `HijriCalendarProvider` | `Interface + Impl` | Library abstraction |
| `DatabaseBuilder` (Room) | `expect / actual` | Pure factory, no state |
| `DataStoreFactory` | `expect / actual` | Pure factory, no state |

---

## 🗂️ قواعد تنظيم الكود في CMP

```
data/src/
├── commonMain/kotlin/     ← Room DAOs, DTOs, Mappers, Remote sources, Repositories
├── androidMain/kotlin/    ← AndroidAudioPlayer, SherpaEngine, CompassSensorManager,
│                             LocationRepositoryImpl, BillingManager, AlarmScheduler,
│                             NotificationScheduler, AndroidAnalyticsTracker,
│                             DatabaseBuilder.android.kt, DataStoreFactory.android.kt
└── iosMain/kotlin/        ← IosAudioPlayer (AVPlayer), IosRecitationEngineStub,
                              IosLocationRepository, DatabaseBuilder.ios.kt,
                              DataStoreFactory.ios.kt

presentation/src/
└── commonMain/kotlin/     ← كل الشاشات والـ ViewModels والـ Navigation
    composeResources/      ← strings, drawables, fonts, raw files
```

---

## 📋 نسخ المكتبات الأصلية (للمرجع)

| المكتبة | الإصدار الأصلي (Android) |
|:---|:---|
| `kotlin` | `2.2.21` |
| `agp` | `8.13.1` |
| `composeBom` | `2024.09.00` |
| `koin-bom` | `4.1.1` |
| `navigationCompose` | `2.9.6` |
| `room` | `2.8.4` |
| `media3-exoplayer` | `1.9.2` |
| `work-runtime-ktx` | `2.11.2` |
| `coilCompose` | `2.7.0` → يصبح `3.x` في CMP |
| `adhan2` | `0.0.6` (تدعم KMP — تبقى بدون تغيير) |
| `supabase-bom` | `3.1.4` |
| `firebase-bom` | `34.6.0` |
| `ktor-client-android` | `3.4.0` |
| `kotlinx-datetime` | `0.7.1` |
| `kotlinx-serialization-json` | `1.7.3` |
| `datastorePreferences` | `1.1.1` |
| `billing-ktx` | `8.3.0` |
| `maplibre-compose` | `0.11.1` |
| `play-services-location` | `21.0.1` |
| `minSdk` | `26` |
| `targetSdk` | `36` |
| `versionCode` | `26` |
| `versionName` | `1.2.0` |

---

## 🔐 المفاتيح والإعدادات السرية

البيانات الحساسة التالية تُحمل من `local.properties` أو Environment Variables:
- `SUPABASE_URL`
- `SUPABASE_KEY`
- `SUPPORT_1`, `SUPPORT_5`, `SUPPORT_10`, `SUPPORT_25`, `SUPPORT_100` (منتجات التبرع)

في CMP تُحمل هذه القيم عبر **BuildKonfig plugin** في الموديول المناسب.

---

## ⚠️ ملاحظات مهمة على ملفات ضخمة وحساسة

| الملف | الحجم | ملاحظة |
|:---|:---|:---|
| `HighlightingViewModel.kt` | 467 سطر | أضخم ViewModel — مزامنة صوت+نص+تظليل. انقله حرفياً. |
| `SurahAyatViewModel.kt` | 607 سطر | منطق معقد للتلاوة والإشارات. انقله حرفياً. |
| `SettingsViewModel.kt` | 657 سطر | أكبر ViewModel. انقله حرفياً. |
| `DictationSequencer.kt` | 453 سطر | Pure Kotlin → `commonMain` |
| `QuranDictationMatcher.kt` | 288 سطر | Pure Kotlin → `commonMain` |
| `BatteryOptimizationLauncher.kt` | 255 سطر | Android-only → `androidMain` |
| `PrayerTimesWidgetProvider.kt` | 282 سطر | RemoteViews → يبقى في `:app` |
| `TajweedRules.kt` / `ErrorExplainer.kt` / `ReciterMistake.kt` | 119/308/128 سطر | Pure Kotlin → `commonMain` |
| `AzkarDetailsScreen.kt` | 364 سطر | Compose UI → `commonMain` |
| `TilawahBox.kt` | 343 سطر | Compose UI → `commonMain` |
| `RecitationDownloadDialog.kt` | 338 سطر | Compose UI → `commonMain` |
| `RecitationCompletionBottomSheet.kt` | 347 سطر | Compose UI → `commonMain` |
| `Tasmee3FloatingControlBar.kt` | 334 سطر | Compose UI → `commonMain` |
| `MainContainer.kt` | 221 سطر | Navigation host → `commonMain` |
| `Route.kt` | 155 سطر | Navigation routes → `commonMain` |
| `AzanScreen.kt` | 382 سطر | Android-only Activity → `:app` |
| `PrayerAlarmService.kt` | 197 سطر | Foreground Service → `:app` |

---

## ✅ قائمة التحقق النهائية (يجب اجتيازها 100% قبل حذف الأصل)

- [ ] قراءة القرآن الكريم + التلاوات الصوتية + التظليل بالألوان
- [x] التسميع بالذكاء الاصطناعي (Tasmee3) — يعمل على Android (Sherpa-ONNX) و iOS (SFSpeechRecognizer) بنفس السلوك 100%
- [ ] الأذكار + السبحة + الإحصائيات المحلية
- [ ] حساب مواقيت الصلاة + المنبهات + الأذان في الخلفية
- [ ] اتجاه القبلة (البوصلة)
- [ ] الخرائط + الموقع الجغرافي
- [ ] ويدجت مواقيت الصلاة (الشاشة الرئيسية — Android فقط)
- [ ] الراديو الإسلامي (Streaming + Media3 Service)
- [ ] التذكيرات + المنبهات المخصصة
- [ ] الإعدادات + تخصيص المظهر
- [ ] المفضلة (Bookmarks)
- [ ] رفيق المؤمن (Companion AI)
- [ ] الإبلاغ عن خطأ
- [ ] الصفحات التعريفية (Onboarding: Permissions + Calculation Method + Battery)
- [ ] شاشة الأذان المنبثقة (AzanActivity — Android فقط)
- [ ] FCM + الإشعارات البعيدة
- [ ] نظام التبرع (Google Play Billing — Android فقط)

---

*آخر تحديث: نوفمبر 2026 — مشروع زاد المؤمن*
