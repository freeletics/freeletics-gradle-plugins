package com.freeletics.gradle.plugin

import com.freeletics.gradle.setup.configureStandaloneLint
import com.freeletics.gradle.setup.disableDefaultJsRepositories
import com.freeletics.gradle.util.compilerOptionsCommon
import com.freeletics.gradle.util.freeleticsExtension
import com.freeletics.gradle.util.kotlin
import com.freeletics.gradle.util.kotlinMultiplatform
import org.gradle.api.Plugin
import org.gradle.api.Project

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
    }
}
