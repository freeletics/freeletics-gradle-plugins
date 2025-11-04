package com.freeletics.gradle.monorepo.util

import org.gradle.api.Project

internal data class AppType(
    val name: String,
)

internal fun Project.appType(): AppType? {
    return path.toAppType()
}

internal fun String.toAppType(): AppType? {
    val parts = split(":")
    if (parts[1] == "app") {
        val suffix = parts[2].substringAfterLast("-")
        // for app modules that are platform specific variants (-android, -desktop) we shouldn't consider the suffix
        return if (platformSuffixes.contains(suffix)) {
            AppType(parts[2].substringBeforeLast("-"))
        } else {
            AppType(parts[2])
        }
    } else if (parts[1].contains("-")) {
        return AppType(parts[1].substringAfter("-"))
    }
    return null
}

private val platformSuffixes = listOf("android", "desktop")
