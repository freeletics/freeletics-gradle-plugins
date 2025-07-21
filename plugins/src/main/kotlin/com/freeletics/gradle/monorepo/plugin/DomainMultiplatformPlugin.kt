package com.freeletics.gradle.monorepo.plugin

import com.freeletics.gradle.monorepo.setup.applyPlatformConstraints
import com.freeletics.gradle.monorepo.setup.disableMultiplatformLibraryTasks
import com.freeletics.gradle.monorepo.tasks.CheckDependencyRulesTask.Companion.registerCheckDependencyRulesTasks
import com.freeletics.gradle.monorepo.util.ProjectType
import com.freeletics.gradle.monorepo.util.appType
import com.freeletics.gradle.monorepo.util.projectType
import com.freeletics.gradle.plugin.FreeleticsMultiplatformPlugin
import com.freeletics.gradle.util.booleanProperty
import com.freeletics.gradle.util.freeleticsAndroidExtension
import com.freeletics.gradle.util.freeleticsMultiplatformExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

public abstract class DomainMultiplatformPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.plugins.apply(FreeleticsMultiplatformPlugin::class.java)

        target.freeleticsMultiplatformExtension.addAndroidTarget(variantsToPublish = null)
        target.freeleticsMultiplatformExtension.addJvmTarget()
        if (target.booleanProperty("fgp.kotlin.iosTargets", false).get()) {
            target.freeleticsMultiplatformExtension.addIosTargets(includeX64 = true)
        }

        target.freeleticsAndroidExtension.minSdkVersion(target.appType()?.minSdkVersion(target))

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
                if (target.projectType() == ProjectType.DOMAIN_IMPLEMENTATION) ProjectType.FEATURE_NAV else null,
            ),
        )

        target.applyPlatformConstraints(multiplatform = true)
        target.disableMultiplatformLibraryTasks()
    }
}
