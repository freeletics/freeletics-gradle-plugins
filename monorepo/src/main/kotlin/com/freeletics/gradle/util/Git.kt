package com.freeletics.gradle.util

import java.io.File
import java.io.IOException
import java.lang.RuntimeException
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

internal interface Git {
    fun branch(): String
    fun commitSha(): String
    fun commitTimestamp(): String
    fun describe(match: String, exactMatch: Boolean): String
    fun commitsSince(date: LocalDate): Int
}

internal class RealGit(
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

    override fun commitsSince(date: LocalDate): Int {
        val formattedDate = date.format(DateTimeFormatter.ISO_LOCAL_DATE)
        return run("rev-list", "--count", "HEAD", "--since=\"$formattedDate\"").toInt()
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
