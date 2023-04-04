package com.freeletics.gradle.plugin

import org.gradle.api.Project

public abstract class DomainKotlinExtension(project: Project) : FreeleticsJvmExtension(project) {

    public var allowLegacyDependencies: Boolean = false
}
