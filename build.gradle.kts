plugins {
    alias(libs.plugins.kotlin) apply (false)
    alias(libs.plugins.kotlin.serialization) apply (false)
    alias(libs.plugins.dependency.analysis) apply (false)
    alias(libs.plugins.publish) apply (false)
    alias(libs.plugins.dokka) apply (false)

    alias(libs.plugins.fgp.root)
}

dependencyAnalysis {
    issues {
        project(":scripts-circleci") {
            onUnusedDependencies {
                exclude(libs.clikt.asProvider())
            }
        }
        project(":scripts-google") {
            onUnusedDependencies {
                exclude(libs.clikt.asProvider())
            }
        }
    }

    structure {
        bundle("slack") {
            includeGroup("com.slack.api")
        }
    }
}
