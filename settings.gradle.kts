pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/test")
    }
}

rootProject.name = "Zad-El-Momen" // Removed spaces to avoid DEX error

// App Modules
include(":androidApp")
project(":androidApp").projectDir = file("app")

// KMP Independent Modules
include(":shared")
include(":domain")
include(":data")
include(":presentation")
include(":core:design-system")
