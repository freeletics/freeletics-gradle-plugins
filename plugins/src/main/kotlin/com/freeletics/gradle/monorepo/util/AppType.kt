package com.freeletics.gradle.monorepo.util

import com.freeletics.gradle.util.getVersionOrNull
import org.gradle.api.Project

internal data class AppType(
    val name: String,
) {
    fun minSdkVersion(project: Project): Int? {
        return project.getVersionOrNull("android.min.$name")?.toInt()
    }
}

internal fun Project.appType(): AppType? {
    return path.toAppType()
}

internal fun String.toAppType(): AppType? {
    if (startsWith(":app:")) {
        return AppType(substringAfter(":app:").substringBefore("-"))
    }
    val firstPathElement = split(":")[1]
    if (firstPathElement.contains("-")) {
        return AppType(firstPathElement.substringAfter("-"))
    }
    return null
}
