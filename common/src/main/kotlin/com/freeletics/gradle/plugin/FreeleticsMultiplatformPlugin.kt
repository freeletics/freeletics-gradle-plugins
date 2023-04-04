package com.freeletics.gradle.plugin

import com.freeletics.gradle.setup.defaultTestSetup
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test

public abstract class FreeleticsMultiplatformPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.plugins.apply("org.jetbrains.kotlin.multiplatform")
        target.plugins.apply(FreeleticsBasePlugin::class.java)
        target.plugins.apply("com.autonomousapps.dependency-analysis")

        target.extensions.create("freeletics", FreeleticsMultiplatformExtension::class.java)

        target.tasks.withType(Test::class.java).configureEach(Test::defaultTestSetup)
    }
}
