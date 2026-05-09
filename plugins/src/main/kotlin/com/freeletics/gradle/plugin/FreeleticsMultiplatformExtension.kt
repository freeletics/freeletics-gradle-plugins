package com.freeletics.gradle.plugin

import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryTarget
import com.freeletics.gradle.setup.setupAndroidTarget
import com.freeletics.gradle.setup.setupXcFrameworkPublishing
import com.freeletics.gradle.util.addImplementationDependency
import com.freeletics.gradle.util.booleanProperty
import com.freeletics.gradle.util.defaultPackageName
import com.freeletics.gradle.util.freeleticsMultiplatformExtension
import com.freeletics.gradle.util.getDependency
import com.freeletics.gradle.util.kotlinMultiplatform
import kotlin.jvm.java
import org.gradle.api.Project
import org.jetbrains.compose.ComposeExtension
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
                )
            } else {
                project.freeleticsMultiplatformExtension.addIosTargets()
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
            extensions.findByType(KotlinMultiplatformAndroidLibraryTarget::class.java)!!
                .setupAndroidTarget(project, configure)
        }
    }

    public fun addIosTargets() {
        addIosTargets() {}
    }

    private fun addIosTargets(configure: KotlinNativeTarget.() -> Unit) {
        project.kotlinMultiplatform {
            iosArm64(configure)
            iosSimulatorArm64(configure)
        }
    }

    @JvmOverloads
    public fun addIosTargetsWithXcFramework(
        frameworkName: String,
        configure: KotlinNativeTarget.(Framework) -> Unit = {},
    ) {
        val xcFramework = XCFrameworkConfig(project, frameworkName)

        addIosTargets {
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
            iosSimulatorArm64()

            macosArm64()

            mingwX64()

            tvosArm64()
            tvosSimulatorArm64()

            watchosArm32()
            watchosArm64()
            watchosDeviceArm64()
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

        project.addImplementationDependency(project.getDependency("compose-components-resources"))
    }

    public fun useSkie() {
        project.plugins.apply("co.touchlab.skie")
    }
}
