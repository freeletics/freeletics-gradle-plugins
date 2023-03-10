package com.freeletics.gradle.setup

import com.android.build.api.dsl.Lint
import org.gradle.api.Project

internal fun Project.configureStandaloneLint() {
    extensions.configure(Lint::class.java) {
        it.configure(project)
    }
}

@Suppress("UnstableApiUsage")
internal fun Lint.configure(project: Project) {
    lintConfig = project.rootProject.file("gradle/lint.xml")

    checkReleaseBuilds = false
    checkGeneratedSources = false
    checkTestSources = false
    checkDependencies = true
    ignoreTestSources = true
    abortOnError = true
    warningsAsErrors = true

    val projectName = project.path
        .replace("projects", "")
        .replaceFirst(":", "")
        .replace(":", "/")

    htmlReport = true
    htmlOutput = project.file("${project.rootProject.buildDir}/reports/lint/$projectName/lint-result.html")
    textReport = true
    textOutput = project.file("${project.rootProject.buildDir}/reports/lint/$projectName/lint-result.txt")
}
