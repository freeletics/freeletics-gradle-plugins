import com.vanniktech.maven.publish.MavenPublishBaseExtension

plugins {
    alias(libs.plugins.kotlin) apply(false)
    alias(libs.plugins.dependency.analysis) apply(false)
    alias(libs.plugins.bestpractices) apply(false)
    alias(libs.plugins.gr8) apply(false)
    alias(libs.plugins.publish) apply(false)
    alias(libs.plugins.dokka) apply(false)

    alias(libs.plugins.fgp.root)
}

dependencyAnalysis {
    issues {
        all {
            onUsedTransitiveDependencies {
                exclude(":base")
            }
        }
    }
}

subprojects {
    plugins.withId("com.vanniktech.maven.publish") {
        extensions.configure(MavenPublishBaseExtension::class.java) {
            pom {
                licenses {
                    license {
                        name.set("The Apache Software License, Version 2.0")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                        distribution.set("repo")
                    }
                }
            }
        }
    }
}
