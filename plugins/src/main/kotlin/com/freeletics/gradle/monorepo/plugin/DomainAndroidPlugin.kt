package com.freeletics.gradle.monorepo.plugin

import com.freeletics.gradle.monorepo.setup.applyPlatformConstraints
import com.freeletics.gradle.monorepo.setup.disableAndroidLibraryTasks
import com.freeletics.gradle.monorepo.tasks.CheckDependencyRulesTask.Companion.registerCheckDependencyRulesTasks
import com.freeletics.gradle.monorepo.util.ProjectType
import com.freeletics.gradle.monorepo.util.projectType
import com.freeletics.gradle.plugin.FreeleticsAndroidPlugin
import com.freeletics.gradle.util.freeleticsAndroidExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

public abstract class DomainAndroidPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.plugins.apply(FreeleticsAndroidPlugin::class.java)

        target.freeleticsAndroidExtension.enableParcelize()

        target.registerCheckDependencyRulesTasks(
            allowedProjectTypes = listOf(
                ProjectType.DOMAIN_API,
                ProjectType.DOMAIN_IMPLEMENTATION,
                ProjectType.DOMAIN_TESTING,
                ProjectType.DOMAIN_DEBUG,
            ),
            allowedDependencyProjectTypes = listOfNotNull(
                ProjectType.CORE_API,
                ProjectType.CORE_TESTING,
                ProjectType.CORE_DEBUG,
                ProjectType.DOMAIN_API,
                ProjectType.DOMAIN_TESTING,
                ProjectType.DOMAIN_DEBUG,
                if (target.projectType() == ProjectType.DOMAIN_IMPLEMENTATION) ProjectType.FEATURE_NAV else null,
            ),
        )

        target.applyPlatformConstraints()
        target.disableAndroidLibraryTasks()
    }
}
