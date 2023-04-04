package com.freeletics.gradle.plugin

import com.freeletics.gradle.setup.configurePom
import com.vanniktech.maven.publish.MavenPublishBaseExtension
import com.vanniktech.maven.publish.SonatypeHost
import org.gradle.api.Plugin
import org.gradle.api.Project

public abstract class FreeleticsPublishOssPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.plugins.apply("org.jetbrains.dokka")
        target.plugins.apply("com.vanniktech.maven.publish")

        val extension = target.extensions.findByName("freeletics") as FreeleticsBaseExtension
        extension.explicitApi()
        
        target.extensions.configure(MavenPublishBaseExtension::class.java) {
            it.publishToMavenCentral(SonatypeHost.DEFAULT, automaticRelease = true)
            it.signAllPublications()
        }

        target.configurePom(includeLicense = true)

        // if the version is a snapshot version disable dokka tasks to speed up the build
        if (target.findProperty("VERSION_NAME")?.toString()?.endsWith("-SNAPSHOT") == true) {
            target.afterEvaluate {
                target.tasks.named("dokkaHtml").configure { task ->
                    task.enabled = false
                }

                it.plugins.withId("com.android.library") {
                    target.tasks.named("javaDocReleaseGeneration").configure { task ->
                        task.enabled = false
                    }
                }
            }
        }
    }
}
