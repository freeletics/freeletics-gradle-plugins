package com.freeletics.gradle.monorepo.plugin

import com.freeletics.gradle.monorepo.setup.applyPlatformConstraints
import com.freeletics.gradle.monorepo.setup.disableKotlinLibraryTasks
import com.freeletics.gradle.monorepo.tasks.CheckDependencyRulesTask.Companion.registerCheckDependencyRulesTasks
import com.freeletics.gradle.monorepo.util.ProjectType
import com.freeletics.gradle.plugin.FreeleticsJvmPlugin
import com.freeletics.gradle.util.freeleticsJvmExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

public abstract class LegacyKotlinPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.plugins.apply(FreeleticsJvmPlugin::class.java)

        target.freeleticsJvmExtension.useAndroidLint()

        target.registerCheckDependencyRulesTasks(
            allowedProjectTypes = listOf(ProjectType.LEGACY),
            allowedDependencyProjectTypes = listOfNotNull(
                ProjectType.CORE_API,
                ProjectType.CORE_IMPLEMENTATION,
                ProjectType.CORE_TESTING,
                ProjectType.DOMAIN_API,
                ProjectType.DOMAIN_IMPLEMENTATION,
                ProjectType.DOMAIN_TESTING,
                ProjectType.LEGACY,
            ),
        )

        target.applyPlatformConstraints()
        target.disableKotlinLibraryTasks()
    }
}
