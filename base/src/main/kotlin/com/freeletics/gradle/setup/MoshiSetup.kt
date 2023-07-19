package com.freeletics.gradle.setup

import dev.zacsweers.moshix.ir.gradle.MoshiPluginExtension
import org.gradle.api.Project

internal fun Project.configureMoshi(sealed: Boolean) {
    plugins.apply("dev.zacsweers.moshix")

    extensions.configure(MoshiPluginExtension::class.java) {
        it.enableSealed.set(sealed)
    }
}
