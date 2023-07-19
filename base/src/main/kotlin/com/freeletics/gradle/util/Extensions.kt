package com.freeletics.gradle.util

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.LibraryExtension
import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import com.android.build.api.variant.LibraryAndroidComponentsExtension
import com.freeletics.gradle.util.KotlinProjectExtensionDelegate.Companion.kotlinProjectExtensionDelegate
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

public fun Project.java(action: JavaPluginExtension.() -> Unit) {
    extensions.configure(JavaPluginExtension::class.java) {
        it.action()
    }
}

public fun Project.kotlin(action: KotlinProjectExtensionDelegate.() -> Unit) {
    kotlinProjectExtensionDelegate().action()
}

public fun Project.kotlinMultiplatform(action: KotlinMultiplatformExtension.() -> Unit) {
    extensions.configure(KotlinMultiplatformExtension::class.java) {
        it.action()
    }
}

public fun Project.android(action: CommonExtension<*, *, *, *>.() -> Unit) {
    extensions.configure(CommonExtension::class.java) {
        it.action()
    }
}

public fun Project.androidLibrary(action: LibraryExtension.() -> Unit) {
    extensions.configure(LibraryExtension::class.java) {
        it.action()
    }
}

public fun Project.androidApp(action: ApplicationExtension.() -> Unit) {
    extensions.configure(ApplicationExtension::class.java) {
        it.action()
    }
}

public fun Project.androidComponents(action: AndroidComponentsExtension<*, *, *>.() -> Unit) {
    extensions.configure(AndroidComponentsExtension::class.java) {
        it.action()
    }
}

public fun Project.androidComponentsLibrary(action: LibraryAndroidComponentsExtension.() -> Unit) {
    extensions.configure(LibraryAndroidComponentsExtension::class.java) {
        it.action()
    }
}

public fun Project.androidComponentsApp(action: ApplicationAndroidComponentsExtension.() -> Unit) {
    extensions.configure(ApplicationAndroidComponentsExtension::class.java) {
        it.action()
    }
}
