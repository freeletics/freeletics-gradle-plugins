package com.freeletics.gradle.monorepo.setup

import com.android.build.api.variant.AndroidComponentsExtension
import com.freeletics.gradle.monorepo.util.capitalize
import org.gradle.api.Project

internal fun Project.disableAndroidApplicationTasks() {
    disableAndroidTasks(androidAppLintTasksToDisableExceptOneVariant, "debug")
}

internal fun Project.disableAndroidLibraryTasks() {
    disableAndroidTasks(androidLibraryTasksToDisable)
    disableAndroidTasks(androidLibraryLintTasksToDisable)
    disableAndroidTasks(androidLibraryLintTasksToDisableExceptOneVariant, "debug")
}

internal fun Project.disableKotlinLibraryTasks() {
    disableTasks(listOf("assemble"))
    disableTasks(lintTasksToDisableJvm)
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

// disable these tasks since we never want to build an aar out of
// library modules and AGP consumes the individual elements directly
private val androidLibraryTasksToDisable = listOf(
    "assemble",
    "assemble{VARIANT}",
    "bundle{VARIANT}Aar",
)

// for libraries remove all reporting tasks so that they only
// have the analyze task since we have an aggregated report at
// the app level
private val androidLibraryLintTasksToDisable = listOf(
    // report
    "lint",
    "lint{VARIANT}",
    "lintReport{VARIANT}",
    "copy{VARIANT}AndroidLintReports",
    // fix
    "lintFix",
    "lintFix{VARIANT}",
    // baseline
    "updateLintBaseline",
    "updateLintBaseline{VARIANT}",
)

// disable debug variant of these tasks, we're only running on release
private val androidLibraryLintTasksToDisableExceptOneVariant = listOf(
    // analyze
    "lintAnalyze{VARIANT}",
)

// disable debug variant of these tasks, we're only running on release
private val androidAppLintTasksToDisableExceptOneVariant = listOf(
    // analyze
    "lintAnalyze{VARIANT}",
    // report
    "lint{VARIANT}",
    "lintReport{VARIANT}",
    "copy{VARIANT}AndroidLintReports",
    // fix
    "lintFix{VARIANT}",
    // baseline
    "updateLintBaseline{VARIANT}",
)

// same as the Android library tasks, only keep analyze and the report
// is created in the app module
private val lintTasksToDisableJvm = listOf(
    "lint",
    "lintReport",
    "copyAndroidLintReports",
    "lintFix",
    "updateLintBaseline",
    // TODO these shouldn't be created by AGP in the first place
    "lintVital",
    "lintVitalAnalyze",
    "lintVitalReport",
)
