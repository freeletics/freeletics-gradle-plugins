package com.freeletics.gradle.plugin

import com.freeletics.gradle.setup.addAndroidDependencies
import com.freeletics.gradle.setup.addDefaultDependencies
import com.freeletics.gradle.setup.addTestDependencies
import com.freeletics.gradle.tasks.CheckDependencyRulesTask.Companion.registerCheckDependencyRulesTasks
import com.freeletics.gradle.util.ProjectType
import com.freeletics.gradle.util.freeleticsAndroidExtension
import com.freeletics.gradle.util.projectType
import org.gradle.api.Plugin
import org.gradle.api.Project

public abstract class LegacyAndroidPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.plugins.apply("com.android.library")
        target.plugins.apply("org.jetbrains.kotlin.android")
        target.plugins.apply(FreeleticsAndroidBasePlugin::class.java)
        target.plugins.apply("com.autonomousapps.dependency-analysis")

        target.freeleticsAndroidExtension.enableAndroidResources()
        target.freeleticsAndroidExtension.enableParcelize()

        target.dependencies.apply {
            addDefaultDependencies(target)
            addAndroidDependencies(target)
            addTestDependencies(target)
        }

        target.registerCheckDependencyRulesTasks(
            allowedProjectTypes = listOf(ProjectType.LEGACY, ProjectType.LEGACY_APP),
            allowedDependencyProjectTypes = listOfNotNull(
                ProjectType.CORE_API,
                ProjectType.CORE_TESTING,
                ProjectType.DOMAIN_API,
                ProjectType.DOMAIN_IMPLEMENTATION,
                ProjectType.DOMAIN_TESTING,
                ProjectType.FEATURE_NAV,
                ProjectType.LEGACY,
            ) + if (target.projectType() == ProjectType.LEGACY_APP) {
                listOf(
                    ProjectType.CORE_IMPLEMENTATION,
                    ProjectType.FEATURE_IMPLEMENTATION,
                )
            } else {
                emptyList()
            },
        )
    }
}
