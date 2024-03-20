package com.freeletics.gradle.setup

import com.freeletics.gradle.util.android
import com.google.devtools.ksp.gradle.KspExtension
import java.io.File
import org.gradle.api.Project
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.process.CommandLineArgumentProvider
import org.jetbrains.kotlin.gradle.plugin.KaptExtension

internal fun Project.configureProcessing(
    useKsp: Boolean,
    vararg arguments: ProcessingArgument,
): String {
    if (useKsp) {
        plugins.apply("com.google.devtools.ksp")

        if (arguments.isNotEmpty()) {
            extensions.configure(KspExtension::class.java) { extension ->
                arguments.forEach { arg ->
                    when (arg) {
                        is BasicArgument ->
                            extension.arg(arg.key, arg.value)

                        is CliArgumentProvider ->
                            extension.arg(arg.provider)
                    }
                }
            }
        }

        return "ksp"
    } else {
        plugins.apply("org.jetbrains.kotlin.kapt")

        val basicArguments = arguments.filterIsInstance<BasicArgument>()
        if (basicArguments.isNotEmpty()) {
            extensions.configure(KaptExtension::class.java) { extension ->
                extension.mapDiagnosticLocations = true
                extension.correctErrorTypes = true

                extension.arguments {
                    basicArguments.forEach { (key, value) ->
                        arg(key, value)
                    }
                }
            }
        }
        val cliArguments = arguments.filterIsInstance<CliArgumentProvider>()
            .map { KaptArgProviderWrapper(it.provider) }
        if (cliArguments.isNotEmpty()) {
            project.android {
                defaultConfig {
                    javaCompileOptions {
                        annotationProcessorOptions {
                            compilerArgumentProviders.addAll(cliArguments)
                        }
                    }
                }
            }
        }

        return "kapt"
    }
}

internal sealed interface ProcessingArgument

internal fun basicArgument(pair: Pair<String, String>): ProcessingArgument = BasicArgument(pair.first, pair.second)
private data class BasicArgument(
    val key: String,
    val value: String,
) : ProcessingArgument

internal fun argumentProvider(provider: CommandLineArgumentProvider): ProcessingArgument = CliArgumentProvider(provider)
private data class CliArgumentProvider(
    val provider: CommandLineArgumentProvider,
) : ProcessingArgument


private class KaptArgProviderWrapper(
    val provider: CommandLineArgumentProvider,
) : CommandLineArgumentProvider {

    override fun asArguments(): Iterable<String> {
        // prepends arguments with "-A" for kapt or javac
        return provider.asArguments().map { "-A$it" }
    }
}
