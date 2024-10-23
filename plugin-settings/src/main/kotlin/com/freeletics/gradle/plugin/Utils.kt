package com.freeletics.gradle.plugin

import nl.littlerobots.vcu.model.VersionCatalog
import nl.littlerobots.vcu.model.VersionDefinition
import org.gradle.api.initialization.Settings

internal fun Settings.stringProperty(name: String): String? {
    return providers.gradleProperty(name).orNull
}

internal fun Settings.booleanProperty(name: String, default: Boolean): Boolean {
    return providers.gradleProperty(name).map { it.toBoolean() }.orElse(default).get()
}

internal fun VersionDefinition.resolve(versionCatalog: VersionCatalog, settings: Settings): String {
    return when (this) {
        is VersionDefinition.Simple -> version
        is VersionDefinition.Reference -> {
            settings.stringProperty("${VERSION_OVERRIDE_PREFIX}$ref")
                ?: versionCatalog.versions.getValue(ref).resolve(versionCatalog, settings)
        }
        is VersionDefinition.Condition -> throw IllegalStateException("Conditional version are unsupported")
        VersionDefinition.Unspecified -> throw IllegalStateException("No version specified")
    }
}

internal const val VERSION_OVERRIDE_PREFIX = "fgp.version.override."
