package com.freeletics.gradle.setup

import com.gradleup.gr8.Gr8Extension
import org.gradle.api.Project

fun Project.setupGr8() {
    val shadeConfiguration = configurations.create("shade")

    configurations.named("compileOnly").configure {
        it.extendsFrom(shadeConfiguration)
    }
    configurations.named("testImplementation").configure {
        it.extendsFrom(shadeConfiguration)
    }

    extensions.configure(Gr8Extension::class.java) { extension ->
        val shadowedJar = extension.create("gr8") {
            it.configuration(shadeConfiguration.name)
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
