package com.freeletics.gradle.setup

import com.freeletics.gradle.util.booleanProperty
import com.freeletics.gradle.util.jvmTarget
import com.google.devtools.ksp.gradle.KspExtension
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.internal.KaptGenerateStubsTask
import org.jetbrains.kotlin.gradle.plugin.KaptExtension

fun Project.configureProcessing(vararg arguments: Pair<String, String>): String {
    val useKsp = booleanProperty("fgp.kotlin.ksp", true)
    if (useKsp.get()) {
        plugins.apply("com.google.devtools.ksp")

        if (arguments.isNotEmpty()) {
            extensions.configure(KspExtension::class.java) { extension ->
                arguments.forEach { (key, value) ->
                    extension.arg(key, value)
                }
            }
        }

        return "ksp"
    } else {
        plugins.apply("org.jetbrains.kotlin.kapt")

        project.tasks.withType(KaptGenerateStubsTask::class.java).configureEach {
            it.compilerOptions {
                jvmTarget.set(project.jvmTarget)
            }
        }

        if (arguments.isNotEmpty()) {
            extensions.configure(KaptExtension::class.java) { extension ->
                extension.mapDiagnosticLocations = true
                extension.correctErrorTypes = true

                extension.arguments {
                    arguments.forEach { (key, value) ->
                        arg(key, value)
                    }
                }
            }
        }

        return "kapt"
    }
}
