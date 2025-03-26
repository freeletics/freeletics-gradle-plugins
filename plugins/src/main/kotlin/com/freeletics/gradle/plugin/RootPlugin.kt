package com.freeletics.gradle.plugin

import com.autonomousapps.DependencyAnalysisExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.buildconfiguration.tasks.UpdateDaemonJvm
import org.gradle.internal.jvm.inspection.JvmVendor

public abstract class RootPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.plugins.apply("com.autonomousapps.dependency-analysis")

        configureDaemonToolchainTask(target)
        createPlatform(target)
        configureDependencyAnalysis(target)
    }

    @Suppress("UnstableApiUsage")
    private fun configureDaemonToolchainTask(target: Project) {
        target.tasks.withType(UpdateDaemonJvm::class.java).configureEach {
            it.jvmVendor.set(JvmVendor.KnownJvmVendor.AZUL.name)
        }
    }

    private fun createPlatform(target: Project) {
        target.plugins.apply("java-platform")

        val catalogs = target.extensions.getByType(VersionCatalogsExtension::class.java)

        target.dependencies.constraints { constraints ->
            catalogs.forEach catalogForEach@{ catalog ->
                catalog.libraryAliases.forEach { libraryAlias ->
                    val library = catalog.findLibrary(libraryAlias).get().get()
                    val module = library.module
                    val version = library.versionConstraint.requiredVersion

                    if (module.group == "com.google.guava") {
                        return@forEach
                    }

                    constraints.add("api", library)

                    // if this is a ktx library also add the non ktx artifact to the platform
                    if (module.name.endsWith("-ktx")) {
                        constraints.add("api", "${module.group}:${module.name.replace("-ktx", "")}:$version")
                    }
                    // for KMP modules where the platform artifact is specified also add the common artifact
                    if (module.name.endsWith("-jvm")) {
                        constraints.add("api", "${module.group}:${module.name.replace("-jvm", "")}:$version")
                    }
                    // for KMP modules where the platform artifact is specified also add the common artifact
                    if (module.name.endsWith("-android")) {
                        constraints.add("api", "${module.group}:${module.name.replace("-android", "")}:$version")
                    }
                    // add all Kotlin stdlib variations
                    if (module.group == "org.jetbrains.kotlin" && module.name == "kotlin-stdlib") {
                        constraints.add("api", "${module.group}:${module.name}-common:$version")
                        constraints.add("api", "${module.group}:${module.name}-jdk7:$version")
                        constraints.add("api", "${module.group}:${module.name}-jdk8:$version")
                    }
                }
            }
        }
    }

    private fun configureDependencyAnalysis(target: Project) {
        target.extensions.configure(DependencyAnalysisExtension::class.java) { analysis ->
            analysis.issues { issues ->
                issues.all { project ->
                    project.onAny {
                        it.severity("fail")
                    }

                    // add all default dependencies to not report them being unused or in the wrong configuration
                    val catalogs = target.extensions.getByType(VersionCatalogsExtension::class.java)
                    catalogs.forEach { catalog ->
                        catalog.bundleAliases.forEach { bundleAlias ->
                            if (bundleAlias.startsWith("default.")) {
                                val bundle = catalog.findBundle(bundleAlias).get().get()
                                project.onUnusedDependencies { issue ->
                                    bundle.forEach { dependency ->
                                        issue.exclude("${dependency.module.group}:${dependency.module.name}")
                                    }
                                }
                                project.onIncorrectConfiguration { issue ->
                                    bundle.forEach { dependency ->
                                        issue.exclude("${dependency.module.group}:${dependency.module.name}")
                                    }
                                }
                            }
                        }
                    }

                    project.onUnusedDependencies {
                        it.exclude(
                            // added by the Kotlin plugin
                            "org.jetbrains.kotlin:kotlin-stdlib",
                            // parcelize is enabled on all Android modules
                            "org.jetbrains.kotlin:kotlin-parcelize-runtime",
                            // added automatically to all app modules
                            "com.freeletics.gradle:minify-common",
                            // added automatically to all app modules with crash reporting
                            "com.freeletics.gradle:minify-crashlytics",
                            // added automatically when enabling Dagger but not all of them might be used
                            // the Dagger runtime is not listed here and it being marked as unused is the indicator
                            // to remove useDagger from the module
                            "javax.inject:javax.inject",
                            "com.squareup.anvil:annotations",
                            "com.squareup.anvil:annotations-optional",
                            "dev.zacsweers.anvil:annotations",
                            "dev.zacsweers.anvil:annotations-optional",
                            "com.freeletics.khonshu:codegen-runtime",
                            "com.freeletics.khonshu:codegen-scope",
                        )
                    }

                    project.onIncorrectConfiguration {
                        it.exclude(
                            // added by the Kotlin plugin
                            "org.jetbrains.kotlin:kotlin-stdlib",
                            // Dagger is always added as "api", but some modules only use it in for example debugApi
                            "javax.inject:javax.inject",
                            "com.squareup.anvil:annotations",
                            "com.squareup.anvil:annotations-optional",
                            "dev.zacsweers.anvil:annotations",
                            "dev.zacsweers.anvil:annotations-optional",
                            "com.freeletics.khonshu:codegen-runtime",
                            "com.freeletics.khonshu:codegen-scope",
                            "com.google.dagger:dagger",
                            "com.google.dagger:dagger-compiler",
                            // Room is always added as "api", but some modules only use it in for example debugApi
                            "androidx.room:room-runtime",
                            // Kotlinx.serialization is always added as "api", but some modules only use it in for example debugApi
                            "org.jetbrains.kotlinx:kotlinx-serialization-core",
                            "org.jetbrains.kotlinx:kotlinx-serialization-core-jvm",
                        )
                    }

                    project.onUsedTransitiveDependencies {
                        it.exclude(
                            // added by the Parcelize plugin
                            "org.jetbrains.kotlin:kotlin-parcelize-runtime",
                        )
                    }

                    project.onRedundantPlugins {
                        // needs to be set separately from onAny which does not apply here
                        it.severity("fail")
                    }

                    project.onDuplicateClassWarnings {
                        // layoutlib also contains these
                        it.exclude("org/jetbrains/annotations/NotNull", "org/jetbrains/annotations/Nullable")
                    }
                }
            }

            analysis.structure { structure ->
                structure.ignoreKtx(true)

                structure.bundle("androidx-compose-runtime") {
                    it.primary("androidx.compose.runtime:runtime")
                    it.includeGroup("androidx.compose.runtime")
                }
                structure.bundle("androidx-compose-ui") {
                    it.primary("androidx.compose.ui:ui")
                    it.includeGroup("androidx.compose.ui")
                    it.includeDependency("androidx.compose.runtime:runtime-saveable")
                }
                structure.bundle("androidx-compose-foundation") {
                    it.primary("androidx.compose.foundation:foundation")
                    it.includeGroup("androidx.compose.animation")
                    it.includeGroup("androidx.compose.foundation")
                }
                structure.bundle("androidx-compose-material") {
                    it.primary("androidx.compose.material:material")
                    it.includeGroup("androidx.compose.material")
                }
                structure.bundle("androidx-compose-material3") {
                    it.primary("androidx.compose.material3:material3")
                    it.includeGroup("androidx.compose.material3")
                }

                structure.bundle("dagger") {
                    it.includeDependency("javax.inject:javax.inject")
                    it.includeDependency("com.google.dagger:dagger")
                }

                structure.bundle("kotest") {
                    it.includeGroup("io.kotest")
                }

                structure.bundle("paparazzi") {
                    it.primary("app.cash.paparazzi")
                    it.includeGroup("app.cash.paparazzi")
                    it.includeGroup("com.android.tools.layoutlib")
                }
            }
        }
    }
}
