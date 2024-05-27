package com.freeletics.gradle.util

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.LibraryExtension
import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import com.android.build.api.variant.LibraryAndroidComponentsExtension
import com.freeletics.gradle.plugin.FreeleticsAndroidExtension
import com.freeletics.gradle.plugin.FreeleticsBaseExtension
import com.freeletics.gradle.plugin.FreeleticsJvmExtension
import com.freeletics.gradle.plugin.FreeleticsMultiplatformExtension
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinCommonCompilerOptions
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension

internal val Project.freeleticsExtension: FreeleticsBaseExtension
    get() = extensions.getByType(FreeleticsBaseExtension::class.java)

internal val Project.freeleticsAndroidExtension: FreeleticsAndroidExtension
    get() = freeleticsExtension.extensions.getByType(FreeleticsAndroidExtension::class.java)

internal val Project.freeleticsJvmExtension: FreeleticsJvmExtension
    get() = freeleticsExtension.extensions.getByType(FreeleticsJvmExtension::class.java)

internal val Project.freeleticsMultiplatformExtension: FreeleticsMultiplatformExtension
    get() = freeleticsExtension.extensions.getByType(FreeleticsMultiplatformExtension::class.java)

internal fun Project.java(action: JavaPluginExtension.() -> Unit) {
    extensions.configure(JavaPluginExtension::class.java) {
        it.action()
    }
}

internal fun Project.kotlin(action: KotlinProjectExtension.() -> Unit) {
    (project.extensions.getByName("kotlin") as KotlinProjectExtension).action()
}

@OptIn(ExperimentalKotlinGradlePluginApi::class)
internal fun KotlinProjectExtension.compilerOptions(configure: KotlinCommonCompilerOptions.() -> Unit) {
    when (this) {
        is KotlinJvmProjectExtension -> compilerOptions(configure)
        is KotlinAndroidProjectExtension -> compilerOptions(configure)
        is KotlinMultiplatformExtension -> {
            compilerOptions(configure)
            targets.configureEach { compilerOptions(configure) }
        }
        else -> throw IllegalStateException("Unsupported kotlin extension ${this::class}")
    }
}

internal fun Project.kotlinMultiplatform(action: KotlinMultiplatformExtension.() -> Unit) {
    extensions.configure(KotlinMultiplatformExtension::class.java) {
        it.action()
    }
}

internal fun Project.android(action: CommonExtension<*, *, *, *, *, *>.() -> Unit) {
    extensions.configure(CommonExtension::class.java) {
        it.action()
    }
}

internal fun Project.androidLibrary(action: LibraryExtension.() -> Unit) {
    extensions.configure(LibraryExtension::class.java) {
        it.action()
    }
}

internal fun Project.androidApp(action: ApplicationExtension.() -> Unit) {
    extensions.configure(ApplicationExtension::class.java) {
        it.action()
    }
}

internal fun Project.androidComponents(action: AndroidComponentsExtension<*, *, *>.() -> Unit) {
    extensions.configure(AndroidComponentsExtension::class.java) {
        it.action()
    }
}

internal fun Project.androidComponentsLibrary(action: LibraryAndroidComponentsExtension.() -> Unit) {
    extensions.configure(LibraryAndroidComponentsExtension::class.java) {
        it.action()
    }
}

internal fun Project.androidComponentsApp(action: ApplicationAndroidComponentsExtension.() -> Unit) {
    extensions.configure(ApplicationAndroidComponentsExtension::class.java) {
        it.action()
    }
}
