package com.freeletics.gradle.monorepo.plugin

import com.freeletics.gradle.monorepo.setup.applyPlatformConstraints
import com.freeletics.gradle.monorepo.setup.disableMultiplatformLibraryTasks
import com.freeletics.gradle.monorepo.tasks.CheckDependencyRulesTask.Companion.registerCheckDependencyRulesTasks
import com.freeletics.gradle.monorepo.util.ProjectType
import com.freeletics.gradle.plugin.FreeleticsMultiplatformPlugin
import com.freeletics.gradle.util.booleanProperty
import com.freeletics.gradle.util.freeleticsMultiplatformExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

public abstract class CoreMultiplatformPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.plugins.apply(FreeleticsMultiplatformPlugin::class.java)

        target.freeleticsMultiplatformExtension.addAndroidTarget(variantsToPublish = null)
        target.freeleticsMultiplatformExtension.addJvmTarget()
        if (target.booleanProperty("fgp.kotlin.iosTargets", false).get()) {
            target.freeleticsMultiplatformExtension.addIosTargets(includeX64 = true)
        }

        target.freeleticsMultiplatformExtension.useAndroidLint()

        target.registerCheckDependencyRulesTasks(
            allowedProjectTypes = listOf(
                ProjectType.CORE_API,
                ProjectType.CORE_IMPLEMENTATION,
                ProjectType.CORE_TESTING,
            ),
            allowedDependencyProjectTypes = listOf(
                ProjectType.CORE_API,
                ProjectType.CORE_TESTING,
            ),
        )

        target.applyPlatformConstraints(multiplatform = true)
        target.disableMultiplatformLibraryTasks()
    }
}
