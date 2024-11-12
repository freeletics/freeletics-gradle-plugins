plugins {
    alias(libs.plugins.kotlin) apply (false)
    alias(libs.plugins.dependency.analysis) apply (false)
    alias(libs.plugins.publish) apply (false)
    alias(libs.plugins.dokka) apply (false)

    alias(libs.plugins.fgp.root)
}

dependencyAnalysis {
    structure {
        bundle("slack") {
            includeGroup("com.slack.api")
        }
    }
}
