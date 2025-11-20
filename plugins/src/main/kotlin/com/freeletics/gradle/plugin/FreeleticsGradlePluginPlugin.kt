package com.freeletics.gradle.plugin

import com.freeletics.gradle.setup.configureStandaloneLint
import com.freeletics.gradle.util.compilerOptionsJvm
import com.freeletics.gradle.util.getDependency
import com.freeletics.gradle.util.java
import com.freeletics.gradle.util.javaTargetVersion
import com.freeletics.gradle.util.kotlin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.plugin.devel.tasks.ValidatePlugins
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

public abstract class FreeleticsGradlePluginPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.plugins.apply("java-gradle-plugin")
        target.plugins.apply("org.jetbrains.kotlin.jvm")
        target.plugins.apply(FreeleticsBasePlugin::class.java)

        target.java {
            sourceCompatibility = target.javaTargetVersion
            targetCompatibility = target.javaTargetVersion
        }

        target.tasks.withType(JavaCompile::class.java).configureEach {
            it.options.release.set(target.javaTargetVersion.majorVersion.toInt())
        }

        target.kotlin {
            compilerOptionsJvm {
                // https://docs.gradle.org/current/userguide/compatibility.html#kotlin
                apiVersion.set(KotlinVersion.KOTLIN_2_2)
                languageVersion.set(KotlinVersion.KOTLIN_2_2)

                // https://github.com/gradle/gradle/issues/24871
                freeCompilerArgs.addAll("-Xsam-conversions=class", "-Xlambdas=class")
            }
        }

        target.tasks.withType(ValidatePlugins::class.java).configureEach {
            it.enableStricterValidation.set(true)
        }

        target.configureStandaloneLint()
        target.dependencies.add("lintChecks", target.getDependency("androidx-lint-gradle"))
    }
}
