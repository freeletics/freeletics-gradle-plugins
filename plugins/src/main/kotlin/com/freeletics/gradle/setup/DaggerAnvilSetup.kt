package com.freeletics.gradle.setup

import com.freeletics.gradle.plugin.FreeleticsBaseExtension.DaggerMode
import com.freeletics.gradle.plugin.FreeleticsBaseExtension.DaggerMode.ANVIL_WITH_FULL_DAGGER
import com.freeletics.gradle.util.addApiDependency
import com.freeletics.gradle.util.addKspDependency
import com.freeletics.gradle.util.booleanProperty
import com.freeletics.gradle.util.getDependency
import com.freeletics.gradle.util.getDependencyOrNull
import com.squareup.anvil.plugin.AnvilExtension
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType.androidJvm
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType.jvm

internal fun Project.configureDagger(mode: DaggerMode) {
    val runDagger = mode == ANVIL_WITH_FULL_DAGGER
    val anvilKsp = if (runDagger) {
        booleanProperty("fgp.kotlin.anvilKspWithComponent", false)
    } else {
        booleanProperty("fgp.kotlin.anvilKsp", false)
    }
    val daggerKsp = booleanProperty("fgp.kotlin.daggerKsp", false)
    val runDaggerInKsp = runDagger && daggerKsp.get()

    plugins.apply("com.squareup.anvil")
    extensions.configure(AnvilExtension::class.java) {
        if (anvilKsp.get() || runDaggerInKsp) {
            it.useKsp(
                contributesAndFactoryGeneration = true,
                // when dagger ksp is used component merging always needs to be done in ksp
                componentMerging = runDaggerInKsp,
            )
        }
        it.generateDaggerFactories.set(!runDagger)
        it.disableComponentMerging.set(!runDagger)
        it.trackSourceFiles.set(true)
    }

    addApiDependency(getDependency("inject"), SUPPORTED_PLATFORMS)
    addApiDependency(getDependency("anvil-annotations"), SUPPORTED_PLATFORMS)
    addApiDependency(getDependency("anvil-annotations-optional"), SUPPORTED_PLATFORMS)
    addApiDependency(getDependency("dagger"), SUPPORTED_PLATFORMS)
    addApiDependency(getDependencyOrNull("khonshu-codegen-runtime"), SUPPORTED_PLATFORMS)

    if (mode == DaggerMode.ANVIL_WITH_KHONSHU) {
        if (anvilKsp.get()) {
            configureProcessing(useKsp = true).also {
                // TODO workaround for Gradle not being able to resolve this in the ksp config
                configurations.named(it).configure {
                    it.exclude(mapOf("group" to "org.jetbrains.skiko", "module" to "skiko"))
                }
            }

            addKspDependency(getDependency("khonshu-codegen-compiler"), SUPPORTED_PLATFORMS)
        } else {
            dependencies.add("anvil", getDependency("khonshu-codegen-compiler"))
        }
    }

    if (runDagger) {
        val processorConfiguration = configureProcessing(
            useKsp = runDaggerInKsp,
            basicArgument("dagger.experimentalDaggerErrorMessages" to "enabled"),
            basicArgument("dagger.strictMultibindingValidation" to "enabled"),
            basicArgument("dagger.warnIfInjectionFactoryNotGeneratedUpstream" to "enabled"),
        )

        dependencies.apply {
            add(processorConfiguration, getDependency("dagger-compiler"))
        }
    }
}

private val SUPPORTED_PLATFORMS = setOf(androidJvm, jvm)
