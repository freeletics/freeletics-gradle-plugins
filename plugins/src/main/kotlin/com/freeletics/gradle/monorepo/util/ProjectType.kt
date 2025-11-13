package com.freeletics.gradle.monorepo.util

import org.gradle.api.Project

internal enum class ProjectType(
    val fullName: String,
    val prefix: String,
    val suffix: String,
) {
    APP(":app:*", "app", ""),
    CORE_API(":core:*:api", "core", "api"),
    CORE_IMPLEMENTATION(":core:*:implementation", "core", "implementation"),
    CORE_TESTING(":core:*:testing", "core", "testing"),
    CORE_DEBUG(":core:*:debug", "core", "debug"),
    DOMAIN_API(":domain:*:api", "domain", "api"),
    DOMAIN_IMPLEMENTATION(":domain:*:implementation", "domain", "implementation"),
    DOMAIN_DEBUG(":domain:*:debug", "domain", "debug"),
    DOMAIN_TESTING(":domain:*:testing", "domain", "testing"),
    FEATURE_NAV(":feature:*:nav", "domain", "nav"),
    FEATURE_IMPLEMENTATION(":feature:*:implementation", "domain", "implementation"),
    FEATURE_DEBUG(":feature:*:debug", "domain", "debug"),

    // TODO phase out
    LEGACY(":legacy-freeletics:*", "legacy", ""),
}

internal fun Project.projectType(): ProjectType {
    return path.toProjectType()
}

internal fun String.toProjectType(): ProjectType {
    return toProjectTypeOrNull() ?: throw IllegalStateException("Unknown project type $this")
}

internal fun String.toProjectTypeOrNull(): ProjectType? {
    return when {
        startsWith(":app:") -> ProjectType.APP
        startsWith(":core:") && endsWith(":api") -> ProjectType.CORE_API
        startsWith(":core:") && endsWith(":implementation") -> ProjectType.CORE_IMPLEMENTATION
        startsWith(":core:") && endsWith(":testing") -> ProjectType.CORE_TESTING
        startsWith(":core:") && endsWith(":debug") -> ProjectType.CORE_DEBUG
        startsWith(":domain") && endsWith(":api") -> ProjectType.DOMAIN_API
        startsWith(":domain") && endsWith(":implementation") -> ProjectType.DOMAIN_IMPLEMENTATION
        startsWith(":domain") && endsWith(":testing") -> ProjectType.DOMAIN_TESTING
        startsWith(":domain") && endsWith(":debug") -> ProjectType.DOMAIN_DEBUG
        startsWith(":feature") && endsWith(":implementation") -> ProjectType.FEATURE_IMPLEMENTATION
        startsWith(":feature") && endsWith(":nav") -> ProjectType.FEATURE_NAV
        startsWith(":feature") && endsWith(":debug") -> ProjectType.FEATURE_DEBUG
        startsWith(":legacy-freeletics:") -> ProjectType.LEGACY
        else -> null
    }
}
