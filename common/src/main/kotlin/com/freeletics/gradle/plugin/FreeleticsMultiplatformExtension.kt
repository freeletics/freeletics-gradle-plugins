package com.freeletics.gradle.plugin

import com.freeletics.gradle.util.kotlinMultiplatform
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.plugin.KotlinJsCompilerType
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFrameworkConfig
import org.jetbrains.kotlin.gradle.targets.jvm.KotlinJvmTarget

abstract class FreeleticsMultiplatformExtension(project: Project) : FreeleticsBaseExtension(project) {

    @JvmOverloads
    fun addJvmTarget(configure: KotlinJvmTarget.() -> Unit = { }) {
        project.kotlinMultiplatform {
            jvm(configure = configure)
        }
    }

    @JvmOverloads
    fun addIosTargets(
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
            val iosMain = sourceSets.create("iosMain") { sourceSet ->
                sourceSet.dependsOn(sourceSets.getByName("commonMain"))
            }
            val iosTest = sourceSets.create("iosTest") { sourceSet ->
                sourceSet.dependsOn(sourceSets.getByName("commonTest"))
            }

            iosArm64 {
                compilations.getByName("main").source(iosMain)
                compilations.getByName("test").source(iosTest)

                binaries.framework {
                    baseName = frameworkName
                    xcFramework?.add(this)
                }

                configure()
            }

            iosX64 {
                compilations.getByName("main").source(iosMain)
                compilations.getByName("test").source(iosTest)

                binaries.framework {
                    baseName = frameworkName
                    xcFramework?.add(this)
                }

                configure()
            }

            iosSimulatorArm64 {
                compilations.getByName("main").source(iosMain)
                compilations.getByName("test").source(iosTest)

                binaries.framework {
                    baseName = frameworkName
                    xcFramework?.add(this)
                }

                configure()
            }
        }
    }

    fun addCommonTargets() {
        project.kotlinMultiplatform {
            jvm()

            js(KotlinJsCompilerType.IR) {
                nodejs()
            }

            linuxX64()

            iosArm64()
            iosX64()
            iosSimulatorArm64()

            macosArm64()
            macosX64()

            mingwX64()

            tvosArm64()
            tvosX64()

            watchosArm32()
            watchosArm64()
            watchosX64()
            watchosSimulatorArm64()

            val nativeMain = sourceSets.create("nativeMain") { sourceSet ->
                sourceSet.dependsOn(sourceSets.getByName("commonMain"))
            }
            val nativeTest = sourceSets.create("nativeTest") { sourceSet ->
                sourceSet.dependsOn(sourceSets.getByName("commonTest"))
            }

            targets.configureEach {
                if (it.platformType == KotlinPlatformType.native) {
                    it.compilations.getByName("main").source(nativeMain)
                    it.compilations.getByName("test").source(nativeTest)
                }
            }
        }
    }
}
