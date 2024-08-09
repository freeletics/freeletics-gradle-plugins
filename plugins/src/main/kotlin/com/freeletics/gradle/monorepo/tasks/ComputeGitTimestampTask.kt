package com.freeletics.gradle.monorepo.tasks

import com.android.build.api.variant.BuildConfigField
import com.freeletics.gradle.monorepo.util.RealGit
import com.freeletics.gradle.monorepo.util.computeInfoFromGit
import java.io.File
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.TaskProvider

public abstract class ComputeGitTimestampTask : DefaultTask() {
    @get:Input
    public abstract val computeFromGit: Property<Boolean>

    @get:Input
    public abstract val gitRootDirectory: Property<File>

    @get:OutputFile
    public abstract val outputFile: RegularFileProperty

    @TaskAction
    public fun action() {
        val timestamp = if (computeFromGit.get()) {
            val git = RealGit(gitRootDirectory.get())
            git.commitTimestamp()
        } else {
            "timestamp-not-computed"
        }
        outputFile.get().asFile.writeText(timestamp)
    }

    internal companion object {
        fun Project.registerComputeGitTimestampTask(): TaskProvider<ComputeGitTimestampTask> {
            return tasks.register("computeGitTimestamp", ComputeGitTimestampTask::class.java) { task ->
                task.computeFromGit.set(computeInfoFromGit)
                task.gitRootDirectory.set(rootDir)
                task.outputFile.set(layout.buildDirectory.file("intermediates/git/timestamp.txt"))
            }
        }

        fun TaskProvider<ComputeGitTimestampTask>.mapOutput(): Provider<BuildConfigField<String>> {
            return flatMap { task ->
                task.outputFile.map {
                    BuildConfigField(
                        type = "String",
                        value = "\"${it.asFile.readText()}\"",
                        comment = null,
                    )
                }
            }
        }
    }
}
