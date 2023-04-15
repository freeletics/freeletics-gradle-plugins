package com.freeletics.gradle.plugin

import com.android.build.api.dsl.ApplicationDefaultConfig
import com.android.build.api.variant.HasAndroidTestBuilder
import com.freeletics.gradle.setup.configure
import com.freeletics.gradle.setup.defaultTestSetup
import com.freeletics.gradle.util.android
import com.freeletics.gradle.util.androidComponents
import com.freeletics.gradle.util.androidResources
import com.freeletics.gradle.util.dataBinding
import com.freeletics.gradle.util.getDependencyOrNull
import com.freeletics.gradle.util.getVersion
import com.freeletics.gradle.util.javaTargetVersion
import com.freeletics.gradle.util.stringProperty
import java.lang.reflect.Method
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.slf4j.ILoggerFactory
import org.slf4j.LoggerFactory

public abstract class FreeleticsAndroidBasePlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.plugins.apply(FreeleticsBasePlugin::class.java)

        target.androidSetup()
        target.configureLint()
        target.configureUnitTests()
        target.disableAndroidTests()

        // TODO workaround for excessive logging, can be removed with 7.4.2
        //  https://issuetracker.google.com/issues/247906487#comment10
        val loggerFactory: ILoggerFactory = LoggerFactory.getILoggerFactory()
        val addNoOpLogger: Method = loggerFactory.javaClass.getDeclaredMethod("addNoOpLogger", String::class.java)
        addNoOpLogger.isAccessible = true
        addNoOpLogger.invoke(loggerFactory, "com.android.build.api.component.impl.MutableListBackedUpWithListProperty")
        addNoOpLogger.invoke(loggerFactory, "com.android.build.api.component.impl.MutableMapBackedUpWithMapProperty")
    }

    private fun Project.androidSetup() {
        val desugarLibrary = project.getDependencyOrNull("android.desugarjdklibs")
        android {
            namespace = pathBasedAndroidNamespace()

            compileSdk = getVersion("android.compile").toInt()
            defaultConfig.minSdk = getVersion("android.min").toInt()
            (defaultConfig as? ApplicationDefaultConfig)?.let {
                it.targetSdk = getVersion("android.target").toInt()
            }

            // default all features to false, they will be enabled through FreeleticsAndroidExtension
            @Suppress("UnstableApiUsage")
            buildFeatures {
                androidResources = false
                viewBinding = false
                resValues = false
                buildConfig = false
                compose = false
                dataBinding = false
                aidl = false
                renderScript = false
                shaders = false
            }

            compileOptions {
                isCoreLibraryDesugaringEnabled = desugarLibrary != null
                sourceCompatibility = javaTargetVersion
                targetCompatibility = javaTargetVersion
            }
        }

        if (desugarLibrary != null) {
            dependencies.add("coreLibraryDesugaring", desugarLibrary)
        }
    }

    private fun Project.pathBasedAndroidNamespace(): String {
        val transformedPath = path.drop(1)
            .split(":")
            .mapIndexed { index, pathElement ->
                val parts = pathElement.split("-")
                if (index == 0) {
                    // top level folders like core, domain, feature etc. handle dashes separately by
                    // having them become separate package elements, also ignore the -freeletics suffix
                    // to avoid package names like com.freeletics.domain.freeletics
                    parts.filter { it != "freeletics" }.joinToString(separator = ".")
                } else {
                    // for second and lower level folders dashes are ignored and the elements are
                    // merged into one word
                    parts.joinToString(separator = "")
                }
            }
            .joinToString(separator = ".")

        val prefix = stringProperty("fgp.android.namespacePrefix").get()
        return "$prefix.$transformedPath"
    }

    private fun Project.configureLint() {
        android {
            lint.configure(project)
        }
    }

    private fun Project.configureUnitTests() {
        android {
            @Suppress("UnstableApiUsage")
            testOptions {
                unitTests.all(Test::defaultTestSetup)
            }
        }

        androidComponents {
            beforeVariants(selector().withBuildType("release")) {
                it.enableUnitTest = false
            }
        }
    }

    private fun Project.disableAndroidTests() {
        androidComponents {
            beforeVariants {
                if (it is HasAndroidTestBuilder) {
                    it.enableAndroidTest = false
                }
            }
        }
    }
}
