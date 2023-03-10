package com.freeletics.gradle.setup

import com.freeletics.gradle.util.getDependency
import com.freeletics.gradle.util.getDependencyOrNull
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.DependencyHandler

internal fun DependencyHandler.addDefaultDependencies(project: Project) {
    add("compileOnly", project.getDependency("androidx.annotations"))
}

internal fun DependencyHandler.addAndroidDependencies(project: Project) {
    add("implementation", project.getDependency("androidx.core"))
    add("implementation", project.getDependency("timber"))
}

internal fun DependencyHandler.addTestDependencies(project: Project) {
    val junit = project.getDependencyOrNull("testing.junit")
    if (junit != null) {
        add("testImplementation", junit)
    }
    val kotestRunner = project.getDependencyOrNull("testing.kotest.runner")
    if (kotestRunner != null) {
        add("testImplementation", kotestRunner)
    }
    val kotestAssertions = project.getDependencyOrNull("testing.kotest.assertions")
    if (kotestAssertions != null) {
        add("testImplementation", kotestAssertions)
    }
}
