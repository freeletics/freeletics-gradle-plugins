package com.freeletics.gradle.monorepo.setup

import com.android.build.api.variant.AndroidComponentsExtension
import com.freeletics.gradle.monorepo.util.capitalize
import org.gradle.api.Project

internal fun Project.disableAndroidApplicationTasks() {
    disableAndroidTasks(androidAppLintTasksToDisableExceptOneVariant, "debug")
}

internal fun Project.disableMultiplatformApplicationTasks() {
    // TODO lint tasks
}

internal fun Project.disableMultiplatformLibraryTasks() {
    disableTasks(listOf("assemble"))
    // TODO lint tasks
}

private fun Project.disableAndroidTasks(names: List<String>, variantToKeep: String = "") {
    extensions.configure<AndroidComponentsExtension<*, *, *>>("androidComponents") { components ->
        components.onVariants { variant ->
            if (variant.name != variantToKeep) {
                val variantAwareNames = names.map { it.replace("{VARIANT}", variant.name.capitalize()) }
                disableTasks(variantAwareNames)
            }
        }
    }
}

private fun Project.disableTasks(names: List<String>) {
    // since AGP 8.3 the tasks.named will fail during project sync
    if (providers.systemProperty("idea.sync.active").getOrElse("false").toBoolean()) {
        return
    }

    afterEvaluate {
        names.forEach { name ->
            tasks.named(name).configure {
                it.enabled = false
                it.description = "DISABLED"
                it.setDependsOn(mutableListOf<Any>())
            }
        }
    }
}

// disable debug variant of these tasks, we're only running on release
private val androidAppLintTasksToDisableExceptOneVariant get() = listOf(
    // analyze
    "lintAnalyze{VARIANT}",
    // report
    "lint{VARIANT}",
    "lintReport{VARIANT}",
    "copy{VARIANT}LintReports",
    // fix
    "lintFix{VARIANT}",
    // baseline
    "updateLintBaseline{VARIANT}",
)
