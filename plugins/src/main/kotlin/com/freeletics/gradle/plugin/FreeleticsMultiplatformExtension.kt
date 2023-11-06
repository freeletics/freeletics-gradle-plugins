package com.freeletics.gradle.plugin

import com.freeletics.gradle.util.kotlinMultiplatform
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.plugin.KotlinJsCompilerType
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinAndroidTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFrameworkConfig
import org.jetbrains.kotlin.gradle.targets.jvm.KotlinJvmTarget

public abstract class FreeleticsMultiplatformExtension(private val project: Project) {

    @JvmOverloads
    public fun addJvmTarget(configure: KotlinJvmTarget.() -> Unit = { }) {
        project.kotlinMultiplatform {
            jvm(configure = configure)
        }
    }

    @JvmOverloads
    public fun addAndroidTarget(
        variantsToPublish: List<String>? = listOf("release"),
        configure: KotlinAndroidTarget.() -> Unit = { },
    ) {
        project.plugins.apply(FreeleticsAndroidPlugin::class.java)

        project.kotlinMultiplatform {
            androidTarget {
                publishLibraryVariants = variantsToPublish

                configure()
            }
        }
    }

    @JvmOverloads
    public fun addIosTargets(
        frameworkName: String,
        createXcFramework: Boolean = false,
        configure: KotlinNativeTarget.() -> Unit = { },
    ) {
        val xcFramework = if (createXcFramework) {
            XCFrameworkConfig(project, frameworkName)
        } else {
            null
        }
        project.kotlinMultiplatform {
            iosArm64 {
                binaries.framework {
                    baseName = frameworkName
                    xcFramework?.add(this)
                }

                configure()
            }

            iosX64 {
                binaries.framework {
                    baseName = frameworkName
                    xcFramework?.add(this)
                }

                configure()
            }

            iosSimulatorArm64 {
                binaries.framework {
                    baseName = frameworkName
                    xcFramework?.add(this)
                }

                configure()
            }
        }
    }

    public fun addCommonTargets(androidNativeTargets: Boolean = true) {
        project.kotlinMultiplatform {
            jvm()

            js(KotlinJsCompilerType.IR) {
                nodejs()
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
            watchosDeviceArm64()
            watchosX64()
            watchosSimulatorArm64()

            if (androidNativeTargets) {
                androidNativeArm32()
                androidNativeArm64()
                androidNativeX86()
                androidNativeX64()
            }
        }
    }
}
