package com.freeletics.gradle.plugin

import com.freeletics.gradle.setup.defaultTestSetup
import com.freeletics.gradle.util.compilerOptions
import com.freeletics.gradle.util.freeleticsExtension
import com.freeletics.gradle.util.kotlin
import com.freeletics.gradle.util.kotlinMultiplatform
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test

public abstract class FreeleticsMultiplatformPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.plugins.apply("org.jetbrains.kotlin.multiplatform")
        target.plugins.apply(FreeleticsBasePlugin::class.java)

        target.freeleticsExtension.extensions.create("multiplatform", FreeleticsMultiplatformExtension::class.java)

        target.kotlinMultiplatform {
            applyDefaultHierarchyTemplate()
        }

        target.kotlin {
            compilerOptions {
                freeCompilerArgs.add("-Xexpect-actual-classes")
            }
        }

        target.tasks.withType(Test::class.java).configureEach(Test::defaultTestSetup)

        target.disableDefaultJsRepositories(target.path)
        target.rootProject.disableDefaultJsRepositories("root")
    }

    // TODO remove after https://youtrack.jetbrains.com/issue/KT-68533
    @Suppress("DEPRECATION")
    private fun Project.disableDefaultJsRepositories(name: String) {
        plugins.withType(org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootPlugin::class.java) {
            extensions.configure(org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootExtension::class.java) {
                it.downloadBaseUrl = null
            }
        }
        plugins.withType(org.jetbrains.kotlin.gradle.targets.js.yarn.YarnPlugin::class.java) {
            extensions.configure(org.jetbrains.kotlin.gradle.targets.js.yarn.YarnRootExtension::class.java) {
                it.downloadBaseUrl = null
            }
        }
        plugins.withType(org.jetbrains.kotlin.gradle.targets.js.binaryen.BinaryenRootPlugin::class.java) {
            extensions.configure(org.jetbrains.kotlin.gradle.targets.js.binaryen.BinaryenRootExtension::class.java) {
                it.downloadBaseUrl = null
            }
        }
    }
}
