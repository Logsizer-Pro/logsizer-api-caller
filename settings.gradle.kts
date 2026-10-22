pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

// The daemon runs on JDK 21 (gradle/gradle-daemon-jvm.properties); the resolver
// downloads it into GRADLE_USER_HOME/jdks on a machine that has none.
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "logsizer-api-caller"
include(":app")
