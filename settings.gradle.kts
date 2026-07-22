pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://maven.ghostscript.com")
    }
}

rootProject.name = "PaperShelf"

include(
    ":app",
    ":core",
    ":data",
    ":database",
    ":domain",
    ":library",
    ":reader",
    ":settings",
)
