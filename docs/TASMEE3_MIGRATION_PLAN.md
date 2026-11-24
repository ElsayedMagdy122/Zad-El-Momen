# خطة ترحيل فيتشر التسميع (Tasmee3) — Android → CMP (iOS + Android)

> **الهدف:** تشغيل فيتشر التسميع على iOS وAndroid بنفس السلوك 100%.
> **المرجع الذهبي:** `E:\Zad-El-Momen-Original`

---

## 🧠 تحليل المشكلة المعمارية

فيتشر التسميع يتكون من **طبقتين**:

```
┌─────────────────────────────────────────────────────────────┐
│          LAYER 1: Pure Kotlin Logic (commonMain ✅)          │
│  - AsrTokenProcessor       - DictationSequencer              │
│  - QuranVerse (data class) - TranscriptionResult             │
│  - ModelDownloadState      - WordStatus / RecitationUiState  │
│  - RecitationCompletionResult                                │
│  - TajweedRules / ErrorExplainer / ReciterMistake            │
│  - FuzzySearch / PhoneticCostEngine / LcsOmissionDetector    │
│  - QuranDictationMatcher / AsrTokenProcessor                 │
└─────────────────────────────────────────────────────────────┘
┌─────────────────────────────────────────────────────────────┐
│       LAYER 2: Platform-Specific (androidMain / iosMain)     │
│                                                              │
│  Android:                │  iOS:                            │
│  - SherpaEngine (AAR)    │  - AVAudioEngine (Core Audio)    │
│  - AudioPipeline         │  - Whisper / Apple STT           │
│    (android.AudioRecord) │    (SFSpeechRecognizer)          │
│  - RecitationDownloadSvc │  - BackgroundDownload (URLSession)│
│    (Foreground Service)  │                                  │
│  - AssetExtractor        │  - IosAssetExtractor             │
│    (context.filesDir)    │    (NSDocumentDirectory)         │
└─────────────────────────────────────────────────────────────┘
```

---

## ⚠️ قرار معماري: لماذا Interface + Impl؟

| المعيار | Interface + Impl | expect/actual |
|:---|:---|:---|
| `SherpaEngine` / `AVAudioEngine` | ✅ مناسب — كل منصة لها impl مختلف كلياً | ❌ |
| `AudioPipeline` (stream بيانات) | ✅ stateful service | ❌ |
| `RecitationModelDownloader` | ✅ منطق تنزيل + supabase | ❌ |
| `AssetExtractor` (pure factory) | — | ✅ expect/actual |

---

## 📁 الملفات المطلوب إنشاؤها — مرتبة حسب الموديول

### الموديول `:domain` — Interfaces فقط

```
domain/src/commonMain/kotlin/dev/sayed/mehrabalmomen/domain/repository/recitation/
├── RecitationEngine.kt          ← Interface للـ ASR engine
├── RecitationAudioRecorder.kt   ← Interface لتسجيل الصوت
└── RecitationModelManager.kt    ← Interface لإدارة النموذج
```

---

### الموديول `:data` — commonMain (Pure Kotlin)

```
data/src/commonMain/kotlin/dev/sayed/mehrabalmomen/data/recitation/
├── engine/
│   ├── TranscriptionResult.kt     ← data class (بدون android.*)
│   ├── AsrTokenProcessor.kt       ← pure Kotlin ✅ — نقل مباشر
│   ├── ModelDownloadState.kt      ← sealed class (بدون android.*)
│   └── RecitationModelDownloader.kt ← Interface impl منصي
├── data/
│   ├── QuranVerse.kt              ← data class (بدون android.*)
│   └── QuranRepository.kt         ← Interface (commonMain)
├── tracking/
│   ├── DictationSequencer.kt      ← pure Kotlin ✅ — نقل مباشر
│   ├── PhoneticCostEngine.kt      ← pure Kotlin ✅ — نقل مباشر
│   ├── LcsOmissionDetector.kt     ← pure Kotlin ✅ — نقل مباشر
│   └── QuranDictationMatcher.kt   ← pure Kotlin ✅ — نقل مباشر
├── search/
│   └── FuzzySearch.kt             ← pure Kotlin ✅ — نقل مباشر
└── tajweed/
    ├── TajweedRules.kt            ← pure Kotlin ✅ — نقل مباشر
    ├── ErrorExplainer.kt          ← pure Kotlin ✅ — نقل مباشر
    └── ReciterMistake.kt          ← pure Kotlin ✅ — نقل مباشر
```

---

### الموديول `:data` — androidMain (Android-specific)

```
data/src/androidMain/kotlin/dev/sayed/mehrabalmomen/data/recitation/
├── engine/
│   ├── SherpaEngine.kt            ← بدون تغيير (يستخدم AAR)
│   ├── AssetExtractor.kt          ← context.filesDir → actual impl
│   └── AndroidRecitationEngine.kt ← Impl لـ RecitationEngine interface
├── audio/
│   ├── AudioPipeline.kt           ← بدون تغيير (android.media.AudioRecord)
│   ├── Float32List.kt             ← helper class
│   └── AndroidAudioRecorder.kt    ← Impl لـ RecitationAudioRecorder interface
├── downloader/
│   └── AndroidModelDownloader.kt  ← Impl يستخدم context.filesDir + Supabase
└── service/
    └── RecitationDownloadService.kt ← Foreground Service — بدون تغيير
```

---

### الموديول `:data` — iosMain (iOS-specific)

```
data/src/iosMain/kotlin/dev/sayed/mehrabalmomen/data/recitation/
├── engine/
│   ├── IosRecitationEngine.kt     ← Impl لـ RecitationEngine interface
│   │                                 يستخدم SFSpeechRecognizer (Apple STT)
│   └── IosAssetExtractor.kt       ← NSDocumentDirectory → actual impl
├── audio/
│   └── IosAudioRecorder.kt        ← Impl يستخدم AVAudioEngine
└── downloader/
    └── IosModelDownloader.kt      ← Impl يستخدم URLSession + NSDocumentDirectory
```

---

### الموديول `:presentation` — commonMain (Compose UI)

```
presentation/src/commonMain/kotlin/dev/sayed/mehrabalmomen/presentation/
├── recitation/
│   └── HighlightingViewModel.kt   ← يُحقن RecitationEngine بدل SherpaEngine
└── screen/quran/tasmee3/
    ├── Tasmee3UiState.kt          ← بدون تغيير (pure Kotlin)
    ├── Tasmee3Screen.kt           ← بدون تغيير في الـ UI
    └── components/                ← كل الـ UI components بدون تغيير
```

---

## 🔌 تعريف الـ Interfaces (domain layer)

### `RecitationEngine.kt`
```kotlin
interface RecitationEngine {
    val transcriptionStream: SharedFlow<TranscriptionResult>
    val currentStreamEpoch: Int
    fun initialize(): Boolean
    fun transcribe(chunk: Float32List, isFinal: Boolean = false): Boolean
    fun resetBuffer()
    fun destroy()
}
```

### `RecitationAudioRecorder.kt`
```kotlin
interface RecitationAudioRecorder {
    val audioChunks: SharedFlow<Float32List>
    fun startStreaming(onError: (Exception) -> Unit = {})
    fun stopStreaming()
}
```

### `RecitationModelManager.kt`
```kotlin
interface RecitationModelManager {
    val downloadState: Flow<ModelDownloadState>
    fun isModelReady(): Boolean
    fun isDownloading(): Boolean
    suspend fun downloadAndPrepareModels(): Boolean
    fun resetState()
}
```

---

## 🔄 التغييرات المطلوبة في `HighlightingViewModel`

| ما يتغير | قبل (Android) | بعد (CMP) |
|:---|:---|:---|
| `extends AndroidViewModel` | `AndroidViewModel(application)` | `ViewModel()` (KMP) |
| `SherpaEngine(application)` | مُنشأ يدوياً | `RecitationEngine` مُحقن من Koin |
| `AudioPipeline()` | مُنشأ يدوياً | `RecitationAudioRecorder` مُحقن |
| `RecitationModelDownloader(context, ...)` | مُنشأ يدوياً | `RecitationModelManager` مُحقن |
| `QuranRepository(application)` | يستخدم `Context` | `QuranRepository` مُعاد كتابته بـ Okio/composeResources |
| `RecitationDownloadService.start(getApplication())` | Foreground Service | `modelManager.downloadAndPrepareModels()` مباشرة (iOS) أو Service (Android) عبر expect/actual |
| `R.string.*` | استيراد Android | `getString(Res.string.*)` |

---

## 📱 استراتيجية iOS للـ ASR

على iOS لا يوجد Sherpa-ONNX. البديل المتاح:

| الخيار | الدقة | الأداء | الملاحظات |
|:---|:---|:---|:---|
| **Apple SFSpeechRecognizer** (محلي) | متوسطة للعربية | ممتاز | متاح offline بعد iOS 13 |
| **Apple SFSpeechRecognizer** (online) | جيدة | جيد | يحتاج اتصال |
| **WhisperKit** (open source) | ممتازة للعربية | جيد | مكتبة KMP wrapper متاحة |

**القرار المعتمد:** `SFSpeechRecognizer` في المرحلة الأولى (أبسط + لا تبعيات خارجية)، مع الإشارة للمستخدم أن دقة التسميع على iOS قد تختلف قليلاً عن Android.

---

## 🗂️ ملف QuranRepository — المشكلة والحل

### المشكلة:
```kotlin
// Android — يستخدم android.content.Context
class QuranRepository(private val context: Context) {
    fun loadData() {
        // Priority 1: internal storage (context.filesDir)
        val internalFile = java.io.File(context.filesDir, "ordered_quran_phonemes.json")
        // Priority 2: APK assets
        context.assets.open("ordered_quran_phonemes.json")
    }
}
```

### الحل في CMP:
```kotlin
// commonMain — يستخدم composeResources
class QuranRepositoryImpl : QuranRepository {
    suspend fun loadData() {
        // الملف موجود في: presentation/composeResources/files/ordered_quran_phonemes.json
        // يُقرأ عبر:
        val bytes = Res.readBytes("files/ordered_quran_phonemes.json")
        val jsonString = bytes.decodeToString()
    }
}
```

> [!IMPORTANT]
> ملف `ordered_quran_phonemes.json` موجود بالفعل في:
> `presentation/src/commonMain/composeResources/files/ordered_quran_phonemes.json`
> وملف `tokens.txt` موجود أيضاً في نفس المكان ✅

---

## 📋 تسلسل التنفيذ خطوة بخطوة

### الخطوة 1: نقل Pure Kotlin إلى commonMain (بدون أي تعديل)
**المطور ينقل هذه الملفات** من `Zad-El-Momen-Original` إلى `data/src/commonMain/`:

| الملف الأصلي | الوجهة |
|:---|:---|
| `data/recitation/engine/AsrTokenProcessor.kt` | `data/src/commonMain/.../data/recitation/engine/` |
| `data/recitation/engine/TranscriptionResult.kt` | `data/src/commonMain/.../data/recitation/engine/` |
| `data/recitation/engine/ModelDownloadState.kt` | `data/src/commonMain/.../data/recitation/engine/` |
| `data/recitation/data/QuranVerse.kt` (data class فقط) | `data/src/commonMain/.../data/recitation/data/` |
| `data/recitation/tracking/DictationSequencer.kt` | `data/src/commonMain/.../data/recitation/tracking/` |
| `data/recitation/tracking/PhoneticCostEngine.kt` | `data/src/commonMain/.../data/recitation/tracking/` |
| `data/recitation/tracking/LcsOmissionDetector.kt` | `data/src/commonMain/.../data/recitation/tracking/` |
| `data/recitation/tracking/QuranDictationMatcher.kt` | `data/src/commonMain/.../data/recitation/tracking/` |
| `data/recitation/search/FuzzySearch.kt` | `data/src/commonMain/.../data/recitation/search/` |
| `data/recitation/tajweed/TajweedRules.kt` | `data/src/commonMain/.../data/recitation/tajweed/` |
| `data/recitation/tajweed/ErrorExplainer.kt` | `data/src/commonMain/.../data/recitation/tajweed/` |
| `data/recitation/tajweed/ReciterMistake.kt` | `data/src/commonMain/.../data/recitation/tajweed/` |
| `presentation/screen/quran/tasmee3/Tasmee3UiState.kt` | `presentation/src/commonMain/.../screen/quran/tasmee3/` |

**الأداة (AI) ستفعل:** إصلاح الـ imports فقط (`android.` → KMP بدائل إن وُجدت).

---

### الخطوة 2: الأداة تُنشئ الـ Interfaces في domain
- `RecitationEngine.kt`
- `RecitationAudioRecorder.kt`
- `RecitationModelManager.kt`
- `QuranRepository.kt` (interface)

### الخطوة 3: نقل الملفات Android-specific إلى androidMain
**المطور ينقل:**

| الملف | الوجهة |
|:---|:---|
| `data/recitation/engine/SherpaEngine.kt` | `data/src/androidMain/.../data/recitation/engine/` |
| `data/recitation/engine/AssetExtractor.kt` | `data/src/androidMain/.../data/recitation/engine/` |
| `data/recitation/audio/AudioPipeline.kt` | `data/src/androidMain/.../data/recitation/audio/` |
| `data/recitation/engine/RecitationModelDownloader.kt` | `data/src/androidMain/.../data/recitation/downloader/` |
| `data/recitation/engine/RecitationDownloadService.kt` | `app/src/main/kotlin/.../service/` |
| `data/recitation/data/QuranRepository.kt` (class) | `data/src/androidMain/.../data/recitation/data/` |

**الأداة ستفعل:**
- تُنشئ `AndroidRecitationEngine` (تُغلّف `SherpaEngine` وتطبق `RecitationEngine`)
- تُنشئ `AndroidAudioRecorder` (تُغلّف `AudioPipeline` وتطبق `RecitationAudioRecorder`)
- تُنشئ `AndroidModelManager` (تُغلّف `RecitationModelDownloader` وتطبق `RecitationModelManager`)
- تُنشئ `AndroidQuranRepository` (تطبق `QuranRepository` بـ `context.filesDir`)

### الخطوة 4: الأداة تُنشئ iOS stubs في iosMain
- `IosRecitationEngine` بـ `SFSpeechRecognizer`
- `IosAudioRecorder` بـ `AVAudioEngine`
- `IosModelDownloader` بـ `URLSession`
- `IosQuranRepository` يقرأ من `NSDocumentDirectory`

### الخطوة 5: الأداة تُعدّل `HighlightingViewModel`
- يستبدل `AndroidViewModel` بـ `ViewModel` من KMP
- يحقن الـ interfaces بدل الـ implementations

### الخطوة 6: تحديث Koin DI
- إضافة bindings للـ interfaces في `androidMain` و `iosMain`

---

## ✅ نقاط التحقق

| النقطة | Android | iOS |
|:---|:---|:---|
| تحميل بيانات القرآن | قراءة `ordered_quran_phonemes.json` | قراءة `ordered_quran_phonemes.json` |
| التنزيل من Supabase | Foreground Service | URLSession background |
| تسجيل الصوت | `AudioRecord` (16kHz, Mono) | `AVAudioEngine` (16kHz, Mono) |
| ASR Engine | Sherpa-ONNX (AAR) | SFSpeechRecognizer |
| Token Processing | `AsrTokenProcessor` | `AsrTokenProcessor` (نفسه) |
| Word Matching | `DictationSequencer` | `DictationSequencer` (نفسه) |
| UI State | `RecitationUiState` | `RecitationUiState` (نفسه) |
| الديزاين | `Tasmee3Screen.kt` | `Tasmee3Screen.kt` (نفسه) |

---

*تاريخ الخطة: نوفمبر 2026 — مشروع زاد المؤمن*
