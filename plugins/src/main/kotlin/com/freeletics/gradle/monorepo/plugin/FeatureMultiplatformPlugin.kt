package com.freeletics.gradle.monorepo.plugin

import com.freeletics.gradle.monorepo.setup.applyPlatformConstraints
import com.freeletics.gradle.monorepo.setup.disableAndroidLibraryTasks
import com.freeletics.gradle.monorepo.tasks.CheckDependencyRulesTask.Companion.registerCheckDependencyRulesTasks
import com.freeletics.gradle.monorepo.util.ProjectType
import com.freeletics.gradle.monorepo.util.appType
import com.freeletics.gradle.plugin.FreeleticsAndroidPlugin
import com.freeletics.gradle.plugin.FreeleticsMultiplatformPlugin
import com.freeletics.gradle.util.freeleticsAndroidExtension
import com.freeletics.gradle.util.freeleticsExtension
import com.freeletics.gradle.util.freeleticsMultiplatformExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

public abstract class FeatureMultiplatformPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.plugins.apply(FreeleticsMultiplatformPlugin::class.java)

        target.freeleticsMultiplatformExtension.addAndroidTarget()
        target.freeleticsMultiplatformExtension.addIosTargets()

        target.freeleticsExtension.useCompose()
        target.freeleticsAndroidExtension.minSdkVersion(target.appType()?.minSdkVersion(target))
        target.freeleticsAndroidExtension.enableAndroidResources()
        target.freeleticsAndroidExtension.enableParcelize()

        target.afterEvaluate {
            target.registerCheckDependencyRulesTasks(
                allowedProjectTypes = listOf(ProjectType.FEATURE_IMPLEMENTATION),
                allowedDependencyProjectTypes = listOf(
                    ProjectType.CORE_API,
                    ProjectType.CORE_TESTING,
                    ProjectType.DOMAIN_API,
                    ProjectType.DOMAIN_TESTING,
                    ProjectType.FEATURE_NAV,
                ),
            )
        }

        // TODO target.applyPlatformConstraints()
        // TODO target.disableAndroidLibraryTasks()
    }
}
