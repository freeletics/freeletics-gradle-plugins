package com.freeletics.gradle.plugin

import com.freeletics.gradle.util.addMaybe
import com.freeletics.gradle.util.booleanProperty
import com.freeletics.gradle.util.compilerOptions
import com.freeletics.gradle.util.getBundleOrNull
import com.freeletics.gradle.util.getVersionOrNull
import com.freeletics.gradle.util.java
import com.freeletics.gradle.util.javaTarget
import com.freeletics.gradle.util.javaToolchainVersion
import com.freeletics.gradle.util.jvmTarget
import com.freeletics.gradle.util.kotlin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.jvm.tasks.Jar
import org.gradle.jvm.toolchain.JvmVendorSpec
import org.jetbrains.kotlin.gradle.dsl.JvmDefaultMode
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompilerOptions
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

public abstract class FreeleticsBasePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.plugins.apply("com.autonomousapps.dependency-analysis")

        target.extensions.create("freeletics", FreeleticsBaseExtension::class.java)

        target.makeJarsReproducible()
        target.addDefaultDependencies()
        target.addDefaultTestDependencies()
        target.configureJava()
        target.configureKotlin()
    }

    private fun Project.makeJarsReproducible() {
        tasks.withType(Jar::class.java).configureEach {
            // Jar contents should be ordered consistently
            it.isReproducibleFileOrder = true
            // Jar contents should not have unique timestamps
            it.isPreserveFileTimestamps = false
        }
    }

    private fun Project.addDefaultDependencies() {
        dependencies.addMaybe("implementation", getBundleOrNull("default-all"))
        dependencies.addMaybe("compileOnly", getBundleOrNull("default-all-compile"))
    }

    private fun Project.addDefaultTestDependencies() {
        dependencies.addMaybe("testImplementation", getBundleOrNull("default-testing"))
        dependencies.addMaybe("testCompileOnly", getBundleOrNull("default-testing-compile"))
        dependencies.addMaybe("testRuntimeOnly", getBundleOrNull("default-testing-runtime"))
    }

    private fun Project.configureJava() {
        java {
            toolchain {
                it.languageVersion.set(javaToolchainVersion)
                it.vendor.set(JvmVendorSpec.AZUL)
            }
        }
    }

    private fun Project.configureKotlin() {
        kotlin {
            jvmToolchain { toolchain ->
                toolchain.languageVersion.set(javaToolchainVersion)
                toolchain.vendor.set(JvmVendorSpec.AZUL)
            }

            val isAndroid = this is KotlinAndroidProjectExtension

            compilerOptions {
                val version = getVersionOrNull("kotlin-language")
                    ?.let(KotlinVersion::fromVersion) ?: KotlinVersion.DEFAULT
                languageVersion.set(version)

                extraWarnings.set(booleanProperty("fgp.kotlin.extraWarnings", true))
                allWarningsAsErrors.set(booleanProperty("fgp.kotlin.warningsAsErrors", true))
                if (booleanProperty("fgp.kotlin.suppressDeprecationWarnings", false).get()) {
                    freeCompilerArgs.add("-Xwarning-level=DEPRECATION:disabled")
                    freeCompilerArgs.add("-Xwarning-level=OVERRIDE_DEPRECATION:disabled")
                }

                // In this mode, some deprecations and bug-fixes for unstable code take effect immediately.
                progressiveMode.set(version >= KotlinVersion.DEFAULT)

                freeCompilerArgs.addAll(
                    // https://youtrack.jetbrains.com/issue/KT-73255
                    "-Xannotation-default-target=param-property",
                    // https://kotlinlang.org/docs/whatsnew2020.html#data-class-copy-function-to-have-the-same-visibility-as-constructor
                    "-Xconsistent-data-class-copy-visibility",
                    // Enable 2.2.0 feature previews
                    "-Xcontext-parameters",
                    "-Xcontext-sensitive-resolution",
                    "-Xannotation-target-all",
                    // opt in to experimental stdlib apis
                    "-opt-in=kotlin.ExperimentalStdlibApi",
                    "-opt-in=kotlin.time.ExperimentalTime",
                    "-opt-in=kotlin.uuid.ExperimentalUuidApi",
                )

                if (this is KotlinJvmCompilerOptions) {
                    jvmTarget.set(project.jvmTarget)
                    jvmDefault.set(JvmDefaultMode.NO_COMPATIBILITY)

                    freeCompilerArgs.addAll(
                        // https://youtrack.jetbrains.com/issue/KT-22292
                        "-Xassertions=jvm",
                        // Enabling default nullability annotations
                        "-Xjsr305=strict",
                        // https://kotlinlang.org/docs/whatsnew1520.html#support-for-jspecify-nullness-annotations
                        "-Xjspecify-annotations=strict",
                        // Enhance not null annotated type parameter's types to definitely not null types (@NotNull T => T & Any)
                        "-Xenhance-type-parameter-types-to-def-not-null",
                        // https://kotlinlang.org/docs/whatsnew-eap.html#support-for-reading-and-writing-annotations-in-kotlin-metadata
                        "-Xannotations-in-metadata",
                        // opt in to experimental stdlib apis
                        "-opt-in=kotlin.io.path.ExperimentalPathApi",
                    )

                    if (!isAndroid) {
                        freeCompilerArgs.add("-Xjdk-release=${project.javaTarget}")
                    }
                }
            }
        }
    }
}
