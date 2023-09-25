package com.freeletics.gradle.monorepo.util

internal fun String.capitalize() = replaceFirstChar { it.titlecase() }
