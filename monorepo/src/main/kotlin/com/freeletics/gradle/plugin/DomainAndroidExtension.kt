package com.freeletics.gradle.plugin

import org.gradle.api.Project

public abstract class DomainAndroidExtension(project: Project) : FreeleticsAndroidExtension(project) {

    public var allowLegacyDependencies: Boolean = false
}
