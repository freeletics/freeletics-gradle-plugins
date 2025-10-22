package com.freeletics.gradle.plugin

import com.freeletics.gradle.setup.configurePom
import com.freeletics.gradle.util.freeleticsExtension
import com.freeletics.gradle.util.kotlin
import com.freeletics.gradle.util.stringProperty
import com.vanniktech.maven.publish.MavenPublishBaseExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.dsl.abi.AbiValidationExtension
import org.jetbrains.kotlin.gradle.dsl.abi.AbiValidationLegacyDumpExtension
import org.jetbrains.kotlin.gradle.dsl.abi.AbiValidationMultiplatformExtension
import org.jetbrains.kotlin.gradle.dsl.abi.ExperimentalAbiValidation

public abstract class FreeleticsPublishOssPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.plugins.apply("org.jetbrains.dokka")
        target.plugins.apply("com.vanniktech.maven.publish")

        target.freeleticsExtension.explicitApi()
        target.configureMavenCentral()
        target.configurePom(includeLicense = true)
        target.configureBinaryCompatibility()
        target.configureDokka()
    }

    private fun Project.configureMavenCentral() {
        extensions.configure(MavenPublishBaseExtension::class.java) {
            it.publishToMavenCentral(automaticRelease = true)
            it.signAllPublications()
        }
    }

    @OptIn(ExperimentalAbiValidation::class)
    private fun Project.configureBinaryCompatibility() {
        kotlin {
            when (this) {
                is KotlinJvmProjectExtension -> extensions.configure<AbiValidationExtension>("abiValidation") {
                    it.enabled.set(true)
                }
                is KotlinAndroidProjectExtension -> extensions.configure<AbiValidationExtension>("abiValidation") {
                    it.enabled.set(true)
                }
                is KotlinMultiplatformExtension -> extensions.configure<AbiValidationMultiplatformExtension>(
                    "abiValidation",
                ) {
                    it.enabled.set(true)
                }
                else -> throw IllegalStateException("Unsupported kotlin extension ${this::class}")
            }
            extensions.configure(AbiValidationLegacyDumpExtension::class.java) {
                tasks.named("check").configure { task ->
                    task.dependsOn(it.legacyDumpTaskProvider)
                }
            }
        }
    }

    private fun Project.configureDokka() {
        // if the version is a snapshot version disable dokka tasks to speed up the build
        if (stringProperty("VERSION_NAME").orNull?.toString()?.endsWith("-SNAPSHOT") == true) {
            afterEvaluate {
                tasks.named("dokkaHtml").configure { task ->
                    task.enabled = false
                }

                it.plugins.withId("com.android.library") {
                    if (!plugins.hasPlugin("org.jetbrains.kotlin.multiplatform")) {
                        tasks.named("javaDocReleaseGeneration").configure { task ->
                            task.enabled = false
                        }
                    }
                }
            }
        }
    }
}
