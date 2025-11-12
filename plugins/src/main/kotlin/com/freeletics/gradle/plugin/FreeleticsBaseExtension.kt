package com.freeletics.gradle.plugin

import com.freeletics.gradle.setup.configureProcessing
import com.freeletics.gradle.setup.setupCompose
import com.freeletics.gradle.setup.setupInternalPublishing
import com.freeletics.gradle.setup.setupOssPublishing
import com.freeletics.gradle.setup.setupSqlDelight
import com.freeletics.gradle.util.addApiDependency
import com.freeletics.gradle.util.addKspDependency
import com.freeletics.gradle.util.compilerOptions
import com.freeletics.gradle.util.getDependency
import com.freeletics.gradle.util.kotlin
import org.gradle.api.Project
import org.gradle.api.internal.catalog.DelegatingProjectDependency
import org.gradle.api.plugins.ExtensionAware

public abstract class FreeleticsBaseExtension(private val project: Project) : ExtensionAware {
    public fun explicitApi() {
        project.kotlin {
            explicitApi()
        }
    }

    public fun optIn(vararg classes: String) {
        project.kotlin {
            compilerOptions {
                optIn.addAll(*classes)
            }
        }
    }

    public fun useCompose() {
        project.setupCompose()
    }

    public fun useSerialization() {
        project.plugins.apply("org.jetbrains.kotlin.plugin.serialization")

        project.addApiDependency(project.getDependency("kotlinx-serialization"))
    }

    public fun useMetro() {
        project.plugins.apply("dev.zacsweers.metro")
    }

    public fun useKhonshu() {
        useMetro()
        project.configureProcessing()
        project.addApiDependency(project.getDependency("khonshu-codegen-runtime"))
        project.addKspDependency(
            project.getDependency("khonshu-codegen-compiler"),
            excludeTargets = setOf("metadata"),
        )
        // TODO workaround for Gradle not being able to resolve this in the ksp config
        project.configurations.named("ksp").configure {
            it.exclude(mapOf("group" to "org.jetbrains.skiko", "module" to "skiko"))
        }
    }

    public fun usePoko() {
        project.plugins.apply("dev.drewhamilton.poko")
    }

    public fun useKopy() {
        project.plugins.apply("com.javiersc.kotlin.kopy")
        project.plugins.apply("org.jetbrains.kotlin.plugin.atomicfu")
    }

    public fun useSqlDelight(
        name: String = "Database",
        dependency: DelegatingProjectDependency? = null,
    ) {
        project.setupSqlDelight(name, dependency)
    }

    public fun enableOssPublishing() {
        setupOssPublishing(project)
    }

    public fun enableInternalPublishing() {
        setupInternalPublishing(project)
    }
}
