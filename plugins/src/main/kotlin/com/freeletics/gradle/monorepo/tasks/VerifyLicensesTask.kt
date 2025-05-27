package com.freeletics.gradle.monorepo.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.VerificationException

public abstract class VerifyLicensesTask : DefaultTask() {
    @get:InputFile
    @get:PathSensitive(PathSensitivity.NONE)
    public abstract val generatedJson: RegularFileProperty

    @get:InputFile
    @get:PathSensitive(PathSensitivity.NONE)
    public abstract val existingJson: RegularFileProperty

    @TaskAction
    public fun verify() {
        val generated = generatedJson.get().asFile
            .readLines()
            .joinToString(separator = "\n") { line -> if (line.contains("\"version\": \"")) "" else line }
            .trim()
        val existing = existingJson.get().asFile
            .readText()
            .trim()
        if (generated != existing) {
            throw VerificationException("Generated licenses changed. Run ./gradlew updateLicenses to update")
        }
    }

    internal companion object {
        fun Project.registerVerifyLicensesTask() {
            val task = tasks.register("verifyLicenses", VerifyLicensesTask::class.java) { task ->
                task.generatedJson.set(
                    project.layout.buildDirectory.file("reports/licensee/androidRelease/artifacts.json"),
                )
                task.existingJson.set(
                    project.layout.projectDirectory.file("src/main/assets/license_acknowledgements.json"),
                )

                task.dependsOn("licenseeAndroidRelease")
            }
            tasks.named("check").configure {
                it.dependsOn(task)
            }
        }
    }
}
