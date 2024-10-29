package com.freeletics.gradle.plugin

import org.gradle.api.initialization.Settings

internal fun Settings.stringProperty(name: String): String? {
    return providers.gradleProperty(name).orNull
}

internal fun Settings.booleanProperty(name: String, default: Boolean): Boolean {
    return providers.gradleProperty(name).map { it.toBoolean() }.orElse(default).get()
}
