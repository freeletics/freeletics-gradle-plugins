package com.freeletics.gradle.setup

import com.freeletics.gradle.plugin.FreeleticsBaseExtension.DaggerMode
import com.freeletics.gradle.util.addApiDependency
import com.freeletics.gradle.util.addKspDependency
import com.freeletics.gradle.util.booleanProperty
import com.freeletics.gradle.util.getDependency
import com.freeletics.gradle.util.getDependencyOrNull
import com.freeletics.gradle.util.getVersion
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType.androidJvm
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType.jvm

internal fun Project.configureDagger(mode: DaggerMode) {
    configureProcessing(
        useKsp = true,
        basicArgument("generate-dagger-factories" to "${mode != DaggerMode.ANVIL_WITH_FULL_DAGGER}"),
        basicArgument("disable-component-merging" to "${mode != DaggerMode.ANVIL_WITH_FULL_DAGGER}"),
        basicArgument("will-have-dagger-factories" to "true"),
        basicArgument("merging-backend" to if (mode == DaggerMode.ANVIL_WITH_FULL_DAGGER) "ksp" else "none"),
    )

    addApiDependency(getDependency("inject"), SUPPORTED_PLATFORMS)
    addApiDependency(getDependency("anvil-annotations"), SUPPORTED_PLATFORMS)
    addApiDependency(getDependency("anvil-annotations-optional"), SUPPORTED_PLATFORMS)
    addApiDependency(getDependency("dagger"), SUPPORTED_PLATFORMS)
    addApiDependency(getDependencyOrNull("khonshu-codegen-runtime"), SUPPORTED_PLATFORMS)
    addKspDependency(getDependency("anvil-compiler"), SUPPORTED_PLATFORMS)

    if (mode == DaggerMode.ANVIL_WITH_KHONSHU) {
        addKspDependency(getDependency("khonshu-codegen-compiler"), SUPPORTED_PLATFORMS)
    }

    if (mode == DaggerMode.ANVIL_WITH_FULL_DAGGER) {
        val daggerProcessorConfiguration = configureProcessing(
            useKsp = booleanProperty("fgp.kotlin.daggerKsp", false).get(),
            basicArgument("dagger.experimentalDaggerErrorMessages" to "enabled"),
            basicArgument("dagger.strictMultibindingValidation" to "enabled"),
            basicArgument("dagger.warnIfInjectionFactoryNotGeneratedUpstream" to "enabled"),
        )
        dependencies.add(daggerProcessorConfiguration, getDependency("dagger-compiler"))
    }

    anvilForkDependencySustitution()

    // TODO workaround for Gradle not being able to resolve this in the ksp config
    configurations.named("ksp").configure {
        it.exclude(mapOf("group" to "org.jetbrains.skiko", "module" to "skiko"))
    }
}

private fun Project.anvilForkDependencySustitution() {
    configurations.configureEach { configuration ->
        configuration.resolutionStrategy.dependencySubstitution {
            it.substitute(it.module("com.squareup.anvil:annotations"))
                .using(it.module("dev.zacsweers.anvil:annotations:${getVersion("anvil")}"))
            it.substitute(it.module("com.squareup.anvil:annotations-optional"))
                .using(it.module("dev.zacsweers.anvil:annotations-optional:${getVersion("anvil")}"))
            it.substitute(it.module("com.squareup.anvil:compiler"))
                .using(it.module("dev.zacsweers.anvil:compiler:${getVersion("anvil")}"))
            it.substitute(it.module("com.squareup.anvil:compiler-api"))
                .using(it.module("dev.zacsweers.anvil:compiler-api:${getVersion("anvil")}"))
            it.substitute(it.module("com.squareup.anvil:compiler-utils"))
                .using(it.module("dev.zacsweers.anvil:compiler-utils:${getVersion("anvil")}"))
        }
    }
}

private val SUPPORTED_PLATFORMS = setOf(androidJvm, jvm)
