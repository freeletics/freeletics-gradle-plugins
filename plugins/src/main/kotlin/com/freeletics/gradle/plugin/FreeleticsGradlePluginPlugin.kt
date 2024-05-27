package com.freeletics.gradle.plugin

import com.freeletics.gradle.util.compilerOptions
import com.freeletics.gradle.util.kotlin
import org.gradle.api.Plugin
import org.gradle.api.Project

public abstract class FreeleticsGradlePluginPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.plugins.apply("java-gradle-plugin")
        target.plugins.apply(FreeleticsJvmPlugin::class.java)

        target.kotlin {
            compilerOptions {
                freeCompilerArgs.add("-Xsam-conversions=class")
            }
        }
    }
}
