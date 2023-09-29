package com.freeletics.gradle.monorepo.plugin

import com.freeletics.gradle.monorepo.setup.addAndroidDependencies
import com.freeletics.gradle.monorepo.setup.addDefaultDependencies
import com.freeletics.gradle.monorepo.setup.addTestDependencies
import com.freeletics.gradle.monorepo.tasks.CheckDependencyRulesTask.Companion.registerCheckDependencyRulesTasks
import com.freeletics.gradle.monorepo.util.ProjectType
import com.freeletics.gradle.monorepo.util.projectType
import com.freeletics.gradle.plugin.FreeleticsAndroidPlugin
import com.freeletics.gradle.util.freeleticsAndroidExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

public abstract class LegacyAndroidPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.plugins.apply(FreeleticsAndroidPlugin::class.java)

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
