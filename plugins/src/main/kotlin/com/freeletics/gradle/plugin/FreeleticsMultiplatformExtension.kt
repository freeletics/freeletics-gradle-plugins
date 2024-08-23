package com.freeletics.gradle.plugin

import com.freeletics.gradle.util.kotlinMultiplatform
import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.maven.tasks.AbstractPublishToMaven
import org.gradle.api.tasks.bundling.Zip
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.plugin.mpp.Framework
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinAndroidTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFrameworkConfig
import org.jetbrains.kotlin.gradle.targets.jvm.KotlinJvmTarget
import org.jetbrains.kotlin.konan.target.HostManager

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
    public fun addIosTargets(configure: KotlinNativeTarget.() -> Unit = { }) {
        project.kotlinMultiplatform {
            iosArm64 {
                configure()
            }

            iosSimulatorArm64 {
                configure()
            }
        }
    }

    @JvmOverloads
    public fun addIosTargetsWithXcFramework(
        frameworkName: String,
        configure: KotlinNativeTarget.(Framework) -> Unit = { },
    ) {
        val xcFramework = XCFrameworkConfig(project, frameworkName)

        project.kotlinMultiplatform {
            iosArm64 {
                binaries.framework {
                    baseName = frameworkName
                    xcFramework.add(this)
                    configure(this)
                }
            }

            iosSimulatorArm64 {
                binaries.framework {
                    baseName = frameworkName
                    xcFramework.add(this)
                    configure(this)
                }
            }
        }

        project.plugins.withType(FreeleticsPublishInternalPlugin::class.java) {
            val framework = "$frameworkName.xcframework"
            val frameworkRoot = project.layout.buildDirectory.dir("XCFrameworks/release")
            val assembleTask = "assemble${frameworkName}ReleaseXCFramework"

            val frameworkZip = project.tasks.register("${assembleTask}Zip", Zip::class.java) {
                it.dependsOn(assembleTask)
                it.onlyIf { HostManager.hostIsMac }

                it.from(frameworkRoot.map { root -> root.dir(framework) })
                it.into(framework)
                it.archiveBaseName.set(framework)
                it.destinationDirectory.set(frameworkRoot)
                it.isPreserveFileTimestamps = false
                it.isReproducibleFileOrder = true
            }

            val publicationName = "${frameworkName}XcFramework"
            project.extensions.configure(PublishingExtension::class.java) { publishing ->
                publishing.publications.create(publicationName, MavenPublication::class.java) {
                    // the project.name will be replaced with the real artifact id by the publishing plugin
                    it.artifactId = "${project.name}-xcframework"
                    it.artifact(frameworkZip) { artifact ->
                        artifact.extension = "zip"
                    }
                }
            }

            project.tasks.withType(AbstractPublishToMaven::class.java).configureEach {
                if (it.name.contains(publicationName, ignoreCase = true)) {
                    it.onlyIf { HostManager.hostIsMac }
                }
            }
        }
    }

    public fun addCommonTargets(androidNativeTargets: Boolean = true) {
        project.kotlinMultiplatform {
            jvm()

            js {
                nodejs()
            }

            @OptIn(ExperimentalWasmDsl::class)
            wasmJs {
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
