# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html
# Keep kotlinx-datetime classes used by Supabase Auth
-keep class kotlinx.datetime.** { *; }

# Keep serialization classes (you already have this, but keep it)
-keep class kotlinx.serialization.** { *; }

# Optional: if you still get warnings, you can add these as fallback
-dontwarn kotlinx.datetime.Clock$System
-dontwarn kotlinx.datetime.Instant
-dontwarn kotlinx.datetime.serializers.InstantIso8601Serializer

-keepattributes *Annotation*, InnerClasses, Signature

-keepclassmembers class **.*$Companion {
    *** Companion;
    kotlinx.serialization.KSerializer serializer(...);
}

-keepclassmembers,allowobfuscation,allowoptimization class * {
    @kotlinx.serialization.Serializable *;
}

-keep class * extends androidx.navigation.NavArgs { *; }

-keep enum * { *; }
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# ===================================================================
# Sherpa-ONNX & ONNX Runtime JNI Rules
# Critical for Release/AAB: Prevents R8 from obfuscating classes and
# native method signatures accessed by C++ JNI code.
# ===================================================================
-keep class com.k2fsa.sherpa.onnx.** { *; }
-keep class ai.onnxruntime.** { *; }
-keepclasseswithmembernames class * {
    native <methods>;
}
-keep class kotlin.jvm.functions.Function1 { *; }
-dontwarn com.k2fsa.sherpa.onnx.**
-dontwarn ai.onnxruntime.**