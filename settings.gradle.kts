pluginManagement {
    repositories {
        exclusiveContent {
            forRepository { google() }

            filter {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("androidx.*")
                includeGroupByRegex("com.google.firebase.*")

                includeGroup("com.google.testing.platform")
                includeGroup("com.google.android.apps.common.testing.accessibility.framework")
            }
        }

        exclusiveContent {
            forRepository { gradlePluginPortal() }

            filter {
                includeGroupByRegex("com.gradle.*")
                includeGroupByRegex("org.gradle.*")
                includeGroup("com.autonomousapps.plugin-best-practices-plugin")
                includeModule("com.autonomousapps", "plugin-best-practices-plugin")
            }
        }

        mavenCentral()
    }
}

plugins {
    id("com.freeletics.gradle.settings").version("0.9.0")
}

rootProject.name = "freeletics-gradle-plugins"
