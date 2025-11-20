package com.freeletics.gradle.plugin

import com.freeletics.gradle.util.addCompileOnlyDependency
import com.freeletics.gradle.util.addImplementationDependency
import com.freeletics.gradle.util.addTestCompileOnlyDependency
import com.freeletics.gradle.util.addTestImplementationDependency
import com.freeletics.gradle.util.addTestRuntimeOnlyDependency
import com.freeletics.gradle.util.booleanProperty
import com.freeletics.gradle.util.compilerOptionsCommon
import com.freeletics.gradle.util.compilerOptionsJvm
import com.freeletics.gradle.util.getBundleOrNull
import com.freeletics.gradle.util.getVersionOrNull
import com.freeletics.gradle.util.java
import com.freeletics.gradle.util.javaTarget
import com.freeletics.gradle.util.javaToolchainVersion
import com.freeletics.gradle.util.jvmTarget
import com.freeletics.gradle.util.kotlin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.jvm.tasks.Jar
import org.gradle.jvm.toolchain.JvmVendorSpec
import org.jetbrains.kotlin.gradle.dsl.JvmDefaultMode
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
        target.configureTests()
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
        addImplementationDependency(getBundleOrNull("default-all"))
        addCompileOnlyDependency(getBundleOrNull("default-all-compile"))
    }

    private fun Project.addDefaultTestDependencies() {
        addTestImplementationDependency(getBundleOrNull("default-testing"))
        addTestCompileOnlyDependency(getBundleOrNull("default-testing-compile"))
        addTestRuntimeOnlyDependency(getBundleOrNull("default-testing-runtime"))
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

            compilerOptionsCommon {
                val version = getVersionOrNull("kotlin-language")
                    ?.let(KotlinVersion::fromVersion) ?: KotlinVersion.DEFAULT
                languageVersion.set(version)

                // In this mode, some deprecations and bug-fixes for unstable code take effect immediately.
                progressiveMode.set(version >= KotlinVersion.DEFAULT)

                extraWarnings.set(booleanProperty("fgp.kotlin.extraWarnings", true))
                allWarningsAsErrors.set(booleanProperty("fgp.kotlin.warningsAsErrors", true))
                if (booleanProperty("fgp.kotlin.suppressDeprecationWarnings", false).get()) {
                    freeCompilerArgs.add("-Xwarning-level=DEPRECATION:disabled")
                    freeCompilerArgs.add("-Xwarning-level=OVERRIDE_DEPRECATION:disabled")
                }

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
            }

            compilerOptionsJvm { isAndroid ->
                jvmTarget.set(project.jvmTarget)
                jvmDefault.set(JvmDefaultMode.NO_COMPATIBILITY)

                if (!isAndroid) {
                    freeCompilerArgs.add("-Xjdk-release=${project.javaTarget}")
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
                    // https://kotlinlang.org/docs/whatsnew-eap.html#support-for-reading-and-writing-annotations-in-kotlin-metadata
                    "-Xannotations-in-metadata",
                    // opt in to experimental stdlib apis
                    "-opt-in=kotlin.io.path.ExperimentalPathApi",
                )
            }
        }
    }

    private fun Project.configureTests() {
        project.tasks.withType(Test::class.java).configureEach {
            @Suppress("UnstableApiUsage")
            val directory = layout.settingsDirectory
            val projectName = path
                .replace("projects", "")
                .replaceFirst(":", "")
                .replace(":", "/")
            it.reports.html.outputLocation.set(directory.dir("reports/tests/$projectName"))
            it.reports.junitXml.outputLocation.set(directory.dir("reports/tests/$projectName"))
        }
    }
}
