package com.freeletics.gradle.setup

import com.freeletics.gradle.util.freeleticsExtension
import com.freeletics.gradle.util.kotlin
import com.freeletics.gradle.util.stringProperty
import com.vanniktech.maven.publish.MavenPublishBaseExtension
import org.gradle.api.Project
import org.gradle.api.credentials.PasswordCredentials
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPom
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.maven.tasks.AbstractPublishToMaven
import org.gradle.api.tasks.bundling.Zip
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.dsl.abi.AbiValidationExtension
import org.jetbrains.kotlin.gradle.dsl.abi.AbiValidationMultiplatformExtension
import org.jetbrains.kotlin.gradle.dsl.abi.ExperimentalAbiValidation
import org.jetbrains.kotlin.konan.target.HostManager

internal fun setupOssPublishing(target: Project) {
    target.plugins.apply("org.jetbrains.dokka")
    target.plugins.apply("com.vanniktech.maven.publish")

    target.freeleticsExtension.explicitApi()

    target.extensions.configure(MavenPublishBaseExtension::class.java) {
        it.publishToMavenCentral(automaticRelease = true)
        it.signAllPublications()
        it.pom { pom -> pom.configurePom(target, includeLicense = true) }

        val isSnapshot = target.stringProperty("VERSION_NAME").orNull?.endsWith("-SNAPSHOT") == true
        @Suppress("UnstableApiUsage")
        it.configureBasedOnAppliedPlugins(sourcesJar = true, javadocJar = !isSnapshot)
    }

    target.configureBinaryCompatibility()
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

internal fun setupXcFrameworkPublishing(project: Project, frameworkName: String) {
    val framework = "$frameworkName.xcframework"
    val frameworkRoot = project.layout.buildDirectory.dir("XCFrameworks/release")
    val assembleTask = "assemble${frameworkName}ReleaseXCFramework"

    val frameworkZip = project.tasks.register("${assembleTask}Zip", Zip::class.java) {
        it.dependsOn(assembleTask)
        it.onlyIf { HostManager.hostIsMac }

        it.from(frameworkRoot.map { root -> root.dir(framework) })
        it.into(framework)
        it.archiveBaseName.set(framework)
        it.destinationDirectory.set(frameworkRoot)
        it.isPreserveFileTimestamps = false
        it.isReproducibleFileOrder = true
    }

    val publicationName = "${frameworkName}XcFramework"
    project.extensions.configure(PublishingExtension::class.java) { publishing ->
        publishing.publications.create(publicationName, MavenPublication::class.java) {
            // the project.name will be replaced with the real artifact id by the publishing plugin
            it.artifactId = "${project.name}-xcframework"
            it.artifact(frameworkZip) { artifact ->
                artifact.extension = "zip"
            }
        }
    }

    project.tasks.withType(AbstractPublishToMaven::class.java).configureEach {
        if (it.name.contains(publicationName, ignoreCase = true)) {
            it.onlyIf { HostManager.hostIsMac }
        }
    }
}

@OptIn(ExperimentalAbiValidation::class)
private fun Project.configureBinaryCompatibility() {
    kotlin {
        when (this) {
            is KotlinJvmProjectExtension -> extensions.configure<AbiValidationExtension>(
                "abiValidation",
            ) { validation ->
                validation.enabled.set(true)
                // TODO remove manual task dependency https://youtrack.jetbrains.com/issue/KT-80614
                tasks.named("check").configure { task ->
                    task.dependsOn(validation.legacyDump.legacyCheckTaskProvider)
                }
            }
            is KotlinAndroidProjectExtension -> extensions.configure<AbiValidationExtension>(
                "abiValidation",
            ) { validation ->
                validation.enabled.set(true)
                // TODO remove manual task dependency https://youtrack.jetbrains.com/issue/KT-80614
                tasks.named("check").configure { task ->
                    task.dependsOn(validation.legacyDump.legacyCheckTaskProvider)
                }
            }
            is KotlinMultiplatformExtension -> extensions.configure<AbiValidationMultiplatformExtension>(
                "abiValidation",
            ) { validation ->
                validation.enabled.set(true)
                // TODO remove manual task dependency https://youtrack.jetbrains.com/issue/KT-80614
                tasks.named("check").configure { task ->
                    task.dependsOn(validation.legacyDump.legacyCheckTaskProvider)
                }
            }
            else -> throw IllegalStateException("Unsupported kotlin extension ${this::class}")
        }
    }
}
