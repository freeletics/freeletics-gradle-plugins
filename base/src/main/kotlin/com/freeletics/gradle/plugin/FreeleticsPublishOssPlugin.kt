package com.freeletics.gradle.plugin

import com.freeletics.gradle.setup.configurePom
import com.freeletics.gradle.util.freeleticsExtension
import com.freeletics.gradle.util.stringProperty
import com.vanniktech.maven.publish.MavenPublishBaseExtension
import com.vanniktech.maven.publish.SonatypeHost
import org.gradle.api.Plugin
import org.gradle.api.Project

public abstract class FreeleticsPublishOssPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.plugins.apply("org.jetbrains.dokka")
        target.plugins.apply("com.vanniktech.maven.publish")

        target.freeleticsExtension.explicitApi()
        target.configureMavenCentral()
        target.configurePom(includeLicense = false)
        target.configureDokka()
    }

    private fun Project.configureMavenCentral() {
        extensions.configure(MavenPublishBaseExtension::class.java) {
            it.publishToMavenCentral(SonatypeHost.DEFAULT, automaticRelease = true)
            it.signAllPublications()
        }
    }

    private fun Project.configureDokka() {
        // if the version is a snapshot version disable dokka tasks to speed up the build
        if (stringProperty("VERSION_NAME").orNull?.toString()?.endsWith("-SNAPSHOT") == true) {
            afterEvaluate {
                tasks.named("dokkaHtml").configure { task ->
                    task.enabled = false
                }

                it.plugins.withId("com.android.library") {
                    if (!plugins.hasPlugin("org.jetbrains.kotlin.multiplatform")) {
                        tasks.named("javaDocReleaseGeneration").configure { task ->
                            task.enabled = false
                        }
                    }
                }
            }
        }
    }
}
