package com.freeletics.gradle.monorepo.plugin

import com.android.build.api.dsl.Lint
import com.freeletics.gradle.monorepo.setup.applyPlatformConstraints
import com.freeletics.gradle.monorepo.setup.disableMultiplatformApplicationTasks
import com.freeletics.gradle.monorepo.tasks.CheckDependencyRulesTask.Companion.registerCheckDependencyRulesTasks
import com.freeletics.gradle.monorepo.util.ProjectType
import com.freeletics.gradle.plugin.FreeleticsMultiplatformPlugin
import com.freeletics.gradle.util.defaultPackageName
import com.freeletics.gradle.util.freeleticsMultiplatformExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.compose.ComposeExtension
import org.jetbrains.compose.desktop.DesktopExtension
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

public abstract class AppDesktopPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.plugins.apply(FreeleticsMultiplatformPlugin::class.java)
        target.plugins.apply("org.jetbrains.compose")

        target.freeleticsMultiplatformExtension.addJvmTarget()

        target.freeleticsMultiplatformExtension.useAndroidLint()

        target.extensions.configure(ComposeExtension::class.java) { compose ->
            compose.extensions.configure(DesktopExtension::class.java) { desktop ->
                desktop.application { app ->
                    app.mainClass = "${target.defaultPackageName()}.AppKt"
                    app.nativeDistributions {
                        it.targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
                        it.packageName = target.defaultPackageName()
                        // TODO compute version from task
                        it.packageVersion = "1.0.0"
                    }
                }
            }
        }

        target.extensions.configure(Lint::class.java) {
            it.baseline = target.file("lint-baseline.xml")
        }

        target.registerCheckDependencyRulesTasks(
            allowedProjectTypes = listOf(ProjectType.APP),
            allowedDependencyProjectTypes = listOfNotNull(
                ProjectType.CORE_API,
                ProjectType.CORE_IMPLEMENTATION,
                ProjectType.CORE_TESTING,
                ProjectType.DOMAIN_API,
                ProjectType.DOMAIN_IMPLEMENTATION,
                ProjectType.DOMAIN_TESTING,
                ProjectType.FEATURE_IMPLEMENTATION,
                ProjectType.FEATURE_NAV,
                ProjectType.APP,
            ),
        )

        target.applyPlatformConstraints(multiplatform = true)
        target.disableMultiplatformApplicationTasks()
    }
}
