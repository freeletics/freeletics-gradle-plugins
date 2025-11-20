package com.freeletics.gradle.monorepo.plugin

import com.freeletics.gradle.monorepo.setup.applyPlatformConstraints
import com.freeletics.gradle.monorepo.setup.disableMultiplatformLibraryTasks
import com.freeletics.gradle.monorepo.tasks.CheckDependencyRulesTask.Companion.registerCheckDependencyRulesTasks
import com.freeletics.gradle.monorepo.util.ProjectType
import com.freeletics.gradle.monorepo.util.projectType
import com.freeletics.gradle.plugin.FreeleticsMultiplatformPlugin
import com.freeletics.gradle.util.freeleticsAndroidMultiplatformExtension
import com.freeletics.gradle.util.freeleticsExtension
import com.freeletics.gradle.util.freeleticsMultiplatformExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

public abstract class FeaturePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.plugins.apply(FreeleticsMultiplatformPlugin::class.java)
        target.freeleticsMultiplatformExtension.addDefaultTargets()

        target.freeleticsExtension.useCompose()
        target.freeleticsAndroidMultiplatformExtension.enableAndroidResources()

        val legacy = target.freeleticsExtension.extensions.create("legacy", LegacyExtension::class.java)

        target.afterEvaluate {
            target.registerCheckDependencyRulesTasks(
                allowedProjectTypes = listOf(
                    ProjectType.FEATURE_IMPLEMENTATION,
                    ProjectType.FEATURE_DEBUG,
                ),
                allowedDependencyProjectTypes = listOfNotNull(
                    ProjectType.CORE_API,
                    ProjectType.CORE_TESTING,
                    ProjectType.CORE_DEBUG,
                    ProjectType.DOMAIN_API,
                    ProjectType.DOMAIN_TESTING,
                    ProjectType.DOMAIN_DEBUG,
                    ProjectType.FEATURE_NAV,
                    ProjectType.FEATURE_IMPLEMENTATION.takeIf { target.projectType() == ProjectType.FEATURE_DEBUG },
                    ProjectType.FEATURE_DEBUG,
                    ProjectType.LEGACY.takeIf { legacy.allowLegacyDependencies },
                ),
            )
        }

        target.applyPlatformConstraints(multiplatform = true)
        target.disableMultiplatformLibraryTasks()
    }
}
