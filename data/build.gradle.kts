plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.ksp)
    alias(libs.plugins.androidx.room)
}

kotlin {
    androidTarget {
        publishLibraryVariants("release")
    }
    
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        commonMain.dependencies {
            implementation(project(":domain"))
            implementation(libs.kotlinx.datetime)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.koin.core)
            
            // Networking
            implementation(libs.ktor.client.core)
            implementation(project.dependencies.platform(libs.supabase.bom))
            implementation(libs.supabase.postgrest)
            implementation(libs.supabase.realtime)
            implementation(libs.supabase.storage)
            implementation(libs.supabase.functions)
            
            // Local Database
            implementation(libs.androidx.room.runtime)
            implementation(libs.androidx.sqlite.bundled)
            implementation(libs.androidx.datastore.preferences)
            implementation(libs.adhan2)
        }
        
        androidMain.dependencies {
            implementation(libs.ktor.client.okhttp)
            implementation(libs.koin.android)
            implementation(libs.firebase.analytics)
            implementation(libs.billing.ktx)
            implementation(libs.play.services.location)
            implementation(libs.androidx.work.runtime.ktx)
        }
        
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
    }
}

android {
    namespace = "dev.sayed.mehrabalmomen.data"
    compileSdk = 36
    defaultConfig {
        minSdk = 26
    }
}

room {
    schemaDirectory("$projectDir/schemas")
}
