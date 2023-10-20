package com.freeletics.gradle.monorepo.setup

import org.gradle.api.Project

internal fun Project.applyPlatformConstraints() {
    val platformDependency = dependencies.enforcedPlatform(rootProject)
    configurations.configureEach { config ->
        if (isPlatformConfigurationName(config.name)) {
            config.dependencies.add(platformDependency)
        }
    }
}

// adapted from https://github.com/ZacSweers/CatchUp/blob/347db46d82497990ff10c441ecc75c0c9eedf7c4/buildSrc/src/main/kotlin/dev/zacsweers/catchup/gradle/CatchUpPlugin.kt#L68-L80
private fun isPlatformConfigurationName(name: String): Boolean {
    // Kapt, ksp and compileOnly are special cases since they can be combined with others
    if (name.startsWith("kapt", ignoreCase = true) ||
        name.startsWith("ksp", ignoreCase = true) ||
        name == "compileOnly"
    ) {
        return true
    }
    // Try trimming the flavor by just matching the suffix
    PLATFORM_CONFIGURATIONS.forEach { platformConfig ->
        if (name.endsWith(platformConfig, ignoreCase = true)) {
            return true
        }
    }
    return false
}

private val PLATFORM_CONFIGURATIONS = setOf(
    "api",
    "coreLibraryDesugaring",
    "compileOnly",
    "implementation",
    "kapt",
    "ksp",
    "runtimeOnly",
)
