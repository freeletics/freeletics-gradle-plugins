package com.freeletics.gradle.setup

import com.android.build.api.dsl.Lint
import com.freeletics.gradle.util.addMaybe
import com.freeletics.gradle.util.getBundleOrNull
import org.gradle.api.Project
import org.gradle.api.file.RegularFile

internal fun Project.configureStandaloneLint() {
    plugins.apply("com.android.lint")

    extensions.configure(Lint::class.java) {
        it.configure(project)
    }
}

internal fun Lint.configure(project: Project) {
    @Suppress("UnstableApiUsage")
    lintConfig = project.layout.settingsDirectory.file("gradle/lint.xml").asFile

    checkReleaseBuilds = false
    checkGeneratedSources = false
    checkTestSources = false
    checkDependencies = true
    ignoreTestSources = true
    abortOnError = true
    warningsAsErrors = true

    htmlReport = true
    htmlOutput = project.reportsFile("lint-result.html").asFile
    textReport = true
    textOutput = project.reportsFile("lint-result.txt").asFile

    disable += "NewerVersionAvailable"
    disable += "GradleDependency"
    disable += "AndroidGradlePluginVersion"

    project.dependencies.addMaybe("lintChecks", project.getBundleOrNull("default-lint"))
}

private fun Project.reportsFile(name: String): RegularFile {
    val projectName = project.path
        .replace("projects", "")
        .replaceFirst(":", "")
        .replace(":", "/")

    @Suppress("UnstableApiUsage")
    return project.layout.settingsDirectory.file("build/reports/lint/$projectName/$name")
}
