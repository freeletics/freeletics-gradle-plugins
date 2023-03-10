package com.freeletics.gradle.plugin

import org.gradle.api.Project

abstract class FeatureExtension(project: Project) : FreeleticsAndroidExtension(project) {

    var allowLegacyDependencies = false
}
