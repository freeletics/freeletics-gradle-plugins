package com.freeletics.gradle.plugin

import com.android.build.api.dsl.androidLibrary
import com.freeletics.gradle.setup.setupAndroidTarget
import com.freeletics.gradle.setup.setupXcFrameworkPublishing
import com.freeletics.gradle.util.addImplementationDependency
import com.freeletics.gradle.util.booleanProperty
import com.freeletics.gradle.util.defaultPackageName
import com.freeletics.gradle.util.freeleticsMultiplatformExtension
import com.freeletics.gradle.util.kotlinMultiplatform
import kotlin.jvm.java
import org.gradle.api.Project
import org.jetbrains.compose.ComposeExtension
import org.jetbrains.compose.ComposePlugin
import org.jetbrains.compose.resources.ResourcesExtension
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.plugin.mpp.Framework
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFrameworkConfig

public abstract class FreeleticsMultiplatformExtension(private val project: Project) {
    internal fun addDefaultTargets(xcFramework: Boolean = false) {
        if (project.booleanProperty("fgp.kotlin.targets.android", false).get()) {
            project.freeleticsMultiplatformExtension.addAndroidTarget(variantsToPublish = null)
        }
        if (project.booleanProperty("fgp.kotlin.targets.jvm", false).get()) {
            project.freeleticsMultiplatformExtension.addJvmTarget()
        }
        if (project.booleanProperty("fgp.kotlin.targets.ios", false).get()) {
            if (xcFramework) {
                project.freeleticsMultiplatformExtension.addIosTargetsWithXcFramework(
                    frameworkName = project.name,
                    includeX64 = true,
                )
            } else {
                project.freeleticsMultiplatformExtension.addIosTargets(includeX64 = true)
            }
        }
    }

    public fun addJvmTarget() {
        project.kotlinMultiplatform {
            jvm()
        }
    }

    @JvmOverloads
    public fun addAndroidTarget(
        variantsToPublish: List<String>? = listOf("release"),
        configure: FreeleticsMultiplatformAndroidExtension.() -> Unit = { },
    ) {
        project.plugins.apply("com.android.kotlin.multiplatform.library")
        project.kotlinMultiplatform {
            @Suppress("UnstableApiUsage")
            androidLibrary {
                setupAndroidTarget(project, configure)
            }
        }
    }

    @JvmOverloads
    public fun addIosTargets(includeX64: Boolean = false) {
        addIosTargets(includeX64) {}
    }

    private fun addIosTargets(includeX64: Boolean, configure: KotlinNativeTarget.() -> Unit) {
        project.kotlinMultiplatform {
            iosArm64(configure)
            iosSimulatorArm64(configure)
            if (includeX64) {
                iosX64(configure)
            }
        }
    }

    @JvmOverloads
    public fun addIosTargetsWithXcFramework(
        frameworkName: String,
        includeX64: Boolean = false,
        configure: KotlinNativeTarget.(Framework) -> Unit = { },
    ) {
        val xcFramework = XCFrameworkConfig(project, frameworkName)

        addIosTargets(includeX64 = includeX64) {
            binaries.framework {
                baseName = frameworkName
                xcFramework.add(this)
                configure(this)
            }
        }

        project.plugins.withId("com.vanniktech.maven.publish") {
            setupXcFrameworkPublishing(project, frameworkName)
        }
    }

    public fun addCommonTargets(limitToComposeTargets: Boolean = false) {
        project.kotlinMultiplatform {
            jvm()

            js {
                nodejs()
            }

            @OptIn(ExperimentalWasmDsl::class)
            wasmJs {
                nodejs()
            }

            if (!limitToComposeTargets) {
                @OptIn(ExperimentalWasmDsl::class)
                wasmWasi {
                    nodejs()
                }
            }

            linuxX64()
            linuxArm64()

            iosArm64()
            iosX64()
            iosSimulatorArm64()

            macosArm64()
            macosX64()

            mingwX64()

            tvosArm64()
            tvosX64()
            tvosSimulatorArm64()

            watchosArm32()
            watchosArm64()
            // TODO remove check when Compose 1.9.0 is stable
            if (!limitToComposeTargets) {
                watchosDeviceArm64()
            }
            watchosX64()
            watchosSimulatorArm64()

            if (!limitToComposeTargets) {
                androidNativeArm32()
                androidNativeArm64()
                androidNativeX86()
                androidNativeX64()
            }
        }
    }

    public fun useComposeResources() {
        project.plugins.apply("org.jetbrains.compose")

        project.extensions.configure(ComposeExtension::class.java) { compose ->
            compose.extensions.configure(ResourcesExtension::class.java) {
                it.generateResClass = ResourcesExtension.ResourceClassGeneration.Always
                it.packageOfResClass = project.defaultPackageName()
            }
        }

        project.addImplementationDependency(ComposePlugin.CommonComponentsDependencies.resources)
    }

    public fun useSkie() {
        project.plugins.apply("co.touchlab.skie")
    }
}
