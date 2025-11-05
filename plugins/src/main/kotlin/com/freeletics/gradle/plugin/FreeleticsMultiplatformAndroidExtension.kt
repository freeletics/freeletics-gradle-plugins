package com.freeletics.gradle.plugin

import com.freeletics.gradle.setup.basicArgument
import com.freeletics.gradle.setup.configurePaparazzi
import com.freeletics.gradle.setup.configureProcessing
import com.freeletics.gradle.util.addApiDependency
import com.freeletics.gradle.util.addKspDependency
import com.freeletics.gradle.util.androidMultiplatform
import com.freeletics.gradle.util.getDependency
import java.io.File
import org.gradle.api.Project

public abstract class FreeleticsMultiplatformAndroidExtension(private val project: Project) {
    public fun useRoom(schemaLocation: String? = null) {
        val processingArguments = buildList {
            add(basicArgument("room.generateKotlin", "true"))
            schemaLocation?.let {
                add(RoomSchemaArgProvider(schemaDir = File(project.projectDir, schemaLocation)))
            }
        }

        project.configureProcessing(processingArguments)
        project.addApiDependency(project.getDependency("androidx-room-runtime"))
        project.addKspDependency(project.getDependency("androidx-room-compiler"))
    }

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
