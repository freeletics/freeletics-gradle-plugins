package com.freeletics.gradle.plugin

import org.gradle.api.Project

abstract class NavExtension(project: Project) : FreeleticsAndroidExtension(project) {

    var allowLegacyDependencies = false
}
