import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    kotlin("plugin.serialization") version "2.0.21"
    alias(libs.plugins.google.gms.google.services)
    alias(libs.plugins.google.firebase.crashlytics)
    alias(libs.plugins.google.firebase.firebase.perf)
}

android {
    namespace = "dev.sayed.mehrabalmomen"
    compileSdk {
        version = release(36)
    }
    bundle {
        language {
            enableSplit = true
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
        versionCode = 9
        versionName = "1.0"

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

        ndk {
            abiFilters += listOf("armeabi-v7a", "arm64-v8a")
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    //Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.bundles.compose.ui)

    // Navigation
    implementation(libs.bundles.navigation.compose)

    // Koin
    implementation(platform(libs.koin.bom))
    implementation(libs.bundles.koin)

    // Supabase
    implementation(platform(libs.bom))
    implementation(libs.bundles.supabase)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.bundles.firebase)

    // AndroidX
    implementation(libs.bundles.androidx.core)

    // Kotlinx & utilities
    implementation(libs.bundles.kotlinx)
    implementation(libs.bundles.network)

    // Maps & location
    implementation(libs.bundles.maps.location)

    // Coil
    implementation(libs.bundles.image.loading)

    // Others (domain specific)
    implementation(libs.bundles.others)

    // Debug only
    debugImplementation(libs.bundles.compose.debug)
//  lintChecks(project(":lint-rules"))
}