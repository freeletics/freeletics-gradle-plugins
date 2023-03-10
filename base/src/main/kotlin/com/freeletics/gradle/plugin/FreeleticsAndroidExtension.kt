package com.freeletics.gradle.plugin

import com.android.build.api.variant.HasAndroidTestBuilder
import com.freeletics.gradle.setup.configurePaparazzi
import com.freeletics.gradle.setup.configureProcessing
import com.freeletics.gradle.util.android
import com.freeletics.gradle.util.androidComponents
import com.freeletics.gradle.util.androidResources
import com.freeletics.gradle.util.compilerOptions
import com.freeletics.gradle.util.getDependency
import com.freeletics.gradle.util.getVersion
import com.freeletics.gradle.util.kotlin
import com.freeletics.gradle.util.stringProperty
import org.gradle.api.Project

abstract class FreeleticsAndroidExtension(project: Project) : FreeleticsBaseExtension(project) {

    fun useRoom() {
        val processorConfiguration = project.configureProcessing()

        project.dependencies.apply {
            add("api", project.getDependency("androidx-room-runtime"))
            add(processorConfiguration, project.getDependency("androidx-room-compiler"))
        }
    }

    fun usePaparazzi() {
        project.configurePaparazzi()
    }

    fun minSdkVersion(minSdkVersion: Int?) {
        if (minSdkVersion != null) {
            project.android {
                defaultConfig.minSdk = minSdkVersion
            }
        }
    }

    fun enableParcelize() {
        project.plugins.apply("org.jetbrains.kotlin.plugin.parcelize")
    }

    @Suppress("UnstableApiUsage")
    fun enableCompose() {
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
                compilerOptions(project) {
                    freeCompilerArgs.addAll(
                        "-P",
                        "plugin:androidx.compose.compiler.plugins.kotlin:" +
                            "suppressKotlinVersionCompatibilityCheck=$suppressComposeCompilerCheck",
                    )
                }
            }
        }
    }

    @Suppress("UnstableApiUsage")
    fun enableViewBinding() {
        project.android {
            buildFeatures.viewBinding = true
        }
    }

    @Suppress("UnstableApiUsage")
    fun enableAndroidResources() {
        project.android {
            buildFeatures.androidResources = true
        }
    }

    @Suppress("UnstableApiUsage")
    fun enableBuildConfig() {
        project.android {
            buildFeatures.buildConfig = true
        }
    }

    @Suppress("UnstableApiUsage")
    fun enableResValues() {
        project.android {
            buildFeatures.resValues = true
        }
    }

    fun buildConfigField(type: String, name: String, value: String) {
        project.android {
            defaultConfig.buildConfigField(type, name, value)
        }
    }

    fun buildConfigField(type: String, name: String, debugValue: String, releaseValue: String) {
        project.android {
            buildTypes.getByName("debug").buildConfigField(type, name, debugValue)
            buildTypes.getByName("release").buildConfigField(type, name, releaseValue)
        }
    }

    fun resValue(type: String, name: String, value: String) {
        project.android {
            defaultConfig.resValue(type, name, value)
        }
    }

    fun resValue(type: String, name: String, debugValue: String, releaseValue: String) {
        project.android {
            buildTypes.getByName("debug").resValue(type, name, debugValue)
            buildTypes.getByName("release").resValue(type, name, releaseValue)
        }
    }

    @Suppress("UnstableApiUsage")
    @JvmOverloads
    fun enableAndroidTests(
        testInstrumentationRunner: String = "androidx.test.runner.AndroidJUnitRunner",
        testInstrumentationRunnerArguments: Map<String, String> = mapOf("clearPackageData" to "'true'"),
        execution: String = "ANDROIDX_TEST_ORCHESTRATOR",
        animationsDisabled: Boolean = true,

    ) {
        project.android {
            defaultConfig {
                this.testInstrumentationRunner = testInstrumentationRunner
                this.testInstrumentationRunnerArguments += testInstrumentationRunnerArguments
            }

            testOptions {
                this.execution = execution
                this.animationsDisabled = animationsDisabled
            }
        }

        project.androidComponents {
            beforeVariants(selector().withBuildType("debug")) {
                (it as HasAndroidTestBuilder).enableAndroidTest = true
            }
        }
    }
}
