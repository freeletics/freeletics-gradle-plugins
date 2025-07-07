package com.freeletics.gradle.setup

import com.freeletics.gradle.plugin.FreeleticsBaseExtension.DaggerMode
import com.freeletics.gradle.util.addApiDependency
import com.freeletics.gradle.util.addKspDependency
import com.freeletics.gradle.util.booleanProperty
import com.freeletics.gradle.util.getDependency
import com.freeletics.gradle.util.getDependencyOrNull
import com.freeletics.gradle.util.getVersion
import dev.zacsweers.metro.gradle.MetroPluginExtension
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType.androidJvm
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType.jvm

internal fun Project.configureMetro() {
    plugins.apply("dev.zacsweers.metro")
}

internal fun Project.configureKhonshu() {
    configureProcessing(useKsp = true)

    addApiDependency(getDependency("khonshu-codegen-runtime"), SUPPORTED_PLATFORMS)
    addKspDependency(getDependency("khonshu-codegen-compiler"), SUPPORTED_PLATFORMS)

    // TODO workaround for Gradle not being able to resolve this in the ksp config
    configurations.named("ksp").configure {
        it.exclude(mapOf("group" to "org.jetbrains.skiko", "module" to "skiko"))
    }
}

internal fun Project.configureDagger(mode: DaggerMode) {
    addApiDependency(getDependency("inject"), SUPPORTED_PLATFORMS)
    addApiDependency(getDependency("anvil-annotations"), SUPPORTED_PLATFORMS)
    addApiDependency(getDependency("anvil-annotations-optional"), SUPPORTED_PLATFORMS)
    addApiDependency(getDependency("dagger"), SUPPORTED_PLATFORMS)
    addApiDependency(getDependencyOrNull("khonshu-codegen-runtime"), SUPPORTED_PLATFORMS)

    if (booleanProperty("fgp.metro.migrationEnabled", false).get()) {
        configureMetro()

        if (booleanProperty("fgp.metro.interop", false).get()) {
            extensions.configure(MetroPluginExtension::class.java) {
                it.interop {
                    it.includeDagger()
                    it.includeAnvil(includeDaggerAnvil = true, includeKotlinInjectAnvil = false)
                }
            }
        }

        if (mode == DaggerMode.ANVIL_WITH_KHONSHU) {
            configureKhonshu()
        }

        return
    }

    configureProcessing(
        useKsp = true,
        basicArgument("generate-dagger-factories" to "${mode != DaggerMode.ANVIL_WITH_FULL_DAGGER}"),
        basicArgument("disable-component-merging" to "${mode != DaggerMode.ANVIL_WITH_FULL_DAGGER}"),
        basicArgument("will-have-dagger-factories" to "true"),
        basicArgument("merging-backend" to if (mode == DaggerMode.ANVIL_WITH_FULL_DAGGER) "ksp" else "none"),
    )

    addKspDependency(getDependency("anvil-compiler"), SUPPORTED_PLATFORMS)

    if (mode == DaggerMode.ANVIL_WITH_KHONSHU) {
        configureKhonshu()
    }

    if (mode == DaggerMode.ANVIL_WITH_FULL_DAGGER) {
        val daggerProcessorConfiguration = configureProcessing(
            useKsp = booleanProperty("fgp.kotlin.daggerKsp", false).get(),
            basicArgument("dagger.experimentalDaggerErrorMessages" to "ENABLED"),
            basicArgument("dagger.strictMultibindingValidation" to "ENABLED"),
            basicArgument("dagger.warnIfInjectionFactoryNotGeneratedUpstream" to "ENABLED"),
            basicArgument("dagger.useBindingGraphFix" to "ENABLED"),
            basicArgument("dagger.ignoreProvisionKeyWildcards" to "ENABLED"),
        )
        dependencies.add(daggerProcessorConfiguration, getDependency("dagger-compiler"))
    }

    anvilForkDependencySustitution()
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
