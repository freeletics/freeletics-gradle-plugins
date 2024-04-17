package com.freeletics.gradle.setup

import com.freeletics.gradle.util.android
import com.freeletics.gradle.util.booleanProperty
import com.freeletics.gradle.util.getDependency
import com.freeletics.gradle.util.getVersion
import com.freeletics.gradle.util.kotlin
import com.freeletics.gradle.util.stringProperty
import org.gradle.api.Project
import org.jetbrains.compose.ComposeExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinBasePlugin

internal fun Project.setupCompose() {
    if (kotlinGradlePluginVersion().startsWith("2.")) {
        setupComposeKotlin2()
    } else if ((plugins.hasPlugin("com.android.library") || plugins.hasPlugin("com.android.application")) &&
        !plugins.hasPlugin("org.jetbrains.kotlin.multiplatform")
    ) {
        setupComposeAndroid()
    } else {
        setupComposeJetbrains()
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

private fun Project.kotlinGradlePluginVersion(): String {
    val id = listOf(
        "org.jetbrains.kotlin.jvm",
        "org.jetbrains.kotlin.android",
        "org.jetbrains.kotlin.multiplatform",
    ).firstOrNull { plugins.hasPlugin(it) } ?: throw IllegalStateException("No Kotlin plugin found")
    return (plugins.getPlugin(id) as KotlinBasePlugin).pluginVersion
}

private fun Project.setupComposeKotlin2() {
    plugins.apply("org.jetbrains.kotlin.plugin.compose")

    // TODO remove after RC1
    if (kotlinGradlePluginVersion() == "2.0.0-RC1") {
        kotlin {
            compilerOptions {
                freeCompilerArgs.addAll(
                    "-P",
                    "plugin:androidx.compose.compiler.plugins.kotlin:" +
                        "suppressKotlinVersionCompatibilityCheck=2.0.0-RC1",
                )
            }
        }
    }
}

private fun Project.setupComposeAndroid() {
    android {
        buildFeatures.compose = true

        composeOptions {
            kotlinCompilerExtensionVersion = project.getVersion("androidx.compose.compiler")
        }
    }

    suppressVersionInconsistency()
}

private fun Project.setupComposeJetbrains() {
    plugins.apply("org.jetbrains.compose")

    extensions.configure(ComposeExtension::class.java) {
        val composeCompiler = getDependency("jetbrains-compose-compiler").get()
        it.kotlinCompilerPlugin.set("${composeCompiler.group}:${composeCompiler.name}:${composeCompiler.version}")
    }

    suppressVersionInconsistency()
}

private fun Project.suppressVersionInconsistency() {
    val suppressComposeCompilerCheck = stringProperty("fgp.compose.kotlinVersion").orNull
    if (suppressComposeCompilerCheck != null) {
        kotlin {
            compilerOptions {
                freeCompilerArgs.addAll(
                    "-P",
                    "plugin:androidx.compose.compiler.plugins.kotlin:" +
                        "suppressKotlinVersionCompatibilityCheck=$suppressComposeCompilerCheck",
                )
            }
        }
    }
}
