package com.freeletics.gradle.tasks

import com.android.build.api.variant.BuildConfigField
import com.freeletics.gradle.util.RealGit
import com.freeletics.gradle.util.computeInfoFromGit
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

public abstract class ComputeGitShaTask : DefaultTask() {

    @get:Input
    public abstract val computeFromGit: Property<Boolean>

    @get:Input
    public abstract val gitRootDirectory: Property<File>

    @get:OutputFile
    public abstract val outputFile: RegularFileProperty

    @TaskAction
    public fun action() {
        val gitSha = if (computeFromGit.get()) {
            val git = RealGit(gitRootDirectory.get())
            git.commitSha()
        } else {
            "sha-not-computed"
        }
        outputFile.get().asFile.writeText(gitSha)
    }

    internal companion object {
        fun Project.registerComputeGitShaTask(): TaskProvider<ComputeGitShaTask> {
            return tasks.register("computeGitSha", ComputeGitShaTask::class.java) { task ->
                task.computeFromGit.set(computeInfoFromGit)
                task.gitRootDirectory.set(rootDir)
                task.outputFile.set(layout.buildDirectory.file("intermediates/git/sha.txt"))
            }
        }

        fun TaskProvider<ComputeGitShaTask>.mapOutput(): Provider<BuildConfigField<String>> {
            return flatMap { task ->
                task.outputFile.map {
                    BuildConfigField(
                        type = "String",
                        value = "\"${it.asFile.readText()}\"",
                        comment = null
                    )
                }
            }
        }
    }
}
