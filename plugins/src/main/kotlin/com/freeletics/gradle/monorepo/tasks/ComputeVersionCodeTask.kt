package com.freeletics.gradle.monorepo.tasks

import com.freeletics.gradle.monorepo.util.RealGit
import com.freeletics.gradle.monorepo.util.computeInfoFromGit
import java.io.File
import java.time.LocalDateTime
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
import org.gradle.work.DisableCachingByDefault

@UntrackedTask(because = "Relies on local git state")
public abstract class ComputeVersionCodeTask : DefaultTask() {
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
        val versionCode = if (computeFromGit.get()) {
            val git = RealGit(gitRootDirectory.get().asFile)
            computeVersionCode(git, gitTagName.get(), LocalDateTime.now())
        } else {
            Int.MAX_VALUE
        }
        outputFile.get().asFile.writeText(versionCode.toString())
    }

    internal companion object {
        fun Project.registerComputeVersionCodeTask(gitTagName: String): TaskProvider<ComputeVersionCodeTask> {
            return tasks.register("computeVersionCode", ComputeVersionCodeTask::class.java) { task ->
                task.computeFromGit.set(computeInfoFromGit)
                task.gitTagName.set(gitTagName)
                task.gitRootDirectory.set(rootDir)
                task.outputFile.set(layout.buildDirectory.file("intermediates/git/version-code.txt"))
            }
        }

        fun TaskProvider<ComputeVersionCodeTask>.mapOutput(): Provider<Int> {
            return flatMap { task ->
                task.outputFile.map { it.asFile.readText().toInt() }
            }
        }
    }
}
