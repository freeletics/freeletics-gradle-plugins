package com.freeletics.gradle.monorepo.plugin

import com.freeletics.gradle.monorepo.setup.applyPlatformConstraints
import com.freeletics.gradle.monorepo.setup.disableAndroidLibraryTasks
import com.freeletics.gradle.monorepo.tasks.CheckDependencyRulesTask.Companion.registerCheckDependencyRulesTasks
import com.freeletics.gradle.monorepo.util.ProjectType
import com.freeletics.gradle.monorepo.util.appType
import com.freeletics.gradle.plugin.FreeleticsAndroidPlugin
import com.freeletics.gradle.util.freeleticsAndroidExtension
import com.freeletics.gradle.util.freeleticsExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

public abstract class FeatureAndroidPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.plugins.apply(FreeleticsAndroidPlugin::class.java)

        val extension = target.freeleticsExtension.extensions.create("legacy", LegacyExtension::class.java)

        target.freeleticsExtension.useCompose()
        target.freeleticsAndroidExtension.enableAndroidResources()
        target.freeleticsAndroidExtension.enableParcelize()

        target.afterEvaluate {
            target.registerCheckDependencyRulesTasks(
                allowedProjectTypes = listOf(ProjectType.FEATURE_IMPLEMENTATION),
                allowedDependencyProjectTypes = listOfNotNull(
                    ProjectType.CORE_API,
                    ProjectType.CORE_TESTING,
                    ProjectType.DOMAIN_API,
                    ProjectType.DOMAIN_TESTING,
                    ProjectType.FEATURE_NAV,
                    // TODO remove when nav modules don't depend on legacy modules anymore
                    if (extension.allowLegacyDependencies) {
                        ProjectType.LEGACY
                    } else {
                        null
                    },
                ),
            )
        }

        target.applyPlatformConstraints()
        target.disableAndroidLibraryTasks()
    }
}
