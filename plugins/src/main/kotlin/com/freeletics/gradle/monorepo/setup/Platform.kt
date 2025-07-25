package com.freeletics.gradle.monorepo.setup

import org.gradle.api.Project

internal fun Project.applyPlatformConstraints(multiplatform: Boolean = false) {
    val platformDependency = dependencies.enforcedPlatform(rootProject)
    configurations.configureEach { config ->
        if (isPlatformConfigurationName(config.name, multiplatform)) {
            config.dependencies.add(platformDependency)
        }
    }
}

// adapted from https://github.com/ZacSweers/CatchUp/blob/347db46d82497990ff10c441ecc75c0c9eedf7c4/buildSrc/src/main/kotlin/dev/zacsweers/catchup/gradle/CatchUpPlugin.kt#L68-L80
private fun isPlatformConfigurationName(name: String, multiplatform: Boolean): Boolean {
    // TODO: KT-61653 KMP fails on androidTest*Api dependencies
    if (multiplatform) {
        MULTIPLATFORM_API_IGNORE_CONFIGURATION.forEach {
            if (name.contains(it, ignoreCase = true) && name.endsWith("api", ignoreCase = true)) {
                return false
            }
        }
    }

    // Try trimming the flavor by just matching the prefix
    PLATFORM_CONFIGURATION_PREFIX.forEach { platformConfig ->
        if (name.startsWith(platformConfig, ignoreCase = true)) {
            return true
        }
    }
    // Try trimming the flavor by just matching the suffix
    PLATFORM_CONFIGURATION_SUFFIX.forEach { platformConfig ->
        if (name.endsWith(platformConfig, ignoreCase = true)) {
            return true
        }
    }
    return false
}

private val PLATFORM_CONFIGURATION_PREFIX = setOf(
    "kapt",
    "ksp",
    "layoutlib",
)

private val PLATFORM_CONFIGURATION_SUFFIX = setOf(
    "api",
    "coreLibraryDesugaring",
    "compileOnly",
    "implementation",
    "kapt",
    "ksp",
    "runtimeOnly",
    "androidTestUtil",
    "lintChecks",
    "lintRelease",
)

private val MULTIPLATFORM_API_IGNORE_CONFIGURATION = setOf(
    "androidTest",
    "androidUnitTest",
    "androidDebugUnitTest",
    "androidReleaseUnitTest",
    "androidInstrumentedTest",
)
