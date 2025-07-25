plugins {
    alias(libs.plugins.fgp.jvm).apply(false)
    alias(libs.plugins.fgp.gradle).apply(false)
    alias(libs.plugins.fgp.publish).apply(false)
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
    }
}
