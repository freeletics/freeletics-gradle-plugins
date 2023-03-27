package com.freeletics.gradle.setup

import com.gradleup.gr8.Gr8Extension
import org.gradle.api.Project
import org.gradle.api.attributes.Usage
import org.gradle.api.attributes.Usage.JAVA_API

fun Project.setupGr8() {
    val shadeConfiguration = configurations.create("shade")

    // make all shaded dependencies available during compilation
    configurations.named("compileClasspath").configure {
        it.extendsFrom(shadeConfiguration)
    }
    // make all shaded dependencies available during tests
    configurations.named("testImplementation").configure {
        it.extendsFrom(shadeConfiguration)
    }

    // add compileOnly dependencies to r8's classpath to avoid class not found warnings
    // needs to be an extra configuration because we want to filter it and compileOnly can't be resolved
    val shadeClassPathConfiguration = configurations.create("shadeClassPath") {
        it.extendsFrom(configurations.getByName("compileOnly"))
        it.attributes { attributes ->
            attributes.attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage::class.java, JAVA_API))
        }
    }

    extensions.configure(Gr8Extension::class.java) { extension ->
        val shadowedJar = extension.create("gr8") {
            it.configuration(shadeConfiguration.name)
            it.classPathConfiguration(shadeClassPathConfiguration.name)
            it.stripGradleApi(true)
            if (rootProject.file(FILE_NAME).exists()) {
                it.proguardFile(rootProject.file(FILE_NAME))
            }
            if (project.file(FILE_NAME).exists()) {
                it.proguardFile(project.file(FILE_NAME))
            }
        }

        extension.replaceOutgoingJar(shadowedJar)
        extension.removeGradleApiFromApi()
    }
}

private const val FILE_NAME = "rules.pro"
