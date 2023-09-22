package com.freeletics.gradle.plugin

import com.android.build.api.dsl.LibraryExtension
import com.freeletics.gradle.setup.configurePaparazzi
import com.freeletics.gradle.setup.configureProcessing
import com.freeletics.gradle.util.android
import com.freeletics.gradle.util.androidResources
import com.freeletics.gradle.util.booleanProperty
import com.freeletics.gradle.util.getDependency
import com.freeletics.gradle.util.getVersion
import com.freeletics.gradle.util.kotlin
import com.freeletics.gradle.util.stringProperty
import org.gradle.api.Project

public abstract class FreeleticsAndroidExtension(private val project: Project) {

    public fun useRoom() {
        val generateKotlin = project.booleanProperty("fgp.room.generateKotlin", true).get()
        val processorConfiguration = project.configureProcessing(
            useKsp = true,
            "room.generateKotlin" to generateKotlin.toString(),
        )

        project.dependencies.apply {
            add("api", project.getDependency("androidx-room-runtime"))
            add(processorConfiguration, project.getDependency("androidx-room-compiler"))
        }
    }

    public fun usePaparazzi() {
        project.configurePaparazzi()
    }

    public fun minSdkVersion(minSdkVersion: Int?) {
        if (minSdkVersion != null) {
            project.android {
                defaultConfig.minSdk = minSdkVersion
            }
        }
    }

    public fun enableParcelize() {
        project.plugins.apply("org.jetbrains.kotlin.plugin.parcelize")
    }

    public fun enableCompose() {
        val project = this.project
        project.android {
            buildFeatures.compose = true

            composeOptions {
                kotlinCompilerExtensionVersion = project.getVersion("androidx.compose.compiler")
            }
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

    public fun enableViewBinding() {
        project.android {
            buildFeatures.viewBinding = true
        }
    }

    public fun enableAndroidResources() {
        project.android {
            buildFeatures.androidResources = true
        }
    }

    public fun enableBuildConfig() {
        project.android {
            buildFeatures.buildConfig = true
        }
    }

    public fun enableResValues() {
        project.android {
            buildFeatures.resValues = true
        }
    }

    public fun buildConfigField(type: String, name: String, value: String) {
        project.android {
            defaultConfig.buildConfigField(type, name, value)
        }
    }

    public fun buildConfigField(type: String, name: String, debugValue: String, releaseValue: String) {
        project.android {
            buildTypes.getByName("debug").buildConfigField(type, name, debugValue)
            buildTypes.getByName("release").buildConfigField(type, name, releaseValue)
        }
    }

    public fun resValue(type: String, name: String, value: String) {
        project.android {
            defaultConfig.resValue(type, name, value)
        }
    }

    public fun resValue(type: String, name: String, debugValue: String, releaseValue: String) {
        project.android {
            buildTypes.getByName("debug").resValue(type, name, debugValue)
            buildTypes.getByName("release").resValue(type, name, releaseValue)
        }
    }

    public fun consumerProguardFiles(vararg files: String) {
        project.android {
            (this as LibraryExtension).defaultConfig.consumerProguardFiles(*files)
        }
    }
}
