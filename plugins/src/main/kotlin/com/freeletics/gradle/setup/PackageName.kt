package com.freeletics.gradle.setup

import com.freeletics.gradle.util.stringProperty
import org.gradle.api.Project

internal fun Project.defaultPackageName(): String {
    val transformedPath = path.drop(1)
        .split(":")
        .mapIndexed { index, pathElement ->
            val parts = pathElement.split("-")
            if (index == 0) {
                // top level folders like core, domain, feature etc. handle dashes separately by
                // having them become separate package elements, also ignore the -freeletics suffix
                // to avoid package names like com.freeletics.domain.freeletics
                parts.filter { it != "freeletics" }.joinToString(separator = ".")
            } else {
                // for second and lower level folders dashes are ignored and the elements are
                // merged into one word
                parts.joinToString(separator = "")
            }
        }
        .joinToString(separator = ".")

    val prefix = stringProperty("fgp.android.namespacePrefix").get()
    return "$prefix.$transformedPath"
}
