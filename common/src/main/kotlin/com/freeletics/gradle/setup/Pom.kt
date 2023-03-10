package com.freeletics.gradle.setup

import com.freeletics.gradle.util.stringProperty
import com.vanniktech.maven.publish.MavenPublishBaseExtension
import org.gradle.api.Project

@Suppress("UnstableApiUsage")
internal fun Project.configurePom(includeLicense: Boolean) {
    val repoName = stringProperty("POM_REPO_NAME").get()
    extensions.configure(MavenPublishBaseExtension::class.java) {
        it.pom { pom ->
            pom.url.set("https://github.com/freeletics/$repoName/")

            pom.scm { scm ->
                scm.url.set("https://github.com/freeletics/$repoName/")
                scm.connection.set("scm:git:git://github.com/freeletics/$repoName.git")
                scm.developerConnection.set("scm:git:ssh://git@github.com/freeletics/$repoName.git")
            }

            pom.developers { developers ->
                developers.developer { dev ->
                    dev.id.set("freeletics")
                    dev.name.set("Freeletics")
                    dev.url.set("https://freeletics.engineering")
                }
            }

            if (includeLicense) {
                pom.licenses { licenses ->
                    licenses.license { license ->
                        license.name.set("The Apache Software License, Version 2.0")
                        license.url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                        license.distribution.set("repo")
                    }
                }
            }
        }
    }
}
