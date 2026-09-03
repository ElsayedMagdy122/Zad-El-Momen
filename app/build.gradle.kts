import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    id("com.google.firebase.firebase-perf")
    id("com.google.devtools.ksp")
    id("androidx.room")
}

android {
    namespace = "dev.sayed.mehrabalmomen"
    compileSdk = 36
    
    defaultConfig {
        applicationId = "dev.sayed.mehrabalmomen"
        minSdk = 26
        targetSdk = 36
        versionCode = 20
        versionName = "1.1.2"
        
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val localProperties = Properties()
        val localPropertiesFile = rootProject.file("local.properties")
        if (localPropertiesFile.exists()) {
            localPropertiesFile.inputStream().use {
                localProperties.load(it)
            }
        }

        fun getProp(key: String): String {
            return localProperties.getProperty(key)
                ?: System.getenv(key)
                ?: error("Missing property: $key")
        }
        buildConfigField("String", "SUPABASE_URL", "\"${getProp("SUPABASE_URL")}\"")
        buildConfigField("String", "SUPABASE_KEY", "\"${getProp("SUPABASE_KEY")}\"")
        buildConfigField("String", "SUPPORT_1", "\"${getProp("SUPPORT_1")}\"")
        buildConfigField("String", "SUPPORT_5", "\"${getProp("SUPPORT_5")}\"")
        buildConfigField("String", "SUPPORT_10", "\"${getProp("SUPPORT_10")}\"")
        buildConfigField("String", "SUPPORT_25", "\"${getProp("SUPPORT_25")}\"")
        buildConfigField("String", "SUPPORT_100", "\"${getProp("SUPPORT_100")}\"")

        ndk {
            abiFilters += listOf("armeabi-v7a", "arm64-v8a")
        }
    }
    
    buildFeatures {
        compose = true
        buildConfig = true
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    room {
        schemaDirectory("$projectDir/schemas")
    }
}

dependencies {
    // KMP Modules
    implementation(project(":domain"))
    implementation(project(":data"))
    implementation(project(":presentation"))
    implementation(project(":core:design-system"))
    implementation(project(":shared"))

    // AndroidX & UI
    implementation(libs.androidx.core.ktx)
    implementation(libs.material.v1120)
    implementation(libs.androidx.core.splashscreen)
    
    // Compose
    implementation(libs.androidx.activity.compose)
    
    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.perf)
    implementation(libs.firebase.messaging)

    // Navigation
    implementation(libs.androidx.navigation.compose)

    // Koin
    implementation(libs.koin.android)

    // Media3
    implementation(libs.androidx.media3.exoplayer)
    implementation("androidx.media:media:1.6.0")
    
    // Room
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)
}
