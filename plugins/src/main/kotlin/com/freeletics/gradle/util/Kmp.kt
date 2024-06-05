package com.freeletics.gradle.util

import com.freeletics.gradle.monorepo.util.capitalize
import org.gradle.api.Project
import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.provider.Provider
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.dsl.kotlinExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType
import org.jetbrains.kotlin.gradle.plugin.KotlinTarget

internal fun Project.addApiDependency(
    dependency: Provider<MinimalExternalModuleDependency>?,
    limitToTargets: Set<KotlinPlatformType>? = null,
) {
    addDependency(
        dependency = dependency,
        notMultiplatformConfiguration = "api",
        commonConfiguration = "commonMainApi",
        targetConfiguration = KotlinTarget::apiConfigName,
        limitToTargets = limitToTargets,
    )
}

internal fun Project.addKspDependency(
    dependency: Provider<MinimalExternalModuleDependency>?,
    limitToTargets: Set<KotlinPlatformType>? = null,
) {
    addDependency(
        dependency = dependency,
        notMultiplatformConfiguration = "ksp",
        commonConfiguration = "ksp",
        targetConfiguration = KotlinTarget::kspConfigName,
        limitToTargets = limitToTargets,
    )
}

private fun Project.addDependency(
    dependency: Provider<MinimalExternalModuleDependency>?,
    notMultiplatformConfiguration: String,
    commonConfiguration: String,
    targetConfiguration: KotlinTarget.() -> String,
    limitToTargets: Set<KotlinPlatformType>?,
) {
    if (dependency == null) {
        return
    }

    val extension = kotlinExtension
    if (extension is KotlinMultiplatformExtension) {
        if (limitToTargets == null) {
            dependencies.add(commonConfiguration, dependency)
        } else {
            extension.targets.configureEach {
                if (it.platformType in limitToTargets) {
                    dependencies.add(it.targetConfiguration(), dependency)
                }
            }
        }
    } else {
        dependencies.add(notMultiplatformConfiguration, dependency)
    }
}

internal fun KotlinTarget.apiConfigName(): String {
    return when (targetName) {
        "main" -> "api"
        else -> "${targetName}MainApi"
    }
}

internal fun KotlinTarget.kspConfigName(): String {
    return when (targetName) {
        "main" -> "ksp"
        else -> "ksp${targetName.capitalize()}"
    }
}
