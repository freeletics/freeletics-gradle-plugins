package com.freeletics.gradle.setup

import com.google.devtools.ksp.gradle.KspExtension
import org.gradle.api.Project
import org.gradle.process.CommandLineArgumentProvider

internal fun Project.configureProcessing(arguments: List<CommandLineArgumentProvider> = emptyList()) {
    plugins.apply("com.google.devtools.ksp")

    extensions.configure(KspExtension::class.java) { extension ->
        arguments.forEach {
            extension.arg(it)
        }
    }
}

internal fun basicArgument(key: String, value: String) = CommandLineArgumentProvider { listOf("$key=$value") }
