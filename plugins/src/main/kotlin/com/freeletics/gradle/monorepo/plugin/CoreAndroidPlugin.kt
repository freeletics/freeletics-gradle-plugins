package com.freeletics.gradle.monorepo.plugin

import com.freeletics.gradle.monorepo.setup.addAndroidDependencies
import com.freeletics.gradle.monorepo.setup.addDefaultDependencies
import com.freeletics.gradle.monorepo.setup.addTestDependencies
import com.freeletics.gradle.monorepo.tasks.CheckDependencyRulesTask.Companion.registerCheckDependencyRulesTasks
import com.freeletics.gradle.monorepo.util.ProjectType
import com.freeletics.gradle.plugin.FreeleticsAndroidPlugin
import com.freeletics.gradle.util.freeleticsAndroidExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

public abstract class CoreAndroidPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.plugins.apply(FreeleticsAndroidPlugin::class.java)

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
