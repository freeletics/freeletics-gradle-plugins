package com.freeletics.gradle.util

import java.time.LocalDate

internal class FakeGit(
    var branch: String = "main",
    var commitSha: String = "abcdefghij",
    var commitTimestamp: String = "2022-10-21 16:36:11 +0200",
    var describe: String = "",
    var commitsSince: Int = 0,
) : Git {
    override fun branch() = branch

    override fun commitSha() = commitSha

    override fun commitTimestamp() = commitTimestamp

    override fun describe(match: String, exactMatch: Boolean) = describe

    override fun commitsSince(date: LocalDate) = commitsSince
}
