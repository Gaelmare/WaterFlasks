pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral() {
            name = "MavenCentral"
        }
        exclusiveContent {
            forRepository {
                maven {
                    name = "NeoForge"
                    url = uri("https://maven.neoforged.net/releases")
                }
            }
            filter { includeGroupAndSubgroups("net.neoforged") }
        }
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
