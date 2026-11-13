import java.util.Properties
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.googleGmsGoogleServices)
    alias(libs.plugins.googleFirebaseCrashlytics)
    alias(libs.plugins.googleFirebaseFirebasePerf)
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_11
    }
}

android {
    signingConfigs {
        create("release") {
            storeFile = file("keystore.jks")
            storePassword = System.getenv("KEY_STORE_PASSWORD")
            keyAlias = System.getenv("ALIAS")
            keyPassword = System.getenv("KEY_PASSWORD")
        }
    }

    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
        }
    }
    namespace = "dev.sayed.mehrabalmomen"
    compileSdk {
        version = release(36)
    }
    bundle {
        language {
            enableSplit = false
        }
        density {
            enableSplit = true
        }
        abi {
            enableSplit = true
        }
    }
    defaultConfig {
        applicationId = "dev.sayed.mehrabalmomen"
        minSdk = 26
        targetSdk = 36
        versionCode = 26
        versionName = "1.2.0"

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
        buildConfigField(
            "String",
            "SUPABASE_URL",
            "\"${getProp("SUPABASE_URL")}\""
        )
        buildConfigField(
            "String",
            "SUPABASE_KEY",
            "\"${getProp("SUPABASE_KEY")}\""
        )
        buildConfigField(
            "String",
            "SUPPORT_1",
            "\"${getProp("SUPPORT_1")}\""
        )
        buildConfigField(
            "String",
            "SUPPORT_5",
            "\"${getProp("SUPPORT_5")}\""
        )
        buildConfigField(
            "String",
            "SUPPORT_10",
            "\"${getProp("SUPPORT_10")}\""
        )
        buildConfigField(
            "String",
            "SUPPORT_25",
            "\"${getProp("SUPPORT_25")}\""
        )
        buildConfigField(
            "String",
            "SUPPORT_100",
            "\"${getProp("SUPPORT_100")}\""
        )

        ndk {
            abiFilters += listOf("armeabi-v7a", "arm64-v8a")
        }
    }
    buildTypes {
        debug {
            isMinifyEnabled = false
            isShrinkResources = false

            firebaseCrashlytics {
                mappingFileUploadEnabled=false
                nativeSymbolUploadEnabled=false
            }

        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            firebaseCrashlytics {
                mappingFileUploadEnabled=true
                nativeSymbolUploadEnabled=true
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":designSystem"))
    implementation(project(":data"))
    implementation(project(":presentation"))
    implementation(project(":shared"))

    //Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.bundles.compose.ui)

    // Koin
    implementation(platform(libs.koin.bom))
    implementation(libs.bundles.koin)

    // Supabase
    implementation(platform(libs.bom))
    implementation(libs.bundles.supabase)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.bundles.firebase)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.firebase.analytics)

    implementation("androidx.media:media:1.6.0")
    // AndroidX
    implementation(libs.bundles.androidx.core)

    // Kotlinx & utilities
    implementation(libs.bundles.kotlinx)
    implementation(libs.bundles.network)
    implementation("com.github.msarhan:ummalqura-calendar:1.1.9")
    // Maps & location
    implementation(libs.bundles.maps.location)

    // Coil
    implementation(libs.bundles.image.loading)

    // Others (domain specific)
    implementation(libs.bundles.others)
    implementation(files("libs/sherpa-onnx-2.0.1.aar"))
    implementation(libs.firebase.messaging)
    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidx.work.runtime.ktx)

    // Debug only
    debugImplementation(libs.bundles.compose.debug)
}