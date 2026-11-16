import java.util.Properties

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.buildkonfig)
    alias(libs.plugins.ksp)
    alias(libs.plugins.androidx.room)
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

fun getLocalProperty(key: String): String {
    return System.getenv(key)
        ?: localProperties.getProperty(key)?.removeSurrounding("\"")
        ?: ""
}

val supabaseUrl = getLocalProperty("SUPABASE_URL")
val supabaseKey = getLocalProperty("SUPABASE_KEY")
val support1 = getLocalProperty("SUPPORT_1")
val support5 = getLocalProperty("SUPPORT_5")
val support10 = getLocalProperty("SUPPORT_10")
val support25 = getLocalProperty("SUPPORT_25")
val support100 = getLocalProperty("SUPPORT_100")

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
        commonMain.dependencies {
            implementation(project(":domain"))

            // Room KMP
            implementation(libs.androidx.room.runtime)
            implementation(libs.androidx.sqlite.bundled)

            // DataStore
            implementation(libs.androidx.datastore.preferences.core)
            implementation(libs.androidx.datastore.preferences)

            // Ktor Client
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.ktor.client.logging)

            // Supabase
            implementation(libs.supabase.postgrest)
            implementation(libs.supabase.storage)
            implementation(libs.supabase.realtime)

            // Koin
            implementation(libs.koin.core)

            // Kotlinx
            implementation(libs.kotlinx.datetime)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.serialization.json)

            // Adhan KMP
            implementation(libs.adhan2)
        }

        androidMain.dependencies {
            implementation(libs.ktor.client.okhttp)
            implementation(libs.androidx.media3.exoplayer)
            implementation(libs.androidx.media3.session)
            implementation(libs.billing.ktx)
            implementation(libs.play.services.location)
            implementation(libs.firebase.analytics)
            implementation(libs.androidx.work.runtime.ktx)
            compileOnly(files("../app/libs/sherpa-onnx-2.0.1.aar"))
            implementation(libs.koin.android)
        }

        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
    }
}

dependencies {
    add("kspAndroid", libs.androidx.room.compiler)
    add("kspIosArm64", libs.androidx.room.compiler)
    add("kspIosSimulatorArm64", libs.androidx.room.compiler)
}
