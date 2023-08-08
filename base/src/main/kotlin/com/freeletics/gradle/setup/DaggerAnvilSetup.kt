package com.freeletics.gradle.setup

import com.freeletics.gradle.plugin.FreeleticsExtension.DaggerMode
import com.freeletics.gradle.util.booleanProperty
import com.freeletics.gradle.util.getDependency
import com.freeletics.gradle.util.getDependencyOrNull
import com.squareup.anvil.plugin.AnvilExtension
import org.gradle.api.Project

internal fun Project.configureDagger(mode: DaggerMode) {
    val daggerKsp = booleanProperty("fgp.kotlin.daggerKsp", false)
    val anvilKsp = booleanProperty("fgp.kotlin.anvilKsp", false)
    val khonshuKsp = booleanProperty("fgp.kotlin.khonshuKsp", false)

    applyAnvil(
        useKsp = anvilKsp.get(),
        // only full dagger modules use dagger compiler all others use anvil to generate factories
        generateDaggerFactories = mode != DaggerMode.ANVIL_WITH_FULL_DAGGER,
        // we ony do component merging when using dagger to generate components
        disableComponentMerging = mode != DaggerMode.ANVIL_WITH_FULL_DAGGER,
    )

    dependencies.apply {
        add("api", getDependency("inject"))
        add("api", getDependency("anvil-annotations"))
        add("api", getDependency("dagger"))
        val khonshuScope = getDependencyOrNull("khonshu-codegen-scope")
        if (khonshuScope != null) {
            add("api", khonshuScope)
        }
    }

    if (mode == DaggerMode.ANVIL_WITH_KHONSHU) {
        val configuration = if (khonshuKsp.get()) {
            configureProcessing(useKsp = true)
        } else {
            "anvil"
        }

        dependencies.apply {
            add(configuration, getDependency("khonshu-codegen-compiler"))
        }
    }

    if (mode == DaggerMode.ANVIL_WITH_FULL_DAGGER) {
        val processorConfiguration = configureProcessing(
            useKsp = daggerKsp.get(),
            "dagger.experimentalDaggerErrorMessages" to "enabled",
            "dagger.strictMultibindingValidation" to "enabled",
            "dagger.warnIfInjectionFactoryNotGeneratedUpstream" to "enabled",
        )

        dependencies.apply {
            add(processorConfiguration, getDependency("dagger-compiler"))
        }
    }
}

private fun Project.applyAnvil(useKsp: Boolean, generateDaggerFactories: Boolean, disableComponentMerging: Boolean) {
    if (useKsp) {
        configureProcessing(
            useKsp = true,
            "generate-dagger-factories" to "$disableComponentMerging",
            "disable-component-merging" to "$generateDaggerFactories",
        )

        dependencies.apply {
            add("ksp", getDependency("anvil-compiler"))
        }
    } else {
        plugins.apply("com.squareup.anvil")

        extensions.configure(AnvilExtension::class.java) {
            it.generateDaggerFactories.set(generateDaggerFactories)
            it.disableComponentMerging.set(disableComponentMerging)
        }
    }
}
