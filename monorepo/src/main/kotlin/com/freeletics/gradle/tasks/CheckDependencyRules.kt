package com.freeletics.gradle.tasks

import com.freeletics.gradle.util.ProjectType
import com.freeletics.gradle.util.toAppType
import com.freeletics.gradle.util.toProjectType

internal fun checkDependencyRules(
    projectPath: String,
    dependencyPath: String,
    allowedProjectTypes: List<ProjectType>,
    allowedDependencyProjectTypes: List<ProjectType>,
): List<String> {
    val errors = mutableListOf<String>()

    val projectType = projectPath.toProjectType()
    val projectAppType = projectPath.toAppType()

    if (!allowedProjectTypes.contains(projectType)) {
        errors += "$projectPath is a ${projectType.fullName} project but the current plugin only allows " +
            allowedProjectTypes.joinToString(separator = ", ") { it.fullName }
    }

    val dependencyProjectType = dependencyPath.toProjectType()
    val dependencyAppType = dependencyPath.toAppType()

    if (!allowedDependencyProjectTypes.contains(dependencyProjectType)) {
        errors += "$projectPath is not allowed to depend on ${dependencyProjectType.fullName} module $dependencyPath"
    }

    if (dependencyAppType != null && dependencyAppType != projectAppType) {
        errors += "$projectPath is not allowed to depend on ${dependencyAppType.name} module $dependencyPath"
    }

    return errors
}
