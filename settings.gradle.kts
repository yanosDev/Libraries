@file:Suppress("UnstableApiUsage")

include(":data")


val snapshotVersion: String? = System.getenv("COMPOSE_SNAPSHOT_ID")
pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        snapshotVersion?.let {
            println("https://androidx.dev/snapshots/builds/$it/artifacts/repository/")
            maven { url = uri("https://androidx.dev/snapshots/builds/$it/artifacts/repository/") }
        }

        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}
rootProject.name = "YanosLibraries"
include(":app")
include(":core")
include(":chat")
include(":domain")
include(":firestorewrapper")