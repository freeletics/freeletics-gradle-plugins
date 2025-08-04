package com.freeletics.gradle.plugin

import com.android.build.api.dsl.LibraryExtension
import com.freeletics.gradle.setup.basicArgument
import com.freeletics.gradle.setup.configurePaparazzi
import com.freeletics.gradle.setup.configureProcessing
import com.freeletics.gradle.util.addApiDependency
import com.freeletics.gradle.util.addKspDependency
import com.freeletics.gradle.util.android
import com.freeletics.gradle.util.enable
import com.freeletics.gradle.util.getDependency
import java.io.File
import org.gradle.api.Project
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.process.CommandLineArgumentProvider

public abstract class FreeleticsAndroidExtension(private val project: Project) {
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

    public fun enableViewBinding() {
        project.android {
            buildFeatures.viewBinding = true
        }
    }

    public fun enableAndroidResources() {
        project.android {
            androidResources.enable = true
        }
    }

    public fun enableBuildConfig() {
        project.android {
            buildFeatures.buildConfig = true
        }
    }

    public fun enableResValues() {
        project.android {
            buildFeatures.resValues = true
        }
    }

    public fun buildConfigField(type: String, name: String, value: String) {
        project.android {
            defaultConfig.buildConfigField(type, name, value)
        }
    }

    public fun buildConfigField(type: String, name: String, debugValue: String, releaseValue: String) {
        project.android {
            buildTypes.getByName("debug").buildConfigField(type, name, debugValue)
            buildTypes.getByName("release").buildConfigField(type, name, releaseValue)
        }
    }

    public fun resValue(type: String, name: String, value: String) {
        project.android {
            defaultConfig.resValue(type, name, value)
        }
    }

    public fun resValue(type: String, name: String, debugValue: String, releaseValue: String) {
        project.android {
            buildTypes.getByName("debug").resValue(type, name, debugValue)
            buildTypes.getByName("release").resValue(type, name, releaseValue)
        }
    }

    public fun consumerProguardFiles(vararg files: String) {
        project.android {
            (this as LibraryExtension).defaultConfig.consumerProguardFiles(*files)
        }
    }
}

internal class RoomSchemaArgProvider(
    @get:InputDirectory
    @get:PathSensitive(PathSensitivity.RELATIVE)
    val schemaDir: File,
) : CommandLineArgumentProvider {
    override fun asArguments(): Iterable<String> {
        return listOf("room.schemaLocation=${schemaDir.path}")
    }
}
