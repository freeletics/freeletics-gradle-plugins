package com.freeletics.gradle.setup

import com.freeletics.gradle.util.booleanProperty
import com.freeletics.gradle.util.getDependency
import dev.zacsweers.moshix.ir.gradle.MoshiPluginExtension
import org.gradle.api.Project

internal fun Project.configureMoshi(sealed: Boolean) {
    val moshiKsp = booleanProperty("fgp.kotlin.moshiKsp", false)

    if (moshiKsp.get()) {
        configureProcessing(useKsp = true)

        dependencies.apply {
            add("implementation", getDependency("moshi"))
            add("ksp", getDependency("moshi-codegen"))
            if (sealed) {
                add("implementation", getDependency("moshix-sealed"))
                add("ksp", getDependency("moshix-sealed-codegen"))
            }
        }
    } else {
        plugins.apply("dev.zacsweers.moshix")

        extensions.configure(MoshiPluginExtension::class.java) {
            it.enableSealed.set(sealed)
        }
    }
}
