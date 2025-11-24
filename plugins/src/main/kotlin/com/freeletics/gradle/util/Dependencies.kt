package com.freeletics.gradle.util

import com.freeletics.gradle.monorepo.util.capitalize
import org.gradle.api.Project
import org.gradle.api.artifacts.ExternalModuleDependencyBundle
import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.provider.Provider
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.dsl.kotlinExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinTarget

internal fun Project.addApiDependency(
    dependency: Provider<MinimalExternalModuleDependency>?,
    limitToTargets: Set<String>? = null,
    excludeTargets: Set<String>? = null,
) {
    addApiDependency(dependency as Any?, limitToTargets)
}

@JvmName("addApiDependencyBundle")
internal fun Project.addApiDependency(
    dependency: Provider<ExternalModuleDependencyBundle>?,
    limitToTargets: Set<String>? = null,
    excludeTargets: Set<String>? = null,
) {
    addApiDependency(dependency as Any?, limitToTargets)
}

private fun Project.addApiDependency(
    dependency: Any?,
    limitToTargets: Set<String>? = null,
    excludeTargets: Set<String>? = null,
) {
    addDependency(
        dependency = dependency,
        notMultiplatformConfiguration = "api",
        commonConfiguration = "commonMainApi",
        targetConfiguration = KotlinTarget::apiConfigName,
        limitToTargets = limitToTargets,
        excludeTargets = excludeTargets,
    )
}

internal fun Project.addImplementationDependency(
    dependency: Provider<MinimalExternalModuleDependency>?,
    limitToTargets: Set<String>? = null,
    excludeTargets: Set<String>? = null,
) {
    addImplementationDependency(dependency as Any?, limitToTargets)
}

@JvmName("addImplementationDependencyBundle")
internal fun Project.addImplementationDependency(
    dependency: Provider<ExternalModuleDependencyBundle>?,
    limitToTargets: Set<String>? = null,
    excludeTargets: Set<String>? = null,
) {
    addImplementationDependency(dependency as Any?, limitToTargets)
}

@JvmName("addImplementationDependencyString")
internal fun Project.addImplementationDependency(
    dependency: String,
    limitToTargets: Set<String>? = null,
    excludeTargets: Set<String>? = null,
) {
    addImplementationDependency(dependency as Any?, limitToTargets)
}

private fun Project.addImplementationDependency(
    dependency: Any?,
    limitToTargets: Set<String>? = null,
    excludeTargets: Set<String>? = null,
) {
    addDependency(
        dependency = dependency,
        notMultiplatformConfiguration = "implementation",
        commonConfiguration = "commonMainImplementation",
        targetConfiguration = KotlinTarget::implementationConfigName,
        limitToTargets = limitToTargets,
        excludeTargets = excludeTargets,
    )
}

internal fun Project.addCompileOnlyDependency(
    dependency: Provider<MinimalExternalModuleDependency>?,
    limitToTargets: Set<String>? = null,
    excludeTargets: Set<String>? = null,
) {
    addCompileOnlyDependency(dependency as Any?, limitToTargets)
}

@JvmName("addCompileOnlyDependencyBundle")
internal fun Project.addCompileOnlyDependency(
    dependency: Provider<ExternalModuleDependencyBundle>?,
    limitToTargets: Set<String>? = null,
    excludeTargets: Set<String>? = null,
) {
    addCompileOnlyDependency(dependency as Any?, limitToTargets)
}

private fun Project.addCompileOnlyDependency(
    dependency: Any?,
    limitToTargets: Set<String>? = null,
    excludeTargets: Set<String>? = null,
) {
    addDependency(
        dependency = dependency,
        notMultiplatformConfiguration = "compileOnly",
        commonConfiguration = "commonMainCompileOnly",
        targetConfiguration = KotlinTarget::compileOnlyConfigName,
        limitToTargets = limitToTargets,
        excludeTargets = excludeTargets,
    )
}

internal fun Project.addTestImplementationDependency(
    dependency: Provider<MinimalExternalModuleDependency>?,
    limitToTargets: Set<String>? = null,
    excludeTargets: Set<String>? = null,
) {
    addTestImplementationDependency(dependency as Any?, limitToTargets)
}

@JvmName("addTestImplementationDependencyBundle")
internal fun Project.addTestImplementationDependency(
    dependency: Provider<ExternalModuleDependencyBundle>?,
    limitToTargets: Set<String>? = null,
    excludeTargets: Set<String>? = null,
) {
    addTestImplementationDependency(dependency as Any?, limitToTargets)
}

private fun Project.addTestImplementationDependency(
    dependency: Any?,
    limitToTargets: Set<String>? = null,
    excludeTargets: Set<String>? = null,
) {
    addDependency(
        dependency = dependency,
        notMultiplatformConfiguration = "testImplementation",
        commonConfiguration = "commonTestImplementation",
        targetConfiguration = KotlinTarget::testImplementationConfigName,
        limitToTargets = limitToTargets,
        excludeTargets = excludeTargets,
    )
}

internal fun Project.addTestCompileOnlyDependency(
    dependency: Provider<MinimalExternalModuleDependency>?,
    limitToTargets: Set<String>? = null,
    excludeTargets: Set<String>? = null,
) {
    addTestCompileOnlyDependency(dependency as Any?, limitToTargets)
}

@JvmName("addTestCompileOnlyDependencyBundle")
internal fun Project.addTestCompileOnlyDependency(
    dependency: Provider<ExternalModuleDependencyBundle>?,
    limitToTargets: Set<String>? = null,
    excludeTargets: Set<String>? = null,
) {
    addTestCompileOnlyDependency(dependency as Any?, limitToTargets)
}

private fun Project.addTestCompileOnlyDependency(
    dependency: Any?,
    limitToTargets: Set<String>? = null,
    excludeTargets: Set<String>? = null,
) {
    addDependency(
        dependency = dependency,
        notMultiplatformConfiguration = "testCompileOnly",
        commonConfiguration = "commonTestCompileOnly",
        targetConfiguration = KotlinTarget::testCompileOnlyConfigName,
        limitToTargets = limitToTargets,
        excludeTargets = excludeTargets,
    )
}

internal fun Project.addTestRuntimeOnlyDependency(
    dependency: Provider<MinimalExternalModuleDependency>?,
    limitToTargets: Set<String>? = null,
    excludeTargets: Set<String>? = null,
) {
    addTestRuntimeOnlyDependency(dependency as Any?, limitToTargets)
}

@JvmName("addTestRuntimeOnlyDependencyBundle")
internal fun Project.addTestRuntimeOnlyDependency(
    dependency: Provider<ExternalModuleDependencyBundle>?,
    limitToTargets: Set<String>? = null,
    excludeTargets: Set<String>? = null,
) {
    addTestRuntimeOnlyDependency(dependency as Any?, limitToTargets)
}

private fun Project.addTestRuntimeOnlyDependency(
    dependency: Any?,
    limitToTargets: Set<String>? = null,
    excludeTargets: Set<String>? = null,
) {
    addDependency(
        dependency = dependency,
        notMultiplatformConfiguration = "testRuntimeOnly",
        commonConfiguration = "commonTestRuntimeOnly",
        targetConfiguration = KotlinTarget::testRuntimeOnlyConfigName,
        limitToTargets = limitToTargets,
        excludeTargets = excludeTargets,
    )
}

internal fun Project.addKspDependency(
    dependency: Provider<MinimalExternalModuleDependency>?,
    limitToTargets: Set<String>? = null,
    excludeTargets: Set<String>? = setOf("metadata"),
) {
    addDependency(
        dependency = dependency,
        notMultiplatformConfiguration = "ksp",
        commonConfiguration = "kspCommonMainMetadata",
        targetConfiguration = KotlinTarget::kspConfigName,
        limitToTargets = limitToTargets,
        excludeTargets = excludeTargets,
    )
}

private fun Project.addDependency(
    dependency: Any?,
    notMultiplatformConfiguration: String,
    commonConfiguration: String,
    targetConfiguration: KotlinTarget.() -> String,
    limitToTargets: Set<String>?,
    excludeTargets: Set<String>?,
) {
    if (dependency == null) {
        return
    }

    val extension = kotlinExtension
    if (extension is KotlinMultiplatformExtension) {
        if (limitToTargets == null && excludeTargets == null) {
            dependencies.add(commonConfiguration, dependency)
        } else {
            extension.targets.configureEach {
                if (limitToTargets != null) {
                    if (limitToTargets.contains(it.targetName)) {
                        dependencies.add(it.targetConfiguration(), dependency)
                    }
                } else if (excludeTargets != null) {
                    if (!excludeTargets.contains(it.targetName)) {
                        dependencies.add(it.targetConfiguration(), dependency)
                    }
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
