package com.freeletics.gradle.scripts

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.split
import com.github.ajalt.clikt.parameters.types.path
import java.nio.file.Path
import java.nio.file.PathMatcher
import java.nio.file.Paths
import kotlin.io.path.absolute
import kotlin.system.exitProcess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.runBlocking

public abstract class FormattingCli : CliktCommand() {
    private val rootDirectory: Path by option("--root", help = ROOT_HELP)
        .path()
        .default(Paths.get(".").absolute())

    private val files: List<Path>? by option("--files", help = FILES_HELP)
        .path()
        .split(Regex("[\n ,]"))

    private val verify: Boolean by option("--fail-on-changes", help = VERIFY_HELP)
        .flag()

    protected abstract fun createPathMatcher(): PathMatcher

    protected abstract fun createFormatter(): Formatter

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun run(): Unit = runBlocking(Dispatchers.IO) {
        val formatter = createFormatter()

        var count = 0
        var hadChanges = false
        var hadErrors = false

        filesToFormat(files, rootDirectory, createPathMatcher())
            .onEach { count++ }
            .flatMapMerge { flowOf(formatter.format(it)) }
            .collect { result ->
                when (result) {
                    FormattingResult.AlreadyFormatted -> {}
                    FormattingResult.Formatted -> {
                        hadChanges = true
                    }
                    is FormattingResult.Error -> {
                        hadErrors = true
                        println(result.message)
                    }
                }
            }

        println("Formatted $count files")
        if (hadErrors || (verify && hadChanges)) {
            exitProcess(1)
        }
    }

    private companion object {
        private const val ROOT_HELP = "The root directory of the project, used as starting point to search files. " +
            "Uses current directory if not specified."
        private const val FILES_HELP = "The files to format, if not specified the root option will be used to" +
            "automatically find files."
        private const val VERIFY_HELP = "When this flag is passed, the script will fail if any files changed during " +
            "formatting."
    }
}
