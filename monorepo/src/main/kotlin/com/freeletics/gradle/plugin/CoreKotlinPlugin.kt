package com.freeletics.gradle.plugin

import com.freeletics.gradle.setup.addDefaultDependencies
import com.freeletics.gradle.setup.addTestDependencies
import com.freeletics.gradle.tasks.CheckDependencyRulesTask.Companion.registerCheckDependencyRulesTasks
import com.freeletics.gradle.util.ProjectType
import org.gradle.api.Plugin
import org.gradle.api.Project

abstract class CoreKotlinPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.plugins.apply("org.jetbrains.kotlin.jvm")
        target.plugins.apply(FreeleticsJvmBasePlugin::class.java)
        target.plugins.apply("com.autonomousapps.dependency-analysis")

        val extension = target.extensions.create("freeletics", FreeleticsJvmExtension::class.java)
        extension.useAndroidLint()

        target.dependencies.apply {
            addDefaultDependencies(target)
            addTestDependencies(target)
        }

        target.registerCheckDependencyRulesTasks(
            allowedProjectTypes = listOf(
                ProjectType.CORE_API,
                ProjectType.CORE_IMPLEMENTATION,
                ProjectType.CORE_TESTING,
            ),
            allowedDependencyProjectTypes = listOfNotNull(
                ProjectType.CORE_API,
                ProjectType.CORE_TESTING,
            ),
        )
    }
}
