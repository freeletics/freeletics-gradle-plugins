package com.freeletics.gradle.setup

import com.freeletics.gradle.plugin.FreeleticsBaseExtension.DaggerMode
import com.freeletics.gradle.util.booleanProperty
import com.freeletics.gradle.util.getDependency
import com.freeletics.gradle.util.getDependencyOrNull
import com.squareup.anvil.plugin.AnvilExtension
import org.gradle.api.Project

internal fun Project.configureDagger(mode: DaggerMode) {
    plugins.apply("com.squareup.anvil")

    extensions.configure(AnvilExtension::class.java) {
        // only full dagger modules use dagger compiler all others use anvil to generate factories
        it.generateDaggerFactories.set(mode != DaggerMode.ANVIL_WITH_FULL_DAGGER)
        // we ony do component merging when using dagger to generate components
        it.disableComponentMerging.set(mode != DaggerMode.ANVIL_WITH_FULL_DAGGER)
    }

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
        dependencies.apply {
            add("anvil", getDependency("khonshu-codegen-compiler"))
        }
    }

    val useKsp = booleanProperty("fgp.kotlin.daggerKsp", false)
    if (mode == DaggerMode.ANVIL_WITH_FULL_DAGGER) {
        val processorConfiguration = configureProcessing(
            useKsp = useKsp.get(),
            "dagger.experimentalDaggerErrorMessages" to "enabled",
            "dagger.strictMultibindingValidation" to "enabled",
            "dagger.warnIfInjectionFactoryNotGeneratedUpstream" to "enabled",
        )

        dependencies.apply {
            add(processorConfiguration, getDependency("dagger.compiler"))
        }
    }
}
