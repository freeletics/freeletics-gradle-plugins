package com.freeletics.gradle.plugin

import com.autonomousapps.DependencyAnalysisExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension

public abstract class RootPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.plugins.apply("com.autonomousapps.dependency-analysis")

        val libs = target.extensions.getByType(VersionCatalogsExtension::class.java).named("libs")
        val expectedJavaVersion = libs.findVersion("java-gradle").orElseGet { null }?.requiredVersion
        if (expectedJavaVersion != null) {
            if (JavaVersion.current().toString() != expectedJavaVersion) {
                target.logger.error("JDK $expectedJavaVersion is required to build this project")
                throw RuntimeException("JDK $expectedJavaVersion is required to build this project")
            }
            if (System.getProperty("java.vendor") != "Azul Systems, Inc.") {
                target.logger.error("The Azul Zulu JDK should be used to run Gradle")
                throw RuntimeException("The Azul Zulu JDK should be used to run Gradle")
            }
        }

        target.extensions.configure(DependencyAnalysisExtension::class.java) { analysis ->
            analysis.issues { issues ->
                issues.all { project ->
                    project.ignoreKtx(true)
                    project.onAny {
                        it.severity("fail")
                        it.exclude(
                            // added by the Kotlin plugin
                            "org.jetbrains.kotlin:kotlin-stdlib",
                            // default dependencies for all modules
                            "junit:junit",
                            "io.kotest:kotest-runner-junit4-jvm",
                            "io.kotest:kotest-assertions-core-jvm",
                            // default dependencies for all Android modules
                            "androidx.core:core-ktx",
                            "com.jakewharton.timber:timber",
                            // parcelize is enabled on all Android modules
                            "org.jetbrains.kotlin:kotlin-parcelize-runtime",
                            // added automatically but only standard Dagger annotations might be used
                            // the dagger runtime is not listed here and it being marked as unused is the indicator
                            // to remove useDagger from the module
                            "javax.inject:javax.inject",
                            "com.squareup.anvil:annotations",
                            "com.freeletics.khonshu:codegen-scope",
                            // added by KGP since 1.8.20
                            // https://github.com/autonomousapps/dependency-analysis-android-gradle-plugin/issues/884
                            "() -> java.io.File?",
                        )
                    }

                    project.onIncorrectConfiguration {
                        it.exclude(
                            // Dagger is always added as "api", but some modules only use it in for example debugApi
                            "javax.inject:javax.inject",
                            "com.squareup.anvil:annotations",
                            "com.freeletics.khonshu:codegen-scope",
                            "com.google.dagger:dagger",
                            "com.google.dagger:dagger-compiler",
                            // added by the MoshiX plugin
                            "com.squareup.moshi:moshi",
                            "dev.zacsweers.moshix:moshi-sealed-runtime",
                        )
                    }

                    project.onRedundantPlugins {
                        // needs to be set separately from onAny which does not apply here
                        it.severity("fail")
                    }
                }
            }

            analysis.dependencies { dependencies ->
                dependencies.bundle("androidx-compose-runtime") {
                    it.primary("androidx.compose.runtime:runtime")
                    it.includeGroup("androidx.compose.runtime")
                }
                dependencies.bundle("androidx-compose-ui") {
                    it.primary("androidx.compose.ui:ui")
                    it.includeGroup("androidx.compose.ui")
                    it.includeDependency("androidx.compose.runtime:runtime-saveable")
                }
                dependencies.bundle("androidx-compose-foundation") {
                    it.primary("androidx.compose.foundation:foundation")
                    it.includeGroup("androidx.compose.animation")
                    it.includeGroup("androidx.compose.foundation")
                }
                dependencies.bundle("androidx-compose-material") {
                    it.primary("androidx.compose.material:material")
                    it.includeGroup("androidx.compose.material")
                }
                dependencies.bundle("androidx-compose-material3") {
                    it.primary("androidx.compose.material3:material3")
                    it.includeGroup("androidx.compose.material3")
                }

                dependencies.bundle("dagger") {
                    it.includeDependency("javax.inject:javax.inject")
                    it.includeDependency("com.google.dagger:dagger")
                }

                dependencies.bundle("kotest") {
                    it.includeGroup("io.kotest")
                }

                dependencies.bundle("paparazzi") {
                    it.primary("com.freeletics.fork.paparazzi:paparazzi")
                    it.includeGroup("app.cash.paparazzi")
                    it.includeGroup("com.freeletics.fork.paparazzi")
                    it.includeGroup("com.android.tools.layoutlib")
                }
            }
        }
    }
}
