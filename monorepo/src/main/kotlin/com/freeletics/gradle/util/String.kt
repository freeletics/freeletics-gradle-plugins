package com.freeletics.gradle.util

internal fun String.capitalize() = replaceFirstChar { it.titlecase() }
