package com.freeletics.gradle.tasks

import com.freeletics.gradle.util.Git
import java.time.LocalDate
import java.time.temporal.WeekFields

/**
 * Get the app version name, which is computed using the branch name or `git describe`.
 */
internal fun computeVersionName(git: Git, gitTagName: String): String {
    return versionFromReleaseOrHotfixBranch(git, gitTagName)
        ?: versionFromTag(git, gitTagName)
        // fallback for apps before the first release
        ?: "0.0.0-${git.commitSha()}"
}

/**
 * Get the app version name, which is computed using the branch name or `git describe`.
 */
internal fun computeVersionCode(git: Git, gitTagName: String, localDate: LocalDate): Int {
    val major: Int
    val minor: Int
    val patch: Int
    val extra: Int

    val version = versionFromReleaseOrHotfixBranch(git, gitTagName) ?: versionFromTag(
        git,
        gitTagName,
        exactMatch = true
    )
    if (version != null) {
        // if we are on a release branch or building a tagged commit use the version to compute the version code
        //
        // 2_100_000_000 - maximum allowed value
        //    22_xxx_xxx - major version 22
        //    xx_31x_xxx - minor version 31
        //    xx_xxx_0xx - patch version 0
        //    xx_xxx_x99 - commit count 99

        val parts = version.split(".")
        check(parts.size == 3)
        major = parts[0].toInt()
        minor = parts[1].toInt()
        patch = parts[2].toInt()
        checkVersions(major, minor, patch)

        val releaseCutOffDate = localDate
            .with(WeekFields.ISO.weekBasedYear(), major + 2_000L)
            .with(WeekFields.ISO.weekOfWeekBasedYear(), minor.toLong())
            .with(WeekFields.ISO.dayOfWeek(), 7) // SUNDAY of the week the date is in

        extra = git.commitsSince(releaseCutOffDate)
        check(extra < 100) { "More than 99 commits found since the release was created" }
    } else {
        check(git.branch() == "main") {
            "Version code can only be computed on main, release and hotfix branches as well as tags"
        }

        // Non release branch and untagged builds will use last week's version for the computed build number.
        // Patch is always 0 in this case however it will set the digit for thousands at least to 1 (this digit
        // is unused by regular releases). This guarantees that a nighly build will always have a higher build number
        // than last week's release (including hotfixes) but one that is lower than then the release created at the
        // end of the week, which should be the safest behavior in regard to build number based fencing.
        //
        // 2_100_000_000 - maximum allowed value
        //    22_xxx_xxx - major version 22
        //    xx_31x_xxx - minor version 31
        //    xx_xx1_099 - commit count 99

        val lastWeek = localDate.minusDays(7)
            .with(WeekFields.ISO.dayOfWeek(), 7) // SUNDAY of the week the date is in

        major = lastWeek.get(WeekFields.ISO.weekBasedYear()) - 2000
        minor = lastWeek.get(WeekFields.ISO.weekOfWeekBasedYear())
        patch = 0
        checkVersions(major, minor, patch)

        val commits = git.commitsSince(lastWeek)
        // add 1000 to the number of commits so that these one
        extra = commits + 1000
        check(extra < 10_000) { "More than 8999 commits found since the last release was created" }
    }

    val versionCode = major * 1_000_000 +
        minor * 10_000 +
        patch * 100 +
        extra
    check(versionCode < 1_000_000_000) { "Version code should always be lower than 1 billion, was $versionCode" }

    return versionCode
}

private fun checkVersions(major: Int, minor: Int, patch: Int) {
    // major is limited to 100 which will only be reached in year 2100
    check(major < 100) { "Major version is limited to 99, was $major" }
    // minor is limited to the number of weeks in a year, so 52 or 53 in practice
    check(minor < 100) { "Minor version is limited to 99, was $minor" }
    // patch is limited to a single digit, if we need more we have other problems
    check(patch < 10) { "Patch is limited to 9, was $patch" }
}

/**
 * If branch is a release or hotfix branch return the last part of the branch name
 * e.g. [branch name] -> [resulting version]
 * release/fl/6.49.0 -> 6.49.0
 * hotfix/fl/7.0.2 -> 7.0.2
 */
private fun versionFromReleaseOrHotfixBranch(git: Git, gitTagName: String): String? {
    val branch = git.branch()
    return if (branch.startsWith("release/$gitTagName/") ||
        branch.startsWith("hotfix/$gitTagName/")
    ) {
        branch.split("/").last()
    } else {
        null
    }
}

/**
 * If a tag exists `describe` matches the tag, e.g. `fl/v7.4.0` -> 7.4.0
 * otherwise it's `[tag]-[commits-since-tag]-[current-sha]`, e.g. `fl/v7.4.0-32-g5e2416d73f` -> 7.4.0-32-g5e2416d73f
 */
private fun versionFromTag(git: Git, gitTagName: String, exactMatch: Boolean = false): String? {
    // match will filter tags to consider based on a regex
    val describe = git.describe(match = "\"$gitTagName/v[0-9.]*\"", exactMatch)

    if (describe.isBlank()) {
        return null
    }
    return describe.substringAfter("$gitTagName/v")
}
