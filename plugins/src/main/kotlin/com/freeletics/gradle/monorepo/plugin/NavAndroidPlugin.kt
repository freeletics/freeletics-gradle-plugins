package com.freeletics.gradle.monorepo.plugin

import com.freeletics.gradle.monorepo.setup.applyPlatformConstraints
import com.freeletics.gradle.monorepo.setup.disableAndroidLibraryTasks
import com.freeletics.gradle.monorepo.tasks.CheckDependencyRulesTask.Companion.registerCheckDependencyRulesTasks
import com.freeletics.gradle.monorepo.util.ProjectType
import com.freeletics.gradle.monorepo.util.appType
import com.freeletics.gradle.plugin.FreeleticsAndroidPlugin
import com.freeletics.gradle.util.freeleticsAndroidExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

public abstract class NavAndroidPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.plugins.apply(FreeleticsAndroidPlugin::class.java)

        target.freeleticsAndroidExtension.minSdkVersion(target.appType()?.minSdkVersion(target))
        target.freeleticsAndroidExtension.enableParcelize()

        target.registerCheckDependencyRulesTasks(
            allowedProjectTypes = listOf(ProjectType.FEATURE_NAV),
            allowedDependencyProjectTypes = listOf(
                ProjectType.CORE_API,
                ProjectType.CORE_TESTING,
                ProjectType.DOMAIN_API,
                ProjectType.DOMAIN_TESTING,
            ),
        )

        target.applyPlatformConstraints()
        target.disableAndroidLibraryTasks()
    }
}
