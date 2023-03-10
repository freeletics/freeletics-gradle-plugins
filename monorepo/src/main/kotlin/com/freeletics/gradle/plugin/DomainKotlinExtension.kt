package com.freeletics.gradle.plugin

import org.gradle.api.Project

abstract class DomainKotlinExtension(project: Project) : FreeleticsJvmExtension(project) {

    var allowLegacyDependencies = false
}
