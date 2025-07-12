package com.freeletics.gradle.util

import com.freeletics.gradle.monorepo.util.capitalize
import org.gradle.api.Project
import org.gradle.api.artifacts.ExternalModuleDependencyBundle
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
    addApiDependency(dependency as Any?, limitToTargets)
}

internal fun Project.addApiDependency(
    dependency: Any?,
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

internal fun Project.addImplementationDependency(
    dependency: Provider<MinimalExternalModuleDependency>?,
    limitToTargets: Set<KotlinPlatformType>? = null,
) {
    addImplementationDependency(dependency as Any?, limitToTargets)
}

internal fun Project.addImplementationDependency(
    dependency: Provider<ExternalModuleDependencyBundle>?,
    limitToTargets: Set<KotlinPlatformType>? = null,
) {
    addImplementationDependency(dependency as Any?, limitToTargets)
}

internal fun Project.addImplementationDependency(
    dependency: String,
    limitToTargets: Set<KotlinPlatformType>? = null,
) {
    addImplementationDependency(dependency as Any?, limitToTargets)
}

private fun Project.addImplementationDependency(
    dependency: Any?,
    limitToTargets: Set<KotlinPlatformType>? = null,
) {
    addDependency(
        dependency = dependency,
        notMultiplatformConfiguration = "implementation",
        commonConfiguration = "commonMainImplementation",
        targetConfiguration = KotlinTarget::implementationConfigName,
        limitToTargets = limitToTargets,
    )
}

internal fun Project.addCompileOnlyDependency(
    dependency: Provider<ExternalModuleDependencyBundle>?,
    limitToTargets: Set<KotlinPlatformType>? = null,
) {
    addCompileOnlyDependency(dependency as Any?, limitToTargets)
}

private fun Project.addCompileOnlyDependency(
    dependency: Any?,
    limitToTargets: Set<KotlinPlatformType>? = null,
) {
    addDependency(
        dependency = dependency,
        notMultiplatformConfiguration = "compileOnly",
        commonConfiguration = "commonMainCompileOnly",
        targetConfiguration = KotlinTarget::compileOnlyConfigName,
        limitToTargets = limitToTargets,
    )
}

internal fun Project.addTestImplementationDependency(
    dependency: Provider<ExternalModuleDependencyBundle>?,
    limitToTargets: Set<KotlinPlatformType>? = null,
) {
    addTestImplementationDependency(dependency as Any?, limitToTargets)
}

internal fun Project.addTestImplementationDependency(
    dependency: String,
    limitToTargets: Set<KotlinPlatformType>? = null,
) {
    addTestImplementationDependency(dependency as Any?, limitToTargets)
}

private fun Project.addTestImplementationDependency(
    dependency: Any?,
    limitToTargets: Set<KotlinPlatformType>? = null,
) {
    addDependency(
        dependency = dependency,
        notMultiplatformConfiguration = "testImplementation",
        commonConfiguration = "commonTestImplementation",
        targetConfiguration = KotlinTarget::testImplementationConfigName,
        limitToTargets = limitToTargets,
    )
}

internal fun Project.addTestCompileOnlyDependency(
    dependency: Provider<ExternalModuleDependencyBundle>?,
    limitToTargets: Set<KotlinPlatformType>? = null,
) {
    addTestCompileOnlyDependency(dependency as Any?, limitToTargets)
}

private fun Project.addTestCompileOnlyDependency(
    dependency: Any?,
    limitToTargets: Set<KotlinPlatformType>? = null,
) {
    addDependency(
        dependency = dependency,
        notMultiplatformConfiguration = "testCompileOnly",
        commonConfiguration = "commonTestCompileOnly",
        targetConfiguration = KotlinTarget::testCompileOnlyConfigName,
        limitToTargets = limitToTargets,
    )
}

internal fun Project.addTestRuntimeOnlyDependency(
    dependency: Provider<ExternalModuleDependencyBundle>?,
    limitToTargets: Set<KotlinPlatformType>? = null,
) {
    addTestRuntimeOnlyDependency(dependency as Any?, limitToTargets)
}

private fun Project.addTestRuntimeOnlyDependency(
    dependency: Any?,
    limitToTargets: Set<KotlinPlatformType>? = null,
) {
    addDependency(
        dependency = dependency,
        notMultiplatformConfiguration = "testRuntimeOnly",
        commonConfiguration = "commonTestRuntimeOnly",
        targetConfiguration = KotlinTarget::testRuntimeOnlyConfigName,
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
        commonConfiguration = "kspCommonMainMetadata",
        targetConfiguration = KotlinTarget::kspConfigName,
        limitToTargets = limitToTargets,
    )
}

private fun Project.addDependency(
    dependency: Any?,
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

internal fun KotlinTarget.implementationConfigName(): String {
    return when (targetName) {
        "main" -> "implementation"
        else -> "${targetName}MainImplementation"
    }
}

internal fun KotlinTarget.compileOnlyConfigName(): String {
    return when (targetName) {
        "main" -> "compileOnly"
        else -> "${targetName}MainCompileOnly"
    }
}

internal fun KotlinTarget.testImplementationConfigName(): String {
    return when (targetName) {
        "main" -> "testImplementation"
        else -> "${targetName}TestImplementation"
    }
}

internal fun KotlinTarget.testCompileOnlyConfigName(): String {
    return when (targetName) {
        "main" -> "testCompileOnly"
        else -> "${targetName}TestCompileOnly"
    }
}

internal fun KotlinTarget.testRuntimeOnlyConfigName(): String {
    return when (targetName) {
        "main" -> "testRuntimeOnly"
        else -> "${targetName}TestCompileOnly"
    }
}

internal fun KotlinTarget.kspConfigName(): String {
    return when (targetName) {
        "main" -> "ksp"
        else -> "ksp${targetName.capitalize()}"
    }
}
