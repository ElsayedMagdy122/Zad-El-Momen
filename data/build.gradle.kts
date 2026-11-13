import java.util.Properties

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.buildkonfig)
    alias(libs.plugins.androidx.room)
    alias(libs.plugins.ksp)
}

room {
    schemaDirectory("$projectDir/schemas")
}

val localProperties = Properties().apply {
    val localFile = rootProject.file("local.properties")
    if (localFile.exists()) {
        load(localFile.inputStream())
    }
}

val supabaseUrl: String = System.getenv("SUPABASE_URL")
    ?: localProperties.getProperty("SUPABASE_URL")
    ?: ""
val supabaseKey: String = System.getenv("SUPABASE_KEY")
    ?: localProperties.getProperty("SUPABASE_KEY")
    ?: ""
val support1: String = localProperties.getProperty("SUPPORT_1") ?: "support_1"
val support5: String = localProperties.getProperty("SUPPORT_5") ?: "support_5"
val support10: String = localProperties.getProperty("SUPPORT_10") ?: "support_10"
val support25: String = localProperties.getProperty("SUPPORT_25") ?: "support_25"
val support100: String = localProperties.getProperty("SUPPORT_100") ?: "support_100"

buildkonfig {
    packageName = "dev.sayed.mehrabalmomen.data"
    defaultConfigs {
        buildConfigField(com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING, "SUPABASE_URL", supabaseUrl)
        buildConfigField(com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING, "SUPABASE_KEY", supabaseKey)
        buildConfigField(com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING, "SUPPORT_1", support1)
        buildConfigField(com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING, "SUPPORT_5", support5)
        buildConfigField(com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING, "SUPPORT_10", support10)
        buildConfigField(com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING, "SUPPORT_25", support25)
        buildConfigField(com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING, "SUPPORT_100", support100)
    }
}

kotlin {
    android {
        namespace = "dev.sayed.mehrabalmomen.data"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()
    }

    val xcfName = "dataKit"

    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = xcfName
        }
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":domain"))
                implementation(libs.kotlin.stdlib)
                implementation(libs.kotlinx.serialization.json)
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.kotlinx.datetime)
                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.content.negotiation)
                implementation(libs.ktor.serialization.kotlinx.json)
                implementation(libs.koin.core)
                implementation(libs.adhan2)

                // Supabase
                implementation(libs.supabase.postgrest)
                implementation(libs.supabase.auth)
                implementation(libs.supabase.storage)
                implementation(libs.supabase.realtime)

                // Room & Paging
                implementation(libs.androidx.room.runtime)
                implementation(libs.androidx.room.paging)
                implementation(libs.androidx.sqlite.bundled)
                implementation(libs.androidx.paging.common)
                implementation(libs.androidx.datastore.preferences.core)
            }
        }

        androidMain {
            dependencies {
                implementation(libs.ktor.client.okhttp)
                implementation(libs.androidx.media3.exoplayer)
                implementation(libs.androidx.media3.session)
                implementation(libs.koin.android)
                implementation(libs.billing.ktx)
                implementation(libs.play.services.location)
                implementation(libs.androidx.work.runtime.ktx)
                implementation(libs.firebase.analytics)
                compileOnly(fileTree("libs") { include("*.aar", "*.jar") })
            }
        }

        iosMain {
            dependencies {
                implementation(libs.ktor.client.darwin)
            }
        }
    }
}

dependencies {
    add("kspAndroid", libs.androidx.room.compiler)
    add("kspIosArm64", libs.androidx.room.compiler)
    add("kspIosSimulatorArm64", libs.androidx.room.compiler)
}
