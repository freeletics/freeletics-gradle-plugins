package com.freeletics.gradle.util

import org.gradle.api.Project

data class AppType(
    val name: String,
) {
    fun minSdkVersion(project: Project): Int? {
        return project.getVersionOrNull("android.min.$name")?.toInt()
    }
}

fun Project.appType(): AppType? {
    return path.toAppType()
}

fun String.toAppType(): AppType? {
    if (startsWith(":app:")) {
        return AppType(substringAfter(":app:"))
    }
    val firstPathElement = split(":")[1]
    if (firstPathElement.contains("-")) {
        return AppType(firstPathElement.substringAfter("-"))
    }
    return null
}
