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
                    if (version < KotlinVersion.KOTLIN_2_2) {
                        freeCompilerArgs.add("-Xsuppress-warning=DEPRECATION")
                        freeCompilerArgs.add("-Xsuppress-warning=OVERRIDE_DEPRECATION")
                    } else {
                        freeCompilerArgs.add("-Xwarning-level=DEPRECATION:disabled")
                        freeCompilerArgs.add("-Xwarning-level=OVERRIDE_DEPRECATION:disabled")
                    }
                }

                // In this mode, some deprecations and bug-fixes for unstable code take effect immediately.
                progressiveMode.set(version >= KotlinVersion.DEFAULT)

                freeCompilerArgs.addAll(
                    // https://youtrack.jetbrains.com/issue/KT-73255
                    "-Xannotation-default-target=param-property",
                    // https://kotlinlang.org/docs/whatsnew2020.html#data-class-copy-function-to-have-the-same-visibility-as-constructor
                    "-Xconsistent-data-class-copy-visibility",
                )

                if (version >= KotlinVersion.KOTLIN_2_1 && version < KotlinVersion.KOTLIN_2_2) {
                    freeCompilerArgs.addAll(
                        // Kotlin 2.1 experimental language features
                        "-Xwhen-guards",
                        "-Xnon-local-break-continue",
                        "-Xmulti-dollar-interpolation",
                    )
                }

                if (version < KotlinVersion.KOTLIN_2_2) {
                    // Support inferring type arguments based on only self upper bounds of the corresponding type parameters
                    // https://kotlinlang.org/docs/whatsnew1530.html#improvements-to-type-inference-for-recursive-generic-types
                    freeCompilerArgs.add("-Xself-upper-bound-inference")
                }

                if (version >= KotlinVersion.KOTLIN_2_2) {
                    freeCompilerArgs.addAll(
                        // Enable context parameters (2.2.0 beta feature)
                        "-Xcontext-parameters",
                    )
                }

                if (this is KotlinJvmCompilerOptions) {
                    jvmTarget.set(project.jvmTarget)

                    if (version >= KotlinVersion.KOTLIN_2_2) {
                        // https://youtrack.jetbrains.com/issue/KT-73007
                        freeCompilerArgs.addAll("-jvm-default=no-compatibility")
                    } else {
                        freeCompilerArgs.addAll(
                            // https://blog.jetbrains.com/kotlin/2020/07/kotlin-1-4-m3-generating-default-methods-in-interfaces/
                            "-Xjvm-default=all",
                        )
                    }

                    freeCompilerArgs.addAll(
                        // https://youtrack.jetbrains.com/issue/KT-22292
                        "-Xassertions=jvm",
                        // Enabling default nullability annotations
                        "-Xjsr305=strict",
                        // https://kotlinlang.org/docs/whatsnew1520.html#support-for-jspecify-nullness-annotations
                        "-Xjspecify-annotations=strict",
                        // Enhance not null annotated type parameter's types to definitely not null types (@NotNull T => T & Any)
                        "-Xenhance-type-parameter-types-to-def-not-null",
                    )

                    if (version >= KotlinVersion.KOTLIN_2_2) {
                        freeCompilerArgs.addAll(
                            // https://kotlinlang.org/docs/whatsnew-eap.html#support-for-reading-and-writing-annotations-in-kotlin-metadata
                            "-Xannotations-in-metadata",
                        )
                    }

                    if (!isAndroid) {
                        freeCompilerArgs.add("-Xjdk-release=${project.javaTarget}")
                    }
                }
            }
        }
    }
}
