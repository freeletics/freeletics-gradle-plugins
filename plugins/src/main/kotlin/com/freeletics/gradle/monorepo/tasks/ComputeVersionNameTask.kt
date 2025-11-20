package com.freeletics.gradle.monorepo.tasks

import com.freeletics.gradle.monorepo.util.RealGit
import com.freeletics.gradle.monorepo.util.computeInfoFromGit
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.TaskProvider
import org.gradle.api.tasks.UntrackedTask

@UntrackedTask(because = "Relies on local git state")
public abstract class ComputeVersionNameTask : DefaultTask() {
    @get:Input
    public abstract val computeFromGit: Property<Boolean>

    @get:Input
    public abstract val gitTagName: Property<String>

    @get:InputDirectory
    @get:PathSensitive(PathSensitivity.RELATIVE)
    public abstract val gitRootDirectory: RegularFileProperty

    @get:OutputFile
    public abstract val outputFile: RegularFileProperty

    @TaskAction
    public fun action() {
        val versionName = if (computeFromGit.get()) {
            val git = RealGit(gitRootDirectory.get().asFile)
            computeVersionName(git, gitTagName.get())
        } else {
            "99.0.0"
        }
        outputFile.get().asFile.writeText(versionName)
    }

    internal companion object {
        fun Project.registerComputeVersionNameTask(gitTagName: String): TaskProvider<ComputeVersionNameTask> {
            return tasks.register("computeVersionName", ComputeVersionNameTask::class.java) { task ->
                task.computeFromGit.set(computeInfoFromGit)
                task.gitTagName.set(gitTagName)
                task.gitRootDirectory.set(rootDir)
                task.outputFile.set(layout.buildDirectory.file("intermediates/git/version-name.txt"))
            }
        }

        fun TaskProvider<ComputeVersionNameTask>.mapOutput(): Provider<String> {
            return flatMap { task ->
                task.outputFile.map { it.asFile.readText() }
            }
        }
    }
}
