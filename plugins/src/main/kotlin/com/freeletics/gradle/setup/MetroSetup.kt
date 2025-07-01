package com.freeletics.gradle.setup

import com.freeletics.gradle.util.addApiDependency
import com.freeletics.gradle.util.addKspDependency
import com.freeletics.gradle.util.getDependency
import org.gradle.api.Project

internal fun Project.configureMetro() {
    plugins.apply("dev.zacsweers.metro")
}

internal fun Project.configureKhonshu() {
    configureProcessing()
    addApiDependency(getDependency("khonshu-codegen-runtime"))
    addKspDependency(getDependency("khonshu-codegen-compiler"))

    // TODO workaround for Gradle not being able to resolve this in the ksp config
    configurations.named("ksp").configure {
        it.exclude(mapOf("group" to "org.jetbrains.skiko", "module" to "skiko"))
    }
}
