package com.freeletics.gradle.plugin

import com.freeletics.gradle.setup.configurePaparazzi
import com.freeletics.gradle.util.androidMultiplatform
import org.gradle.api.Project

public abstract class FreeleticsMultiplatformAndroidExtension(private val project: Project) {
    public fun usePaparazzi() {
        project.configurePaparazzi()
    }

    public fun enableParcelize() {
        project.plugins.apply("org.jetbrains.kotlin.plugin.parcelize")
    }

    public fun enableAndroidResources() {
        project.androidMultiplatform {
            androidResources.enable = true
        }
    }

    @Suppress("UnstableApiUsage")
    public fun consumerProguardFiles(vararg files: String) {
        project.androidMultiplatform {
            optimization.consumerKeepRules.files(*files)
        }
    }
}
