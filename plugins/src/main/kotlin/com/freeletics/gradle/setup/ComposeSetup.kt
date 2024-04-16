package com.freeletics.gradle.setup

import com.freeletics.gradle.util.booleanProperty
import com.freeletics.gradle.util.kotlin
import org.gradle.api.Project

internal fun Project.setupCompose() {
    plugins.apply("org.jetbrains.kotlin.plugin.compose")

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
