import org.gradle.kotlin.dsl.compileOnly

plugins {
    id("java-library")
    alias(libs.plugins.jetbrains.kotlin.jvm)
}
java {
    sourceCompatibility = JavaVersion.VERSION_11

    targetCompatibility = JavaVersion.VERSION_11
}
kotlin {
    compilerOptions {
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11
    }
}
dependencies {
    compileOnly ("com.android.tools.lint:lint-api:31.9.2")
    compileOnly ("com.android.tools.lint:lint-checks:31.9.2")
}
tasks.register<Jar>("lintJar") {
    archiveClassifier.set("lint")
    from(sourceSets.main.get().output)
    manifest {
        attributes["Lint-Registry-v2"] = "dev.sayed.lint_rules.LintRegistry"
    }
}

artifacts {
    archives(tasks["lintJar"])
}