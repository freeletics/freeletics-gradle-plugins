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
                    freeCompilerArgs.add("-Xsuppress-warning=DEPRECATION")
                    freeCompilerArgs.add("-Xsuppress-warning=OVERRIDE_DEPRECATION")
                }

                // In this mode, some deprecations and bug-fixes for unstable code take effect immediately.
                progressiveMode.set(version >= KotlinVersion.DEFAULT)

                // Support inferring type arguments based on only self upper bounds of the corresponding type parameters
                // https://kotlinlang.org/docs/whatsnew1530.html#improvements-to-type-inference-for-recursive-generic-types
                freeCompilerArgs.add("-Xself-upper-bound-inference")

                // Kotlin 2.1 experimental language features
                freeCompilerArgs.addAll("-Xwhen-guards", "-Xnon-local-break-continue", "-Xmulti-dollar-interpolation")

                if (this is KotlinJvmCompilerOptions) {
                    jvmTarget.set(project.jvmTarget)

                    freeCompilerArgs.addAll(
                        // https://blog.jetbrains.com/kotlin/2020/07/kotlin-1-4-m3-generating-default-methods-in-interfaces/
                        "-Xjvm-default=all",
                        // https://youtrack.jetbrains.com/issue/KT-22292
                        "-Xassertions=jvm",
                        // Enabling default nullability annotations
                        "-Xjsr305=strict",
                        // https://kotlinlang.org/docs/whatsnew1520.html#support-for-jspecify-nullness-annotations
                        "-Xjspecify-annotations=strict",
                        // Enhance not null annotated type parameter's types to definitely not null types (@NotNull T => T & Any)
                        "-Xenhance-type-parameter-types-to-def-not-null",
                        // https://kotlinlang.org/docs/whatsnew2020.html#data-class-copy-function-to-have-the-same-visibility-as-constructor
                        "-Xconsistent-data-class-copy-visibility",
                    )

                    if (!isAndroid) {
                        freeCompilerArgs.add("-Xjdk-release=${project.javaTarget}")
                    }
                }
            }
        }

        // TODO remove when updating to Kotlin 2.1.20 https://youtrack.jetbrains.com/issue/KT-64385
        tasks.withType(org.jetbrains.kotlin.gradle.tasks.KaptGenerateStubs::class.java).configureEach {
            it.compilerOptions {
                progressiveMode.set(booleanProperty("kapt.use.k2", false))
            }
        }
    }
}
