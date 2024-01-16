package com.freeletics.gradle.monorepo.plugin

import com.freeletics.gradle.monorepo.setup.applyPlatformConstraints
import com.freeletics.gradle.monorepo.setup.disableKotlinLibraryTasks
import com.freeletics.gradle.monorepo.tasks.CheckDependencyRulesTask.Companion.registerCheckDependencyRulesTasks
import com.freeletics.gradle.monorepo.util.ProjectType
import com.freeletics.gradle.plugin.FreeleticsJvmPlugin
import com.freeletics.gradle.plugin.FreeleticsMultiplatformPlugin
import com.freeletics.gradle.util.freeleticsJvmExtension
import com.freeletics.gradle.util.freeleticsMultiplatformExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

public abstract class DomainMultiplatformPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.plugins.apply(FreeleticsMultiplatformPlugin::class.java)

        target.freeleticsMultiplatformExtension.addIosTargets(target.path.substring(1).replace(":", "-"))
        target.freeleticsMultiplatformExtension.addAndroidTarget()
        // TODO target.freeleticsJvmExtension.useAndroidLint()

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
            ),
        )

        target.applyPlatformConstraints()
        // TODO target.disableKotlinLibraryTasks()
    }
}
