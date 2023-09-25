package com.freeletics.gradle.plugin

import com.freeletics.gradle.setup.addAndroidDependencies
import com.freeletics.gradle.setup.addDefaultDependencies
import com.freeletics.gradle.setup.addTestDependencies
import com.freeletics.gradle.tasks.CheckDependencyRulesTask.Companion.registerCheckDependencyRulesTasks
import com.freeletics.gradle.util.ProjectType
import com.freeletics.gradle.util.appType
import com.freeletics.gradle.util.freeleticsAndroidExtension
import com.freeletics.gradle.util.freeleticsExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

public abstract class FeaturePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.plugins.apply(FreeleticsAndroidPlugin::class.java)

        val extension = target.freeleticsExtension.extensions.create("legacy", LegacyExtension::class.java)

        target.freeleticsAndroidExtension.minSdkVersion(target.appType()?.minSdkVersion(target))
        target.freeleticsAndroidExtension.enableAndroidResources()
        target.freeleticsAndroidExtension.enableParcelize()

        target.dependencies.apply {
            addDefaultDependencies(target)
            addAndroidDependencies(target)
            addTestDependencies(target)
        }

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
    }
}
