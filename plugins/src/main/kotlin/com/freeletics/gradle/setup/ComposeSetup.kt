package com.freeletics.gradle.setup

import com.freeletics.gradle.util.android
import com.freeletics.gradle.util.booleanProperty
import com.freeletics.gradle.util.getDependency
import com.freeletics.gradle.util.getVersion
import com.freeletics.gradle.util.kotlin
import com.freeletics.gradle.util.stringProperty
import org.gradle.api.Project
import org.jetbrains.compose.ComposeExtension

internal fun Project.setupCompose() {
    if ((plugins.hasPlugin("com.android.library") || plugins.hasPlugin("com.android.library")) &&
        !plugins.hasPlugin("org.jetbrains.kotlin.multiplatform")
    ) {
        setupComposeAndroid()
    } else {
        setupComposeJetbrains()
    }

    val suppressComposeCompilerCheck = project.stringProperty("fgp.compose.kotlinVersion").orNull
    if (suppressComposeCompilerCheck != null) {
        project.kotlin {
            compilerOptions {
                freeCompilerArgs.addAll(
                    "-P",
                    "plugin:androidx.compose.compiler.plugins.kotlin:" +
                        "suppressKotlinVersionCompatibilityCheck=$suppressComposeCompilerCheck",
                )
            }
        }
    }

    val enableMetrics = project.booleanProperty("fgp.compose.enableCompilerMetrics", false)
    if (enableMetrics.get()) {
        val metricsFolderAbsolutePath = project.layout.buildDirectory
            .file("compose-metrics")
            .map { it.asFile.absolutePath }
            .get()

        project.kotlin {
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

        project.kotlin {
            compilerOptions {
                freeCompilerArgs.addAll(
                    "-P",
                    "plugin:androidx.compose.compiler.plugins.kotlin:reportsDestination=$reportsFolderAbsolutePath",
                )
            }
        }
    }
}

private fun Project.setupComposeAndroid() {
    project.android {
        buildFeatures.compose = true

        composeOptions {
            kotlinCompilerExtensionVersion = project.getVersion("androidx.compose.compiler")
        }
    }
}

private fun Project.setupComposeJetbrains() {
    plugins.apply("org.jetbrains.compose")

    extensions.configure(ComposeExtension::class.java) {
        val composeCompiler = getDependency("jetbrains-compose-compiler").get()
        it.kotlinCompilerPlugin.set("${composeCompiler.group}:${composeCompiler.name}:${composeCompiler.version}")
    }
}
