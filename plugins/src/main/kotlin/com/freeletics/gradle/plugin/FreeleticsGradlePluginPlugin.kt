package com.freeletics.gradle.plugin

import com.freeletics.gradle.setup.setupGr8
import com.freeletics.gradle.util.getDependency
import com.freeletics.gradle.util.kotlin
import org.gradle.api.Plugin
import org.gradle.api.Project

public abstract class FreeleticsGradlePluginPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.plugins.apply("java-gradle-plugin")
        target.plugins.apply(FreeleticsJvmPlugin::class.java)
        target.plugins.apply("com.gradleup.gr8")
        target.plugins.apply("com.autonomousapps.plugin-best-practices-plugin")

        target.kotlin {
            compilerOptions {
                freeCompilerArgs.add("-Xsam-conversions=class")
            }
        }

        target.setupGr8()

        target.dependencies.add("compileOnly", target.getDependency("gradle-api"))
        target.dependencies.add("shade", target.getDependency("kotlin-stdlib"))
    }
}
