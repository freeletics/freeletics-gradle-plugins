package com.freeletics.gradle.plugin

import com.freeletics.gradle.setup.addAndroidDependencies
import com.freeletics.gradle.setup.addDefaultDependencies
import com.freeletics.gradle.setup.addTestDependencies
import com.freeletics.gradle.tasks.CheckDependencyRulesTask.Companion.registerCheckDependencyRulesTasks
import com.freeletics.gradle.util.ProjectType
import com.freeletics.gradle.util.freeleticsAndroidExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

public abstract class CoreAndroidPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.plugins.apply("com.android.library")
        target.plugins.apply("org.jetbrains.kotlin.android")
        target.plugins.apply(FreeleticsAndroidBasePlugin::class.java)
        target.plugins.apply("com.autonomousapps.dependency-analysis")

        target.freeleticsAndroidExtension.enableParcelize()

        target.dependencies.apply {
            addDefaultDependencies(target)
            addAndroidDependencies(target)
            addTestDependencies(target)
        }

        target.registerCheckDependencyRulesTasks(
            allowedProjectTypes = listOf(
                ProjectType.CORE_API,
                ProjectType.CORE_IMPLEMENTATION,
                ProjectType.CORE_TESTING,
            ),
            allowedDependencyProjectTypes = listOf(
                ProjectType.CORE_API,
                ProjectType.CORE_TESTING,
            ),
        )
    }
}
