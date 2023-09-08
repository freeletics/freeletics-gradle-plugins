package com.freeletics.gradle.util

import java.io.File
import java.io.IOException
import java.lang.RuntimeException
import java.util.concurrent.TimeUnit

public interface Git {
    public fun branch(): String
    public fun commitSha(): String
    public fun commitTimestamp(): String
    public fun describe(match: String, exactMatch: Boolean): String
}

public class RealGit(
    private val rootDir: File,
) : Git {

    override fun branch(): String {
        return run("branch", "--show-current")
    }

    override fun commitSha(): String {
        return run("describe", "--match", "DO_NOT_MATCH", "--always", "--abbrev=10", "--dirty")
    }

    override fun commitTimestamp(): String {
        return run("show", "-s", "--format=%ci", commitSha())
    }

    override fun describe(match: String, exactMatch: Boolean): String {
        return if (exactMatch) {
            run("describe", "--match", match, "--exact-match")
        } else {
            run("describe", "--match", match)
        }
    }

    private fun run(vararg command: String): String {
        try {
            val proc = ProcessBuilder("bash", "-c", "git ${command.joinToString(" ")}")
                .directory(rootDir)
                .start()

            proc.waitFor(60, TimeUnit.SECONDS)
            return proc.inputStream.bufferedReader().readText().trim()
        } catch (e: IOException) {
            throw RuntimeException("Git command ${command.toList()} failed", e)
        }
    }
}
