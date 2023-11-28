package com.freeletics.gradle.util

import com.android.Version
import com.android.build.api.AndroidPluginVersion
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
import com.freeletics.gradle.util.KotlinProjectExtensionDelegate.Companion.kotlinProjectExtensionDelegate
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

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

internal fun Project.kotlin(action: KotlinProjectExtensionDelegate.() -> Unit) {
    kotlinProjectExtensionDelegate().action()
}

internal fun Project.kotlinMultiplatform(action: KotlinMultiplatformExtension.() -> Unit) {
    extensions.configure(KotlinMultiplatformExtension::class.java) {
        it.action()
    }
}

internal val androidPluginVersion: AndroidPluginVersion
    get() {
        val parts = Version.ANDROID_GRADLE_PLUGIN_VERSION.split("-")[0].split(".")
        return AndroidPluginVersion(parts[0].toInt(), parts[1].toInt(), parts[2].toInt())
    }

internal fun Project.android(action: CommonExtension<*, *, *, *, *>.() -> Unit) {
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
