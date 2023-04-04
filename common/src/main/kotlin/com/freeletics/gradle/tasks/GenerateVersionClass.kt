package com.freeletics.gradle.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

abstract class GenerateVersionClass : DefaultTask() {

    @get:Input
    abstract val packageName: Property<String>

    @get:Input
    abstract val version: Property<String>

    @get:OutputDirectory
    abstract val outputDirectory: DirectoryProperty

    @TaskAction
    fun generate() {
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
