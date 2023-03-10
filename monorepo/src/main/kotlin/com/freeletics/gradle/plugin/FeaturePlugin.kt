package com.freeletics.gradle.plugin

import com.freeletics.gradle.setup.addAndroidDependencies
import com.freeletics.gradle.setup.addDefaultDependencies
import com.freeletics.gradle.setup.addTestDependencies
import com.freeletics.gradle.tasks.CheckDependencyRulesTask.Companion.registerCheckDependencyRulesTasks
import com.freeletics.gradle.util.ProjectType
import com.freeletics.gradle.util.appType
import org.gradle.api.Plugin
import org.gradle.api.Project

abstract class FeaturePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.plugins.apply("com.android.library")
        target.plugins.apply("org.jetbrains.kotlin.android")
        target.plugins.apply(FreeleticsAndroidBasePlugin::class.java)
        target.plugins.apply("com.autonomousapps.dependency-analysis")

        val extension = target.extensions.create("freeletics", FeatureExtension::class.java)

        extension.minSdkVersion(target.appType()?.minSdkVersion(target))
        extension.enableAndroidResources()
        extension.enableParcelize()

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
