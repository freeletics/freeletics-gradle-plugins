package com.freeletics.gradle.setup

import com.freeletics.gradle.plugin.FreeleticsBaseExtension.DaggerMode
import com.freeletics.gradle.plugin.FreeleticsBaseExtension.DaggerMode.ANVIL_WITH_FULL_DAGGER
import com.freeletics.gradle.util.addMaybe
import com.freeletics.gradle.util.booleanProperty
import com.freeletics.gradle.util.getDependency
import com.freeletics.gradle.util.getDependencyOrNull
import com.squareup.anvil.plugin.AnvilExtension
import org.gradle.api.Project

internal fun Project.configureDagger(mode: DaggerMode) {
    val anvilKsp = booleanProperty("fgp.kotlin.anvilKsp", false)
    val daggerKsp = booleanProperty("fgp.kotlin.daggerKsp", false)

    val runDagger = mode == ANVIL_WITH_FULL_DAGGER
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
    }

    dependencies.apply {
        add("api", getDependency("inject"))
        add("api", getDependency("anvil-annotations"))
        add("api", getDependency("anvil-annotations-optional"))
        add("api", getDependency("dagger"))
        addMaybe("api", getDependencyOrNull("khonshu-codegen-runtime"))
    }

    if (mode == DaggerMode.ANVIL_WITH_KHONSHU) {
        val configuration = if (anvilKsp.get()) {
            configureProcessing(useKsp = true).also {
                // TODO workaround for Gradle not being able to resolve this in the ksp config
                configurations.named(it).configure {
                    it.exclude(mapOf("group" to "org.jetbrains.skiko", "module" to "skiko"))
                }
            }
        } else {
            "anvil"
        }

        dependencies.apply {
            add(configuration, getDependency("khonshu-codegen-compiler"))
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
