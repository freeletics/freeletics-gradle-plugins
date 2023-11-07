package com.freeletics.gradle.monorepo.tasks

import com.freeletics.gradle.monorepo.util.Git
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Get the app version name, which is computed using the branch name or `git describe`.
 */
public fun computeVersionName(git: Git, gitTagName: String): String {
    val version = versionFromTag(git, gitTagName)
    checkNotNull(version) { "Did not find a previous release/tag" }
    return version
}

/**
 * Get the app version name, which is computed using the branch name or `git describe`.
 */
public fun computeVersionCode(git: Git, gitTagName: String, localDate: LocalDateTime): Int {
    val major: Int
    val minor: Int
    val patch: Int
    val extra: Int

    val version = versionFromTag(git, gitTagName, exactMatch = true)
    if (version != null) {
        // if we are building a tagged commit use the version to compute the version code
        //
        // New (since major v24):
        //
        // 2_100_000_000 - maximum allowed value
        //    24_xxx_xxx - major version 24
        //    xx_x31_xxx - minor version 31
        //    xx_xxx_x00 - patch version 0
        //
        // Old (until major v24):
        //
        // 2_100_000_000 - maximum allowed value
        //    22_xxx_xxx - major version 22
        //    xx_31x_xxx - minor version 31
        //    xx_xxx_000 - patch version 0

        val parts = version.split(".")
        check(parts.size == 3)
        major = parts[0].toInt()
        minor = parts[1].toInt()
        patch = parts[2].toInt()
        checkVersions(major, minor, patch)
        extra = 0
    } else {
        check(git.branch() == "main") {
            "Version code can only be computed on the main branch and tagged commits"
        }

        // Untagged builds will use the major and minor version of the last version for
        // the computed build number.
        //
        // New (since major v24):
        //
        // We use the day of week in the 100 digit to get a higher build number than
        // anything the last release can reach. The last 2 digits are used for the time
        // of day to generally have the ability to produce multiple builds per day
        // for testing (in practice we get a new build number every 15 minutes which is
        // good enough for one-off manual tests).
        //
        // 2_100_000_000 - maximum allowed value
        //    24_xxx_xxx - major version 24
        //    xx_x31_xxx - minor version 31
        //    xx_xxx_1xx - day of week
        //    xx_xxx_x99 - time of day in 15 minute intervals
        //
        // Old (until major v24):
        //
        // We use the day of week in the 100 digit to get a higher build number than
        // anything the last release can reach. The last 3 digits are used for the
        // number of commits since the last release.
        //
        // 2_100_000_000 - maximum allowed value
        //    22_xxx_xxx - major version 22
        //    xx_31x_xxx - minor version 31
        //    xx_xx1_xxx - day of week
        //    xx_xxx_099 - commit count 99

        val lastRelease = versionFromTag(git, gitTagName, initialRelease = true)
        checkNotNull(lastRelease) { "Did not find a previous release/tag" }
        val versionParts = lastRelease.split(".")
        check(versionParts.size == 3)
        val suffixParts = versionParts[2].split("-")
        check(suffixParts.size == 3)
        major = versionParts[0].toInt()
        minor = versionParts[1].toInt()
        patch = 0
        checkVersions(major, minor, patch)

        extra = if (major >= 24) {
            // Monday = 100, Sunday = 700
            val dayOfWeek = localDate.dayOfWeek.value * 100
            // Time of day in 15 minute intervals -> values from 0 to 96
            val time = (localDate.hour * 60 + localDate.minute) / 15
            dayOfWeek + time
        } else {
            // Monday = 1000, Sunday = 7000
            val dayOfWeek = localDate.dayOfWeek.value * 1_000
            // the returned version has 7.4.0-32-g5e2416d73f as format where the 32 is the commit count since the tag
            val commitsSinceLastRelease = suffixParts[1].toInt()
            check(commitsSinceLastRelease < 1_000) { "More than 999 commits found since the last release was created" }
            dayOfWeek + commitsSinceLastRelease
        }
    }

    val versionCode = if (major >= 24) {
        major * 1_000_000 +
            minor * 1_000 +
            patch +
            extra
    } else {
        major * 1_000_000 +
            minor * 10_000 +
            patch +
            extra
    }
    check(versionCode < 1_000_000_000) { "Version code should always be lower than 1 billion, was $versionCode" }

    return versionCode
}

private fun checkVersions(major: Int, minor: Int, patch: Int) {
    // major is limited to 100 which will only be reached in year 2100
    check(major < 100) { "Major version is limited to 99, was $major" }
    // minor is limited to the number of weeks in a year, so 52 or 53 in practice
    check(minor < 100) { "Minor version is limited to 99, was $minor" }
    // patch is limited to three digits
    check(patch < 100) { "Patch is limited to 99, was $patch" }
}

/**
 * If a tag exists `describe` matches the tag, e.g. `fl/v7.4.0` -> 7.4.0
 * otherwise it's `[tag]-[commits-since-tag]-[current-sha]`, e.g. `fl/v7.4.0-32-g5e2416d73f` -> 7.4.0-32-g5e2416d73f
 *
 * The [exactMatch] option makes it so that only the former would be returned and `null` if the current commit
 * is untagged.
 *
 * If [initialRelease] is set to `true` only tags ending in `.0` will be matched.
 */
private fun versionFromTag(
    git: Git,
    gitTagName: String,
    initialRelease: Boolean = false,
    exactMatch: Boolean = false,
): String? {
    val patchVersion = if (initialRelease) "0" else "[0-9]*"
    // match will filter tags to consider based on a regex
    val describe = git.describe(match = "\"$gitTagName/v[1-9][0-9]\\.[0-9]*\\.$patchVersion\"", exactMatch)

    if (describe.isBlank()) {
        return null
    }
    return describe.substringAfter("$gitTagName/v")
}
