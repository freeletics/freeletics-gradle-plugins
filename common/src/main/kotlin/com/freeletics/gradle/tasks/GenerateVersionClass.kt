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
        val file = outputDirectory.file("${packageName.get().replace(".", "/")}/Version.kt").get().asFile
        file.writeText(
            """
            // Generated file. Do not edit!
            package ${packageName.get()}
            
            internal const val VERSION = "${version.get()}"
            """.trimIndent()
        )
    }
}
