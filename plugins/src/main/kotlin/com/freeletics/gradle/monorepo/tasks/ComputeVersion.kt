package com.freeletics.gradle.monorepo.tasks

import com.freeletics.gradle.monorepo.util.Git
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.IsoFields

/**
 * Get the app version name, which is computed using the branch name or `git describe`.
 */
public fun computeVersionName(git: Git, gitTagPrefix: String): String {
    val version = versionNameFromTag(git, gitTagPrefix)
    checkNotNull(version) { "Did not find a previous release/tag" }
    return version
}

/**
 * Get the app version name, which is computed using the branch name or `git describe`.
 */
public fun computeVersionCode(git: Git, gitTagPrefix: String, localDate: LocalDateTime): Int {
    val version = versionNameFromTag(git, gitTagPrefix, exactMatch = true)
    return if (version != null) {
        computeVersionCodeFromTag(version)
    } else {
        check(git.branch() == "main") {
            "Version code can only be computed on the main branch and tagged commits"
        }
        val lastRelease = versionNameFromTag(git, gitTagPrefix, initialRelease = true)
        checkNotNull(lastRelease) { "Did not find a previous release/tag" }
        computeVersionCodeFromLastRelease(lastRelease, localDate)
    }
}

/**
 * Tagged commits use the version to compute the version code.
 *
 * 2_100_000_000 - maximum allowed value
 *    24_xxx_xxx - major version 24
 *    xx_x31_xxx - minor version 31
 *    xx_xxx_x00 - patch version 0
 */
public fun computeVersionCodeFromTag(version: String): Int {
    val parts = version.split(".")
    check(parts.size == 3)
    val major = parts[0].toInt()
    val minor = parts[1].toInt()
    val patch = parts[2].toInt()
    checkVersions(major, minor, patch)
    return versionCode(major, minor, patch, extra = 0)
}

/**
 * Untagged builds will use the major and minor version of the last version for
 * the computed build number.
 *
 * We use the day of week in the 100 digit to get a higher build number than
 * anything the last release can reach. The last 2 digits are used for the time
 * of day to generally have the ability to produce multiple builds per day
 * for testing (in practice we get a new build number every 15 minutes which is
 * good enough for one-off manual tests).

 * 2_100_000_000 - maximum allowed value
 *    24_xxx_xxx - major version 24
 *    xx_x31_xxx - minor version 31
 *    xx_xxx_1xx - day of week
 *    xx_xxx_x99 - time of day in 15 minute intervals
 */
private fun computeVersionCodeFromLastRelease(lastRelease: String, localDate: LocalDateTime): Int {
    val versionParts = lastRelease.split(".")
    check(versionParts.size == 3)
    val suffixParts = versionParts[2].split("-")
    check(suffixParts.size == 3)
    val major = versionParts[0].toInt()
    val minor = versionParts[1].toInt()
    // Monday = 100, Sunday = 700
    val dayOfWeek = localDate.dayOfWeek.value * 100
    // Time of day in 15 minute intervals -> values from 0 to 96
    val time = (localDate.hour * 60 + localDate.minute) / 15
    val extra = dayOfWeek + time
    return versionCode(major, minor, patch = 0, extra)
}

private fun versionCode(major: Int, minor: Int, patch: Int, extra: Int): Int {
    checkVersions(major, minor, patch)
    val versionCode = major * 1_000_000 +
        minor * 1_000 +
        patch +
        extra
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
private fun versionNameFromTag(
    git: Git,
    gitTagPrefix: String,
    initialRelease: Boolean = false,
    exactMatch: Boolean = false,
): String? {
    val patchVersion = if (initialRelease) "0" else "[0-9]*"
    // match will filter tags to consider based on a regex
    val describe = git.describe(match = "\"$gitTagPrefix/v[1-9][0-9]\\.[0-9]*\\.$patchVersion\"", exactMatch)

    if (describe.isBlank()) {
        return null
    }
    return versionNameFromTag(describe, gitTagPrefix)
}

/**
 * See [versionNameFromTag] overload.
 */
public fun versionNameFromTag(tag: String, gitTagPrefix: String): String {
    return tag.substringAfter("$gitTagPrefix/v")
}

/**
 * Computes a version based on the given date. The last 2 digits of the year are taken
 * as major version, the week number as minor. The week based year is used to stay
 * consistent
 *
 * For example:
 * 2024-11-12 -> 24.46.0
 * 2025-12-30 -> 25.1.0
 * 2025-12-01 -> 25.1.0
 */
public fun versionBasedOnDate(date: LocalDate): String {
    val year = date.get(IsoFields.WEEK_BASED_YEAR)
    val week = date.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR)

    // Version pattern is 'yy.ww.x'
    val major = year - 2000 // so that major version is 'yy', i.e. 2022 - 2000 => '22.1.0'
    val minor = week
    val patch = 0
    return "${major}.${minor}.${patch}"
}
