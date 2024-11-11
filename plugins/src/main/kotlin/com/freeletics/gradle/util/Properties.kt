package com.freeletics.gradle.util

import org.gradle.api.Project
import org.gradle.api.provider.Provider

internal fun Project.stringProperties(prefix: String): Provider<MutableMap<String, String>> {
    return providers.gradlePropertiesPrefixedBy(prefix)
}

internal fun Project.stringProperty(name: String): Provider<String> = providers.gradleProperty(name)

internal fun Project.booleanProperty(name: String, defaultValue: Boolean): Provider<Boolean> {
    return stringProperty(name).map { it.toBoolean() }.orElse(defaultValue)
}
