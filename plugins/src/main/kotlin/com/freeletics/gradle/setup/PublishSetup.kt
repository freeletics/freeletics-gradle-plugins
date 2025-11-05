package com.freeletics.gradle.setup

import com.freeletics.gradle.util.freeleticsExtension
import com.freeletics.gradle.util.stringProperty
import com.github.javaparser.printer.lexicalpreservation.LexicalPreservingPrinter.setup
import com.gradle.scan.agent.serialization.scan.serializer.kryo.it
import com.vanniktech.maven.publish.MavenPublishBaseExtension
import org.gradle.api.Project
import org.gradle.api.credentials.PasswordCredentials
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPom

internal fun setupOssPublishing(target: Project) {
    target.plugins.apply("org.jetbrains.dokka")
    target.plugins.apply("com.vanniktech.maven.publish")

    target.freeleticsExtension.explicitApi()

    target.extensions.configure(MavenPublishBaseExtension::class.java) {
        it.publishToMavenCentral(automaticRelease = true)
        it.signAllPublications()
        it.pom { pom -> pom.configurePom(target, includeLicense = false) }

        val isSnapshot = target.stringProperty("VERSION_NAME").orNull?.endsWith("-SNAPSHOT") == true
        @Suppress("UnstableApiUsage")
        it.configureBasedOnAppliedPlugins(sourcesJar = true, javadocJar = !isSnapshot)
    }
}

internal fun setupInternalPublishing(target: Project) {
    target.plugins.apply("com.vanniktech.maven.publish")

    target.extensions.configure(MavenPublishBaseExtension::class.java) {
        it.pom { pom ->
            pom.configurePom(target, includeLicense = false)
        }
    }

    val internalUrl = target.stringProperty("fgp.internalArtifacts.url").orNull
    if (internalUrl != null) {
        target.extensions.configure(PublishingExtension::class.java) { publishing ->
            publishing.repositories { repositories ->
                repositories.maven {
                    it.name = "internalArtifacts"
                    it.setUrl(internalUrl)
                    it.credentials(PasswordCredentials::class.java)
                }
            }
        }
    }

    val localPath = target.stringProperty("fgp.publishToFolder").orNull
    if (localPath != null) {
        target.extensions.configure(PublishingExtension::class.java) { publishing ->
            publishing.repositories { repositories ->
                repositories.maven {
                    it.name = "local"
                    it.setUrl(localPath)
                }
            }
        }
    }
}

internal fun MavenPom.configurePom(project: Project, includeLicense: Boolean) {
    val repoName = project.stringProperty("POM_REPO_NAME").get()

    url.set("https://github.com/freeletics/$repoName/")

    scm { scm ->
        scm.url.set("https://github.com/freeletics/$repoName/")
        scm.connection.set("scm:git:git://github.com/freeletics/$repoName.git")
        scm.developerConnection.set("scm:git:ssh://git@github.com/freeletics/$repoName.git")
    }

    developers { developers ->
        developers.developer { dev ->
            dev.id.set("freeletics")
            dev.name.set("Freeletics")
            dev.url.set("https://freeletics.engineering")
        }
    }

    if (includeLicense) {
        licenses { licenses ->
            licenses.license { license ->
                license.name.set("The Apache Software License, Version 2.0")
                license.url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                license.distribution.set("repo")
            }
        }
    }
}
