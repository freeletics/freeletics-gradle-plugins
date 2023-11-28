package com.freeletics.gradle.plugin

import com.freeletics.gradle.setup.configurePom
import com.freeletics.gradle.util.stringProperty
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.credentials.PasswordCredentials
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.tasks.AbstractPublishToMaven

public abstract class FreeleticsPublishInternalPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.plugins.apply("com.vanniktech.maven.publish")

        target.addInternalRepo()
        target.disablePublishingIosArtifacts()
        target.configurePom(includeLicense = false)
    }

    private fun Project.addInternalRepo() {
        val internalUrl = stringProperty("fgp.internalArtifacts.url").orNull
        if (internalUrl != null) {
            extensions.configure(PublishingExtension::class.java) { publishing ->
                publishing.repositories { repositories ->
                    repositories.maven {
                        it.name = "internalArtifacts"
                        it.setUrl(internalUrl)
                        // allows using the internal url property to specify a local folder for testing
                        if (!internalUrl.startsWith("file:")) {
                            it.credentials(PasswordCredentials::class.java)
                        }
                    }
                }
            }
        }
    }

    private fun Project.disablePublishingIosArtifacts() {
        tasks.withType(AbstractPublishToMaven::class.java).configureEach {
            if (it.name.contains("ios", ignoreCase = true)) {
                it.onlyIf { false }
            }
        }
    }
}
