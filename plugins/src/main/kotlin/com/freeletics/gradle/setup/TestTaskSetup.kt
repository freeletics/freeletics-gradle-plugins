package com.freeletics.gradle.setup

import org.gradle.api.tasks.testing.Test

internal fun Test.defaultTestSetup() {
    useJUnitPlatform()

    val projectName = project.path
        .replace("projects", "")
        .replaceFirst(":", "")
        .replace(":", "/")
    reports.html.outputLocation.set(project.rootProject.layout.buildDirectory.dir("reports/tests/$projectName"))
    reports.junitXml.outputLocation.set(project.rootProject.layout.buildDirectory.dir("reports/tests/$projectName"))
}
