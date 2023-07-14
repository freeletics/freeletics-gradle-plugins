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

// TODO remove after FGP 0.3.9+ was released
subprojects {
    plugins.withId("com.freeletics.gradle.common.gradle") {
        tasks.named("generatePluginVersion").configure {
            enabled = false
        }
    }
}
