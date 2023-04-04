package com.freeletics.gradle.util

import org.gradle.api.Project

public enum class ProjectType(
    internal val fullName: String,
) {
    APP(":app:*"),
    CORE_API(":core:*:api"),
    CORE_IMPLEMENTATION(":core:*:implementation"),
    CORE_TESTING(":core:*:testing"),
    DOMAIN_API(":domain:*:api"),
    DOMAIN_IMPLEMENTATION(":domain:*:implementation"),
    DOMAIN_TESTING(":domain:*:testing"),
    FEATURE_NAV(":feature:*:nav"),
    FEATURE_IMPLEMENTATION(":feature:*:implementation"),

    // TODO phase out
    LEGACY_APP(":legacy-freeletics:app"),
    LEGACY(":legacy-freeletics:*"),
}

internal fun Project.projectType(): ProjectType {
    return path.toProjectType()
}

internal fun String.toProjectType(): ProjectType {
    return when {
        startsWith(":app:") -> ProjectType.APP
        startsWith(":core:") && endsWith(":api") -> ProjectType.CORE_API
        startsWith(":core:") && endsWith(":implementation") -> ProjectType.CORE_IMPLEMENTATION
        startsWith(":core:") && endsWith(":testing") -> ProjectType.CORE_TESTING
        startsWith(":domain") && endsWith(":api") -> ProjectType.DOMAIN_API
        startsWith(":domain") && endsWith(":implementation") -> ProjectType.DOMAIN_IMPLEMENTATION
        startsWith(":domain") && endsWith(":testing") -> ProjectType.DOMAIN_TESTING
        startsWith(":feature") && endsWith(":implementation") -> ProjectType.FEATURE_IMPLEMENTATION
        startsWith(":feature") && endsWith(":nav") -> ProjectType.FEATURE_NAV
        this == ":legacy-freeletics:app" -> ProjectType.LEGACY_APP
        startsWith(":legacy-freeletics:") -> ProjectType.LEGACY
        else -> throw IllegalStateException("Unknown project type $this")
    }
}
