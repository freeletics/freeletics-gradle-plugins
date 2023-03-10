package com.freeletics.gradle.plugin

import com.freeletics.gradle.setup.configurePom
import com.vanniktech.maven.publish.MavenPublishBaseExtension
import com.vanniktech.maven.publish.SonatypeHost
import org.gradle.api.Plugin
import org.gradle.api.Project

abstract class FreeleticsPublishOssPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.plugins.apply("com.vanniktech.maven.publish")

        target.extensions.configure(MavenPublishBaseExtension::class.java) {
            it.publishToMavenCentral(SonatypeHost.DEFAULT, automaticRelease = true)
            it.signAllPublications()
        }

        target.configurePom(includeLicense = true)
    }
}
