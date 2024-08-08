package com.freeletics.gradle.setup

import com.freeletics.gradle.plugin.FreeleticsBaseExtension.DaggerMode
import com.freeletics.gradle.plugin.FreeleticsBaseExtension.DaggerMode.ANVIL_WITH_FULL_DAGGER
import com.freeletics.gradle.util.addApiDependency
import com.freeletics.gradle.util.addKspDependency
import com.freeletics.gradle.util.booleanProperty
import com.freeletics.gradle.util.getDependency
import com.freeletics.gradle.util.getDependencyOrNull
import com.freeletics.gradle.util.getVersion
import com.squareup.anvil.plugin.AnvilExtension
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType.androidJvm
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType.jvm

internal fun Project.configureDagger(mode: DaggerMode) {
    addApiDependency(getDependency("inject"), SUPPORTED_PLATFORMS)
    addApiDependency(getDependency("anvil-annotations"), SUPPORTED_PLATFORMS)
    addApiDependency(getDependency("anvil-annotations-optional"), SUPPORTED_PLATFORMS)
    addApiDependency(getDependency("dagger"), SUPPORTED_PLATFORMS)
    addApiDependency(getDependencyOrNull("khonshu-codegen-runtime"), SUPPORTED_PLATFORMS)

    when (mode) {
        DaggerMode.ANVIL_ONLY -> {
            if (booleanProperty("fgp.kotlin.anvilKsp", false).get()) {
                configureProcessing(
                    useKsp = true,
                    basicArgument("generate-dagger-factories" to "true"),
                    basicArgument("disable-component-merging" to "true"),
                    basicArgument("will-have-dagger-factories" to "true"),
                    basicArgument("merging-backend" to "none"),
                )

                dependencySustitution()
                addKspDependency(getDependency("anvil-compiler"), SUPPORTED_PLATFORMS)
            } else {
                plugins.apply("com.squareup.anvil")
                extensions.configure(AnvilExtension::class.java) {
                    it.generateDaggerFactories.set(true)
                    it.disableComponentMerging.set(true)
                    it.trackSourceFiles.set(true)
                }
            }
        }
        DaggerMode.ANVIL_WITH_KHONSHU -> {
            if (booleanProperty("fgp.kotlin.anvilKsp", false).get()) {
                configureProcessing(
                    useKsp = true,
                    basicArgument("generate-dagger-factories" to "true"),
                    basicArgument("disable-component-merging" to "true"),
                    basicArgument("will-have-dagger-factories" to "true"),
                    basicArgument("merging-backend" to "none"),
                ).also {
                    // TODO workaround for Gradle not being able to resolve this in the ksp config
                    configurations.named(it).configure {
                        it.exclude(mapOf("group" to "org.jetbrains.skiko", "module" to "skiko"))
                    }
                }

                dependencySustitution()
                addKspDependency(getDependency("anvil-compiler"), SUPPORTED_PLATFORMS)
                addKspDependency(getDependency("khonshu-codegen-compiler"), SUPPORTED_PLATFORMS)
            } else {
                plugins.apply("com.squareup.anvil")
                extensions.configure(AnvilExtension::class.java) {
                    it.generateDaggerFactories.set(true)
                    it.disableComponentMerging.set(true)
                    it.trackSourceFiles.set(true)
                }

                dependencies.add("anvil", getDependency("khonshu-codegen-compiler"))
            }
        }
        DaggerMode.ANVIL_WITH_FULL_DAGGER -> {
            if (booleanProperty("fgp.kotlin.anvilKspWithComponent", false).get()) {
                configureProcessing(
                    useKsp = true,
                    basicArgument("generate-dagger-factories" to "false"),
                    basicArgument("disable-component-merging" to "false"),
                    basicArgument("will-have-dagger-factories" to "true"),
                    basicArgument("merging-backend" to "ksp"),
                    basicArgument("dagger.experimentalDaggerErrorMessages" to "enabled"),
                    basicArgument("dagger.strictMultibindingValidation" to "enabled"),
                    basicArgument("dagger.warnIfInjectionFactoryNotGeneratedUpstream" to "enabled"),
                )

                dependencySustitution()
                addKspDependency(getDependency("dagger-compiler"), SUPPORTED_PLATFORMS)
                addKspDependency(getDependency("anvil-compiler"), SUPPORTED_PLATFORMS)
            } else {
                plugins.apply("com.squareup.anvil")
                extensions.configure(AnvilExtension::class.java) {
                    it.generateDaggerFactories.set(false)
                    it.disableComponentMerging.set(false)
                    it.trackSourceFiles.set(true)
                }

                val processorConfiguration = configureProcessing(
                    useKsp = false,
                    basicArgument("dagger.experimentalDaggerErrorMessages" to "enabled"),
                    basicArgument("dagger.strictMultibindingValidation" to "enabled"),
                    basicArgument("dagger.warnIfInjectionFactoryNotGeneratedUpstream" to "enabled"),
                )
                dependencies.add(processorConfiguration, getDependency("dagger-compiler"))
            }
        }
    }
}

private fun Project.dependencySustitution() {
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
