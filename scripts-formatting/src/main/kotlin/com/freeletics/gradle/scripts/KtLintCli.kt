package com.freeletics.gradle.scripts

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.split
import com.github.ajalt.clikt.parameters.types.path
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.absolute
import kotlin.system.exitProcess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.runBlocking

public class KtLintCli : CliktCommand() {
    private val rootDirectory: Path by option(
        "--root",
        help = "The root directory of the project, used as starting point to search files and to find editorconfig. " +
            "Uses current directory if not specified",
    ).path().default(Paths.get(".").absolute())

    private val files: List<Path>? by option(
        "--files",
        help = "The files to format, if not specified all kt/kts files in the current directory and its " +
            "subdirectories will be formatted.",
    ).path().split(Regex("[\n ,]"))

    private val verify: Boolean by option(
        "--fail-on-changes",
        help = "When this flag is passed, the script will fail if any files changed during formatting",
    ).flag()

    private val init: Boolean by option(
        "--init",
        help = "When this flag is passed, the script will do nothing. Can be used to eagerly compile it",
    ).flag()

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun run(): Unit = runBlocking(Dispatchers.IO) {
        if (init) {
            return@runBlocking
        }

        val formatter = KtLintFormatter(rootDirectory.resolve(".editorconfig"))

        var count = 0
        var hadChanges = false
        var hadErrors = false

        filesToFormat(files, rootDirectory, kotlinMatcher)
            .onEach { count++ }
            .flatMapMerge { formatter.format(it) }
            .collect {
                if (it.corrected) {
                    hadChanges = true
                    if (verify) {
                        it.print(prefix = "e: (corrected)")
                    } else {
                        it.print(prefix = "corrected:")
                    }
                } else {
                    hadErrors = true
                    it.print(prefix = "e:")
                }
            }

        println("Formatted $count files")

        if (hadErrors || (verify && hadChanges)) {
            // fail
            exitProcess(1)
        }
    }

    override fun help(context: Context): String {
        return "CLI that runs ktlint."
    }
}
