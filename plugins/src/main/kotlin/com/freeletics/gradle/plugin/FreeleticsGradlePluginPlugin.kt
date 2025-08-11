package com.freeletics.gradle.plugin

import com.freeletics.gradle.util.compilerOptions
import com.freeletics.gradle.util.kotlin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

public abstract class FreeleticsGradlePluginPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.plugins.apply("java-gradle-plugin")
        target.plugins.apply(FreeleticsJvmPlugin::class.java)

        target.kotlin {
            compilerOptions {
                // https://docs.gradle.org/current/userguide/compatibility.html#kotlin
                apiVersion.set(KotlinVersion.KOTLIN_2_2)
                languageVersion.set(KotlinVersion.KOTLIN_2_2)

                // https://github.com/gradle/gradle/issues/24871
                freeCompilerArgs.addAll("-Xsam-conversions=class", "-Xlambdas=class")
            }
        }
    }
}
