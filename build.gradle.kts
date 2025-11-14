plugins {
    alias(libs.plugins.fgp.multiplatform).apply(false)
    alias(libs.plugins.fgp.gradle).apply(false)
    alias(libs.plugins.kotlin).apply(false)
    alias(libs.plugins.kotlin.serialization).apply(false)
    alias(libs.plugins.dependency.analysis).apply(false)
    alias(libs.plugins.publish).apply(false)
    alias(libs.plugins.dokka).apply(false)

    alias(libs.plugins.fgp.root)
}

dependencyAnalysis {
    issues {
        project(":plugins") {
            // false positive around capabilities
            onUsedTransitiveDependencies {
                exclude("org.jetbrains.kotlin:kotlin-gradle-plugin-api")
            }
        }
        project(":scripts-circleci") {
            onUnusedDependencies {
                exclude(libs.clikt.asProvider())
            }
        }
        project(":scripts-github") {
            onUnusedDependencies {
                exclude(libs.clikt.asProvider())
            }
        }
        project(":scripts-google") {
            onUnusedDependencies {
                exclude(libs.clikt.asProvider())
            }
        }
        project(":scripts-s3") {
            onUnusedDependencies {
                exclude(libs.clikt.asProvider())
            }
        }
    }

    structure {
        bundle("slack") {
            includeGroup("com.slack.api")
        }
        bundle("aws") {
            includeGroup("aws.smithy.kotlin")
            includeGroup("aws.sdk.kotlin")
        }
        bundle("zxing") {
            includeGroup("com.google.zxing")
        }
        bundle("ktlint") {
            includeGroup("com.pinterest.ktlint")
            includeGroup("org.ec4j.core")
        }
    }
}

subprojects {
    plugins.withId("maven-publish") {
        extensions.configure(PublishingExtension::class) {
            publications.configureEach {
                (this as? MavenPublication)?.pom?.licenses {
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
