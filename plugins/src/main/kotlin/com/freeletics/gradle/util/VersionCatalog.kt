package com.freeletics.gradle.util

import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.artifacts.ExternalModuleDependencyBundle
import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.provider.Provider
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

private val Project.libs: VersionCatalog
    get() = extensions.getByType(VersionCatalogsExtension::class.java).named("libs")

internal fun Project.getDependency(name: String): Provider<MinimalExternalModuleDependency> {
    return libs.findLibrary(name).orElseThrow { NoSuchElementException("Could not find library $name") }
}

internal fun Project.getDependencyOrNull(name: String): Provider<MinimalExternalModuleDependency>? {
    return libs.findLibrary(name).orElseGet { null }
}

internal fun Project.getVersion(name: String): String {
    return getVersionOrNull(name) ?: throw NoSuchElementException("Could not find version $name")
}

internal fun Project.getVersionOrNull(name: String): String? {
    return libs.findVersion(name).orElseGet { null }?.requiredVersion
}

internal fun Project.getBundleOrNull(name: String): Provider<ExternalModuleDependencyBundle>? {
    return libs.findBundle(name).orElseGet { null }
}

internal fun DependencyHandler.addMaybe(name: String, dependency: Any?) {
    if (dependency != null) {
        add(name, dependency)
    }
}

internal val Project.javaTargetVersion: JavaVersion
    get() = JavaVersion.toVersion(getVersion("java-target"))

internal val Project.jvmTarget: JvmTarget
    get() = JvmTarget.fromTarget(getVersion("java-target"))

internal val Project.javaToolchainVersion: JavaLanguageVersion
    get() = JavaLanguageVersion.of(getVersion("java-toolchain"))
