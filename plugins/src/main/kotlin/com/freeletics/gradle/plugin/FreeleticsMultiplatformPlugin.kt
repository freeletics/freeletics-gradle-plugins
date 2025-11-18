package com.freeletics.gradle.plugin

import com.freeletics.gradle.setup.configureStandaloneLint
import com.freeletics.gradle.util.compilerOptionsCommon
import com.freeletics.gradle.util.freeleticsExtension
import com.freeletics.gradle.util.kotlin
import com.freeletics.gradle.util.kotlinMultiplatform
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

public abstract class FreeleticsMultiplatformPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.plugins.apply("org.jetbrains.kotlin.multiplatform")
        target.plugins.apply(FreeleticsBasePlugin::class.java)

        target.freeleticsExtension.extensions.create("multiplatform", FreeleticsMultiplatformExtension::class.java)

        target.kotlinMultiplatform {
            applyDefaultHierarchyTemplate()
        }

        target.kotlin {
            compilerOptionsCommon {
                freeCompilerArgs.add("-Xexpect-actual-classes")
            }
        }

        target.configureStandaloneLint()
        target.disableDefaultJsRepositories()
        target.rootProject.disableDefaultJsRepositories()
    }

    // TODO remove after https://youtrack.jetbrains.com/issue/KT-68533
    private fun Project.disableDefaultJsRepositories() {
        plugins.withType(org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsPlugin::class.java) {
            extensions.configure(org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsEnvSpec::class.java) {
                it.downloadBaseUrl.set(null)
            }
        }
        plugins.withType(org.jetbrains.kotlin.gradle.targets.js.yarn.YarnPlugin::class.java) {
            extensions.configure(org.jetbrains.kotlin.gradle.targets.js.yarn.YarnRootEnvSpec::class.java) {
                it.downloadBaseUrl.set(null)
            }
        }
        plugins.withType(org.jetbrains.kotlin.gradle.targets.wasm.nodejs.WasmNodeJsPlugin::class.java) {
            extensions.configure(org.jetbrains.kotlin.gradle.targets.wasm.nodejs.WasmNodeJsEnvSpec::class.java) {
                it.downloadBaseUrl.set(null)
            }
        }
        plugins.withType(org.jetbrains.kotlin.gradle.targets.wasm.yarn.WasmYarnPlugin::class.java) {
            extensions.configure(org.jetbrains.kotlin.gradle.targets.wasm.yarn.WasmYarnRootEnvSpec::class.java) {
                it.downloadBaseUrl.set(null)
            }
        }
        @OptIn(ExperimentalWasmDsl::class)
        plugins.withType(org.jetbrains.kotlin.gradle.targets.wasm.binaryen.BinaryenPlugin::class.java) {
            extensions.configure(org.jetbrains.kotlin.gradle.targets.wasm.binaryen.BinaryenEnvSpec::class.java) {
                it.downloadBaseUrl.set(null)
            }
        }
    }
}
