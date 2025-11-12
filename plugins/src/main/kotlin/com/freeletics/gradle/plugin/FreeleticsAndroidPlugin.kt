package com.freeletics.gradle.plugin

import com.android.build.api.variant.HasAndroidTestBuilder
import com.android.build.api.variant.HasUnitTestBuilder
import com.freeletics.gradle.setup.configure
import com.freeletics.gradle.util.addCompileOnlyDependency
import com.freeletics.gradle.util.addImplementationDependency
import com.freeletics.gradle.util.addMaybe
import com.freeletics.gradle.util.android
import com.freeletics.gradle.util.androidComponents
import com.freeletics.gradle.util.dataBinding
import com.freeletics.gradle.util.defaultPackageName
import com.freeletics.gradle.util.enable
import com.freeletics.gradle.util.freeleticsExtension
import com.freeletics.gradle.util.getBundleOrNull
import com.freeletics.gradle.util.getDependencyOrNull
import com.freeletics.gradle.util.getVersion
import com.freeletics.gradle.util.getVersionOrNull
import com.freeletics.gradle.util.javaTargetVersion
import org.gradle.api.Plugin
import org.gradle.api.Project

public abstract class FreeleticsAndroidPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        if (!target.plugins.hasPlugin("com.android.application")) {
            target.plugins.apply("com.android.library")
        }
        target.plugins.apply("org.jetbrains.kotlin.android")
        target.plugins.apply(FreeleticsBasePlugin::class.java)

        target.freeleticsExtension.extensions.create("android", FreeleticsAndroidExtension::class.java)

        target.androidSetup()
        target.addDefaultAndroidDependencies()
        target.disableReleaseUnitTests()
        target.disableAndroidTests()
    }

    private fun Project.androidSetup() {
        val desugarLibrary = project.getDependencyOrNull("android.desugarjdklibs")
        android {
            namespace = defaultPackageName()

            val buildTools = getVersionOrNull("android.buildTools")
            if (buildTools != null) {
                buildToolsVersion = buildTools
            }

            compileSdk = getVersion("android.compile").toInt()
            defaultConfig.minSdk = getVersion("android.min").toInt()

            // default all features to false, they will be enabled through FreeleticsAndroidExtension
            androidResources.enable = false
            buildFeatures {
                viewBinding = false
                resValues = false
                buildConfig = false
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

            lint.configure(project)
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

    private fun Project.disableReleaseUnitTests() {
        androidComponents {
            beforeVariants(selector().withBuildType("release")) {
                (it as? HasUnitTestBuilder)?.enableUnitTest = false
            }
        }
    }

    private fun Project.disableAndroidTests() {
        androidComponents {
            beforeVariants {
                if (it is HasAndroidTestBuilder) {
                    it.androidTest.enable = false
                }
            }
        }
    }
}
