package com.freeletics.gradle.tasks

import org.gradle.api.Project
import org.gradle.api.tasks.Copy

abstract class UpdateLicensesTask : Copy() {
    companion object {
        fun Project.registerUpdateLicensesTask() {
            tasks.register("updateLicenses", UpdateLicensesTask::class.java) { task ->
                task.from("$buildDir/reports/licensee/release/artifacts.json")
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
