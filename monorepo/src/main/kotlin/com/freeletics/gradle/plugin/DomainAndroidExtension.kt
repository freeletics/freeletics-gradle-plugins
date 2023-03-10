package com.freeletics.gradle.plugin

import org.gradle.api.Project

abstract class DomainAndroidExtension(project: Project) : FreeleticsAndroidExtension(project) {

    var allowLegacyDependencies = false
}
