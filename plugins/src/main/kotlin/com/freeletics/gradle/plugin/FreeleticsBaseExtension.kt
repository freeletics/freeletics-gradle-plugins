package com.freeletics.gradle.plugin

import com.freeletics.gradle.setup.basicArgument
import com.freeletics.gradle.setup.configureProcessing
import com.freeletics.gradle.setup.setupCompose
import com.freeletics.gradle.setup.setupInternalPublishing
import com.freeletics.gradle.setup.setupOssPublishing
import com.freeletics.gradle.setup.setupSqlDelight
import com.freeletics.gradle.util.addApiDependency
import com.freeletics.gradle.util.addKspDependency
import com.freeletics.gradle.util.compilerOptionsCommon
import com.freeletics.gradle.util.getDependency
import com.freeletics.gradle.util.kotlin
import java.io.File
import org.gradle.api.Project
import org.gradle.api.artifacts.ProjectDependency
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.process.CommandLineArgumentProvider

public abstract class FreeleticsBaseExtension(private val project: Project) : ExtensionAware {
    public fun explicitApi() {
        project.kotlin {
            explicitApi()
        }
    }

    public fun optIn(vararg classes: String) {
        project.kotlin {
            compilerOptionsCommon {
                optIn.addAll(*classes)
            }
        }
    }

    public fun useCompose() {
        project.setupCompose()
    }

    public fun useSerialization() {
        project.plugins.apply("org.jetbrains.kotlin.plugin.serialization")

        project.addApiDependency(project.getDependency("kotlinx-serialization"))
    }

    public fun useMetro() {
        project.plugins.apply("dev.zacsweers.metro")
    }

    public fun useKhonshu() {
        useMetro()
        project.configureProcessing()
        project.addApiDependency(project.getDependency("khonshu-codegen-runtime"))
        project.addKspDependency(project.getDependency("khonshu-codegen-compiler"))
        // TODO workaround for Gradle not being able to resolve this in the ksp config
        project.configurations.named("ksp").configure {
            it.exclude(mapOf("group" to "org.jetbrains.skiko", "module" to "skiko"))
        }
    }

    public fun usePoko() {
        project.plugins.apply("dev.drewhamilton.poko")
    }

    public fun useKopy() {
        project.plugins.apply("com.javiersc.kotlin.kopy")
        project.plugins.apply("org.jetbrains.kotlin.plugin.atomicfu")
    }

    public fun useSqlDelight(
        name: String = "Database",
        dependency: ProjectDependency? = null,
    ) {
        project.setupSqlDelight(name, dependency)
    }

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

    public fun useBurst() {
        project.plugins.apply("app.cash.burst")
    }

    public fun enableOssPublishing() {
        setupOssPublishing(project)
    }

    public fun enableInternalPublishing() {
        setupInternalPublishing(project)
    }

    private class RoomSchemaArgProvider(
        @get:InputDirectory
        @get:PathSensitive(PathSensitivity.RELATIVE)
        val schemaDir: File,
    ) : CommandLineArgumentProvider {
        override fun asArguments(): Iterable<String> {
            return listOf("room.schemaLocation=${schemaDir.path}")
        }
    }
}
