package com.freeletics.gradle.monorepo.plugin

import com.freeletics.gradle.monorepo.setup.applyPlatformConstraints
import com.freeletics.gradle.monorepo.setup.disableKotlinLibraryTasks
import com.freeletics.gradle.monorepo.tasks.CheckDependencyRulesTask.Companion.registerCheckDependencyRulesTasks
import com.freeletics.gradle.monorepo.util.ProjectType
import com.freeletics.gradle.plugin.FreeleticsJvmPlugin
import com.freeletics.gradle.util.freeleticsExtension
import com.freeletics.gradle.util.freeleticsJvmExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

public abstract class DomainKotlinPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.plugins.apply(FreeleticsJvmPlugin::class.java)

        val extension = target.freeleticsExtension.extensions.create("legacy", LegacyExtension::class.java)

        target.freeleticsJvmExtension.useAndroidLint()

        target.afterEvaluate {
            target.registerCheckDependencyRulesTasks(
                allowedProjectTypes = listOf(
                    ProjectType.DOMAIN_API,
                    ProjectType.DOMAIN_IMPLEMENTATION,
                    ProjectType.DOMAIN_TESTING,
                ),
                allowedDependencyProjectTypes = listOfNotNull(
                    ProjectType.CORE_API,
                    ProjectType.CORE_TESTING,
                    ProjectType.DOMAIN_API,
                    ProjectType.DOMAIN_TESTING,
                    // TODO remove after migrating domain modules away from legacy
                    if (extension.allowLegacyDependencies) {
                        ProjectType.LEGACY
                    } else {
                        null
                    },
                ),
            )
        }

        target.applyPlatformConstraints()
        target.disableKotlinLibraryTasks()
    }
}
