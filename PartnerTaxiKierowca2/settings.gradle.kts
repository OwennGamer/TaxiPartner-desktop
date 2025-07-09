// Ustawienie, gdzie Gradle ma szukać wtyczek
pluginManagement {
    repositories {
        google()                 // tutaj jest com.android.application
        gradlePluginPortal()     // oficjalne pluginy gradle
        mavenCentral()           // dodatkowe pluginy
    }
}

// Ustawienie, gdzie Gradle ma szukać zależności projektu
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "PartnerTaxiKierowca2"
include(":app")
