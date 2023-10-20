package com.freeletics.gradle.setup

import com.freeletics.gradle.plugin.FreeleticsBaseExtension.DaggerMode
import com.freeletics.gradle.util.addMaybe
import com.freeletics.gradle.util.booleanProperty
import com.freeletics.gradle.util.getDependency
import com.freeletics.gradle.util.getDependencyOrNull
import com.squareup.anvil.plugin.AnvilExtension
import org.gradle.api.Project

internal fun Project.configureDagger(mode: DaggerMode) {
    val daggerKsp = booleanProperty("fgp.kotlin.daggerKsp", false)
    val anvilKsp = booleanProperty("fgp.kotlin.anvilKsp", false)
    val khonshuKsp = booleanProperty("fgp.kotlin.khonshuKsp", false)

    // When not requiring Dagger's component generation we usually don't
    // apply it at all and let Anvil handle the factory generation. There
    // is no advantage to do that when running Anvil through KSP since KSP
    // is applied anyways so there is no additional overhead for running Dagger.
    val applyDaggerProcessor = mode == DaggerMode.ANVIL_WITH_FULL_DAGGER || (anvilKsp.get() && daggerKsp.get())

    applyAnvil(
        // when Dagger KSP is used, Anvil needs to be used through KSP as well
        useKsp = (applyDaggerProcessor && daggerKsp.get()) || anvilKsp.get(),
        generateDaggerFactories = !applyDaggerProcessor,
        // we ony do component merging when using dagger to generate components
        disableComponentMerging = mode != DaggerMode.ANVIL_WITH_FULL_DAGGER,
    )

    dependencies.apply {
        add("api", getDependency("inject"))
        add("api", getDependency("anvil-annotations"))
        add("api", getDependency("anvil-annotations-optional"))
        add("api", getDependency("dagger"))
        addMaybe("api", getDependencyOrNull("khonshu-codegen-runtime"))
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

    if (applyDaggerProcessor) {
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
            "generate-dagger-factories" to "$generateDaggerFactories",
            "disable-component-merging" to "$disableComponentMerging",
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
