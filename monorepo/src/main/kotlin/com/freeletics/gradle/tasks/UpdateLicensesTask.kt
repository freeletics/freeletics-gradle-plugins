package com.freeletics.gradle.tasks

import org.gradle.api.Project
import org.gradle.api.tasks.Copy

public abstract class UpdateLicensesTask : Copy() {
    internal companion object {
        fun Project.registerUpdateLicensesTask() {
            tasks.register("updateLicenses", UpdateLicensesTask::class.java) { task ->
                task.from(project.layout.buildDirectory.file("reports/licensee/release/artifacts.json"))
                task.into("src/main/assets")

                task.rename("artifacts.json", "license_acknowledgements.json")

                task.filter { line ->
                    if (line.contains("\"version\": \"")) "" else line
                }

                task.dependsOn("licenseeRelease")
            }
        }
    }
}
