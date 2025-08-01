package com.freeletics.gradle.util

import com.freeletics.gradle.monorepo.util.ProjectType
import com.freeletics.gradle.monorepo.util.appType
import com.freeletics.gradle.monorepo.util.toProjectType
import org.gradle.api.Project

internal fun Project.defaultPackageName(): String {
    val appType = project.appType()
    val prefix = if (appType != null) {
        stringProperty("fgp.defaultPackageName.${appType.name}").get()
    } else {
        stringProperty("fgp.defaultPackageName").get()
    }
    if (booleanProperty("fgp.useLegacyPackageNaming", false).get()) {
        return legacyPackageName(path, prefix)
    }
    return defaultPackageName(path, prefix)
}

internal fun defaultPackageName(path: String, prefix: String): String {
    val projectType = path.toProjectType()
    val pathElements = path.split(":").drop(1)
    val projectPackageElements = when (projectType) {
        // skip first part of the app name because it's generally already part of the prefix
        ProjectType.APP -> pathElements[1].transformPathPart().drop(1)
        ProjectType.CORE_API -> pathElements[1].transformPathPart()
        ProjectType.CORE_IMPLEMENTATION -> pathElements[1].transformPathPart() + projectType.suffix
        ProjectType.CORE_TESTING -> pathElements[1].transformPathPart() + projectType.suffix
        ProjectType.DOMAIN_API -> pathElements[1].transformPathPart()
        ProjectType.DOMAIN_IMPLEMENTATION -> pathElements[1].transformPathPart() + projectType.suffix
        ProjectType.DOMAIN_TESTING -> pathElements[1].transformPathPart() + projectType.suffix
        ProjectType.FEATURE_NAV -> pathElements[1].transformPathPart() + projectType.suffix
        ProjectType.FEATURE_IMPLEMENTATION -> pathElements[1].transformPathPart()
        ProjectType.LEGACY -> pathElements[1].transformPathPart()
    }
    val packageElements = prefix.split(".") + projectPackageElements
    return packageElements.joinToString(separator = ".") { it.lowercase() }
}

private fun String.transformPathPart(): List<String> {
    return split("-")
}

internal fun legacyPackageName(path: String, prefix: String): String {
    val prefixElements = prefix.split(".")
    val pathElements = path.drop(1)
        .split(":")
        .mapIndexed { index, pathElement ->
            val parts = pathElement.split("-")
            if (index == 0) {
                // top level folders like core, domain, feature etc. handle dashes separately by
                // having them become separate package elements, also ignore the -freeletics suffix
                // to avoid package names like com.freeletics.domain.freeletics
                parts.filterNot { prefixElements.contains(it) }.joinToString(separator = ".")
            } else {
                // for second and lower level folders dashes are ignored and the elements are
                // merged into one word
                parts.joinToString(separator = "")
            }
        }
    return (prefixElements + pathElements).joinToString(separator = ".")
}
