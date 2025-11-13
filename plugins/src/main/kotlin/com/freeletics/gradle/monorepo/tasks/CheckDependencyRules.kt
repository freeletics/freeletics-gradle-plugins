package com.freeletics.gradle.monorepo.tasks

import com.freeletics.gradle.monorepo.util.ProjectType
import com.freeletics.gradle.monorepo.util.toAppType
import com.freeletics.gradle.monorepo.util.toProjectType

internal fun checkDependencyRules(
    projectPath: String,
    configurationName: String,
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

    if (dependencyProjectType.suffix == "testing" && projectType.suffix != "testing") {
        if (!configurationName.contains("test", ignoreCase = true)) {
            errors += "$projectPath is not allowed to depend on testing module $dependencyPath " +
                "in configuration $configurationName"
        }
    }
    if (dependencyProjectType.suffix == "debug" && projectType.suffix != "debug" && projectType.suffix != "testing") {
        if (!configurationName.contains("debug", ignoreCase = true) &&
            !configurationName.contains("test", ignoreCase = true)
        ) {
            errors += "$projectPath is not allowed to depend on debug module $dependencyPath " +
                "in configuration $configurationName"
        }
    }

    return errors
}
