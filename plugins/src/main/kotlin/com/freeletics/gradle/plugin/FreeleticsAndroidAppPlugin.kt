package com.freeletics.gradle.plugin

import com.android.build.api.AndroidPluginVersion
import com.android.build.api.variant.HasUnitTestBuilder
import com.freeletics.gradle.setup.configure
import com.freeletics.gradle.util.addCompileOnlyDependency
import com.freeletics.gradle.util.addImplementationDependency
import com.freeletics.gradle.util.addMaybe
import com.freeletics.gradle.util.androidApp
import com.freeletics.gradle.util.androidComponentsApp
import com.freeletics.gradle.util.defaultPackageName
import com.freeletics.gradle.util.freeleticsExtension
import com.freeletics.gradle.util.getBundleOrNull
import com.freeletics.gradle.util.getDependencyOrNull
import com.freeletics.gradle.util.getVersion
import com.freeletics.gradle.util.getVersionOrNull
import com.freeletics.gradle.util.javaTargetVersion
import kotlin.jvm.java
import kotlin.text.toInt
import org.gradle.api.Plugin
import org.gradle.api.Project

public abstract class FreeleticsAndroidAppPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.plugins.apply("com.android.application")
        if (AndroidPluginVersion.getCurrent() < AndroidPluginVersion(9, 0, 0).beta(1)) {
            target.plugins.apply("org.jetbrains.kotlin.android")
        }
        target.plugins.apply(FreeleticsBasePlugin::class.java)

        target.freeleticsExtension.extensions.create("android", FreeleticsAndroidAppExtension::class.java)

        target.androidSetup()
        target.addDefaultAndroidDependencies()
        target.configureLint()
        target.disableReleaseUnitTests()
        target.disableAndroidTests()
    }

    private fun Project.androidSetup() {
        val desugarLibrary = project.getDependencyOrNull("android.desugarjdklibs")
        androidApp {
            namespace = defaultPackageName()

            val buildTools = getVersionOrNull("android.buildTools")
            if (buildTools != null) {
                buildToolsVersion = buildTools
            }

            compileSdk = getVersion("android.compile").toInt()
            defaultConfig.minSdk = getVersion("android.min").toInt()
            defaultConfig.targetSdk = getVersion("android.target").toInt()

            buildFeatures {
                resValues = true
                buildConfig = true

                viewBinding = false
                dataBinding = false
                aidl = false
                shaders = false
            }

            compileOptions {
                isCoreLibraryDesugaringEnabled = desugarLibrary != null
                sourceCompatibility = javaTargetVersion
                targetCompatibility = javaTargetVersion
            }
        }

        dependencies.addMaybe("coreLibraryDesugaring", desugarLibrary)
    }

    private fun Project.addDefaultAndroidDependencies() {
        val bundle = getBundleOrNull("default-android")
        if (bundle != null) {
            addImplementationDependency(bundle)
        }
        val compileBundle = getBundleOrNull("default-android-compile")
        if (compileBundle != null) {
            addCompileOnlyDependency(compileBundle)
        }
    }

    private fun Project.configureLint() {
        androidApp {
            lint.configure(project)
        }
    }

    private fun Project.disableReleaseUnitTests() {
        androidComponentsApp {
            beforeVariants(selector().withBuildType("release")) {
                (it as? HasUnitTestBuilder)?.enableUnitTest = false
            }
        }
    }

    private fun Project.disableAndroidTests() {
        androidComponentsApp {
            beforeVariants {
                it.androidTest.enable = false
            }
        }
    }
}
