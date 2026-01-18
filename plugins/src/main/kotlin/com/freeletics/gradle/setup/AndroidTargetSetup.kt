package com.freeletics.gradle.setup

import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryTarget
import com.freeletics.gradle.plugin.FreeleticsMultiplatformAndroidExtension
import com.freeletics.gradle.util.addCompileOnlyDependency
import com.freeletics.gradle.util.addImplementationDependency
import com.freeletics.gradle.util.addMaybe
import com.freeletics.gradle.util.defaultPackageName
import com.freeletics.gradle.util.freeleticsExtension
import com.freeletics.gradle.util.getBundleOrNull
import com.freeletics.gradle.util.getDependencyOrNull
import com.freeletics.gradle.util.getVersion
import com.freeletics.gradle.util.getVersionOrNull
import kotlin.text.toInt
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test

internal fun KotlinMultiplatformAndroidLibraryTarget.setupAndroidTarget(
    target: Project,
    configure: FreeleticsMultiplatformAndroidExtension.() -> Unit,
) {
    namespace = target.defaultPackageName()

    val buildTools = target.getVersionOrNull("android.buildTools")
    if (buildTools != null) {
        buildToolsVersion = buildTools
    }

    compileSdk = target.getVersion("android.compile").toInt()
    minSdk = target.getVersion("android.min").toInt()

    // default all features to false, they will be enabled through FreeleticsAndroidExtension
    androidResources.enable = false

    val desugarLibrary = target.getDependencyOrNull("android.desugarjdklibs")
    enableCoreLibraryDesugaring = desugarLibrary != null
    target.dependencies.addMaybe("coreLibraryDesugaring", desugarLibrary)

    lint.configure(target)

    // enable tests
    withHostTestBuilder {}.configure {
        isIncludeAndroidResources = true
    }
    // AGP sets failOnNoDiscoveredTests to true by default
    // we want tests to be enabled by default for convenience so we disable the option again
    target.afterEvaluate {
        it.tasks.named("testAndroidHostTest") { task ->
            (task as Test).failOnNoDiscoveredTests.set(false)
        }
    }

    // add default dependencies
    val bundle = target.getBundleOrNull("default-android")
    if (bundle != null) {
        target.addImplementationDependency(bundle, setOf("android"))
    }
    val compileBundle = target.getBundleOrNull("default-android-compile")
    if (compileBundle != null) {
        target.addCompileOnlyDependency(compileBundle, setOf("android"))
    }

    project.freeleticsExtension.extensions
        .create("android", FreeleticsMultiplatformAndroidExtension::class.java)
        .configure()
}
