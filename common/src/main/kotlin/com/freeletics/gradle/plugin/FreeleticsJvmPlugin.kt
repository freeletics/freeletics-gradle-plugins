package com.freeletics.gradle.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project

abstract class FreeleticsJvmPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.plugins.apply("org.jetbrains.kotlin.jvm")
        target.plugins.apply(FreeleticsJvmBasePlugin::class.java)
        target.plugins.apply("com.autonomousapps.dependency-analysis")

        target.extensions.create("freeletics", FreeleticsJvmExtension::class.java)
    }
}
