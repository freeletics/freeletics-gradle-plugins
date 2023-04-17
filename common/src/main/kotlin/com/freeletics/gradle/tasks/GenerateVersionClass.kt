package com.freeletics.gradle.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

public abstract class GenerateVersionClass : DefaultTask() {

    @get:Input
    public abstract val packageName: Property<String>

    @get:Input
    public abstract val version: Property<String>

    @get:OutputDirectory
    public abstract val outputDirectory: DirectoryProperty

    @TaskAction
    public fun generate() {
        val path = packageName.get().replace(".", "/")
        val parentFolder = outputDirectory.dir(path).get().asFile
        parentFolder.mkdirs()
        val file = parentFolder.resolve("Version.kt")
        file.writeText(
            """
            // Generated file. Do not edit!
            package ${packageName.get()}
            
            internal const val VERSION = "${version.get()}"
            """.trimIndent()
        )
    }
}
