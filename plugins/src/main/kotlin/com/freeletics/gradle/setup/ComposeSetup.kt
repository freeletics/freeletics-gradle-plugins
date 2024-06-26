package com.freeletics.gradle.setup

import com.android.build.api.dsl.LibraryExtension
import com.freeletics.gradle.util.booleanProperty
import com.freeletics.gradle.util.compilerOptions
import com.freeletics.gradle.util.kotlin
import org.gradle.api.Project

internal fun Project.setupCompose() {
    plugins.apply("org.jetbrains.kotlin.plugin.compose")

    // TODO remove after https://issuetracker.google.com/issues/341986306
    plugins.withId("com.android.library") {
        extensions.getByType(LibraryExtension::class.java).buildFeatures.compose = true
    }

    val enableMetrics = project.booleanProperty("fgp.compose.enableCompilerMetrics", false)
    if (enableMetrics.get()) {
        val metricsFolderAbsolutePath = project.layout.buildDirectory
            .file("compose-metrics")
            .map { it.asFile.absolutePath }
            .get()

        kotlin {
            compilerOptions {
                freeCompilerArgs.addAll(
                    "-P",
                    "plugin:androidx.compose.compiler.plugins.kotlin:metricsDestination=$metricsFolderAbsolutePath",
                )
            }
        }
    }

    val enableReports = project.booleanProperty("fgp.compose.enableCompilerReports", false)
    if (enableReports.get()) {
        val reportsFolderAbsolutePath = project.layout.buildDirectory
            .file("compose-reports")
            .map { it.asFile.absolutePath }
            .get()

        kotlin {
            compilerOptions {
                freeCompilerArgs.addAll(
                    "-P",
                    "plugin:androidx.compose.compiler.plugins.kotlin:reportsDestination=$reportsFolderAbsolutePath",
                )
            }
        }
    }
}
