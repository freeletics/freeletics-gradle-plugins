package com.freeletics.gradle.setup

import com.android.build.api.dsl.Lint
import com.freeletics.gradle.util.addMaybe
import com.freeletics.gradle.util.getBundleOrNull
import org.gradle.api.Project
import org.gradle.api.file.RegularFile
import org.gradle.api.provider.Provider

internal fun Project.configureStandaloneLint() {
    extensions.configure(Lint::class.java) {
        it.configure(project)
    }
}

internal fun Lint.configure(project: Project) {
    lintConfig = project.rootProject.file("gradle/lint.xml")

    checkReleaseBuilds = false
    checkGeneratedSources = false
    checkTestSources = false
    checkDependencies = true
    ignoreTestSources = true
    abortOnError = true
    warningsAsErrors = true

    htmlReport = true
    htmlOutput = project.reportsFile("lint-result.html").get().asFile
    textReport = true
    textOutput = project.reportsFile("lint-result.txt").get().asFile

    disable += "NewerVersionAvailable"
    disable += "GradleDependency"
    disable += "AndroidGradlePluginVersion"

    project.dependencies.addMaybe("lintChecks", project.getBundleOrNull("default-lint"))
}

private fun Project.reportsFile(name: String): Provider<RegularFile> {
    val projectName = project.path
        .replace("projects", "")
        .replaceFirst(":", "")
        .replace(":", "/")

    return rootProject.layout.buildDirectory.file("reports/lint/$projectName/$name")
}
