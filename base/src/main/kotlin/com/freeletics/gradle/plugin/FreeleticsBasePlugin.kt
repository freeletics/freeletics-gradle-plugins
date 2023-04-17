package com.freeletics.gradle.plugin

import com.freeletics.gradle.util.booleanProperty
import com.freeletics.gradle.util.compilerOptions
import com.freeletics.gradle.util.getVersionOrNull
import com.freeletics.gradle.util.java
import com.freeletics.gradle.util.javaToolchainVersion
import com.freeletics.gradle.util.jvmTarget
import com.freeletics.gradle.util.kotlin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.jvm.tasks.Jar
import org.gradle.jvm.toolchain.JvmVendorSpec
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompilerOptions
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

public abstract class FreeleticsBasePlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.makeJarsReproducible()
        target.applyPlatformConstraints()
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

    private fun Project.applyPlatformConstraints() {
        val platformProject = findProject(":tooling:platform") ?: return
        val platformDependency = dependencies.enforcedPlatform(platformProject)
        configurations.configureEach { config ->
            if (isPlatformConfigurationName(config.name)) {
                config.dependencies.add(platformDependency)
            }
        }
    }

    // adapted from https://github.com/ZacSweers/CatchUp/blob/347db46d82497990ff10c441ecc75c0c9eedf7c4/buildSrc/src/main/kotlin/dev/zacsweers/catchup/gradle/CatchUpPlugin.kt#L68-L80
    private fun isPlatformConfigurationName(name: String): Boolean {
        // Kapt, ksp and compileOnly are special cases since they can be combined with others
        if (name.startsWith("kapt", ignoreCase = true) ||
            name.startsWith("kapt", ignoreCase = true) ||
            name == "compileOnly"
        ) {
            return true
        }
        // Try trimming the flavor by just matching the suffix
        PLATFORM_CONFIGURATIONS.forEach { platformConfig ->
            if (name.endsWith(platformConfig, ignoreCase = true)) {
                return true
            }
        }
        return false
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

            compilerOptions(project) {
                if (this is KotlinJvmCompilerOptions) {
                    jvmTarget.set(project.jvmTarget)
                }
                getVersionOrNull("kotlin-language")?.let {
                    languageVersion.set(KotlinVersion.fromVersion(it))
                }
                allWarningsAsErrors.set(!booleanProperty("fgp.kotlin.allowWarnings", false).get())
                freeCompilerArgs.addAll(
                    // In this mode, some deprecations and bug-fixes for unstable code take effect immediately.
                    "-progressive",
                    // https://blog.jetbrains.com/kotlin/2020/07/kotlin-1-4-m3-generating-default-methods-in-interfaces/
                    "-Xjvm-default=all",
                    // https://youtrack.jetbrains.com/issue/KT-22292
                    "-Xassertions=jvm",
                    // Enabling default nullability annotations
                    "-Xjsr305=strict",
                    // Enhance not null annotated type parameter's types to definitely not null types (@NotNull T => T & Any)
                    "-Xenhance-type-parameter-types-to-def-not-null",
                    // Support inferring type arguments based on only self upper bounds of the corresponding type parameters
                    // https://kotlinlang.org/docs/whatsnew1530.html#improvements-to-type-inference-for-recursive-generic-types
                    "-Xself-upper-bound-inference",
                    // Enable faster jar file system
                    "-Xuse-fast-jar-file-system"
                )
            }
        }
    }

    private companion object {
        private val PLATFORM_CONFIGURATIONS = setOf(
            "api",
            "coreLibraryDesugaring",
            "compileOnly",
            "implementation",
            "kapt",
            "ksp",
            "runtimeOnly"
        )
    }
}
