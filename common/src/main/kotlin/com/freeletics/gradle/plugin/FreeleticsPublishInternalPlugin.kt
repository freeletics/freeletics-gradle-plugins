package com.freeletics.gradle.plugin

import com.freeletics.gradle.setup.configurePom
import com.freeletics.gradle.util.stringProperty
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.credentials.AwsCredentials
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.tasks.AbstractPublishToMaven

public abstract class FreeleticsPublishInternalPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.plugins.apply("com.vanniktech.maven.publish")

        target.addInternalS3Repo()
        target.disablePublishingIosArtifacts()
        target.configurePom(includeLicense = false)
    }

    private fun Project.addInternalS3Repo() {
        val internalUrl = stringProperty("freeleticsAndroidArtifactsUrl").orNull
        if (internalUrl != null) {
            extensions.configure(PublishingExtension::class.java) { publishing ->
                publishing.repositories { repositories ->
                    repositories.maven { repo ->
                        repo.name = "freeleticsAndroidArtifacts"
                        repo.setUrl(internalUrl)
                        repo.credentials(AwsCredentials::class.java)
                    }
                }
            }
        }
    }

    private fun Project.disablePublishingIosArtifacts() {
        extensions.configure(PublishingExtension::class.java) { publishing ->
            publishing.publications.configureEach { publication ->
                if (publication.name.contains("ios", ignoreCase = true)) {
                    tasks.withType(AbstractPublishToMaven::class.java).configureEach {
                        if (it.publication == publication) {
                            it.onlyIf { false }
                        }
                    }
                }
            }
        }
    }
}
