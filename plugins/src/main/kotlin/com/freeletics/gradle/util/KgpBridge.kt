package com.freeletics.gradle.util

import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.jvm.toolchain.JavaToolchainSpec
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinCommonCompilerOptions
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask

// TODO can be removed after KotlinMultiplatformExtension supports `compilerOptions`
//  which should happen in 1.9.20 https://youtrack.jetbrains.com/issue/KT-55515
internal sealed class KotlinProjectExtensionDelegate(
    private val extension: KotlinProjectExtension,
) {
    fun explicitApi() {
        extension.explicitApi()
    }

    fun jvmToolchain(action: Action<JavaToolchainSpec>) {
        extension.jvmToolchain(action)
    }

    abstract fun compilerOptions(configure: KotlinCommonCompilerOptions.() -> Unit)

    internal companion object {
        fun Project.kotlinProjectExtensionDelegate(): KotlinProjectExtensionDelegate {
            return when (val extension = extensions.getByName("kotlin")) {
                is KotlinJvmProjectExtension -> KotlinJvmProjectExtensionDelegate(extension)
                is KotlinAndroidProjectExtension -> KotlinAndroidProjectExtensionDelegate(extension)
                is KotlinMultiplatformExtension -> KotlinMultiplatformProjectExtensionDelegate(this, extension)
                else -> throw IllegalStateException("Unsupported kotlint extension ${extension::class}")
            }
        }
    }
}

internal class KotlinAndroidProjectExtensionDelegate(
    private val extension: KotlinAndroidProjectExtension,
) : KotlinProjectExtensionDelegate(extension) {
    override fun compilerOptions(configure: KotlinCommonCompilerOptions.() -> Unit) {
        extension.compilerOptions(configure)
    }
}

internal class KotlinJvmProjectExtensionDelegate(
    private val extension: KotlinJvmProjectExtension,
) : KotlinProjectExtensionDelegate(extension) {
    override fun compilerOptions(configure: KotlinCommonCompilerOptions.() -> Unit) {
        extension.compilerOptions(configure)
    }
}
internal class KotlinMultiplatformProjectExtensionDelegate(
    private val project: Project,
    extension: KotlinMultiplatformExtension,
) : KotlinProjectExtensionDelegate(extension) {

    override fun compilerOptions(configure: KotlinCommonCompilerOptions.() -> Unit) {
        project.tasks.withType(KotlinCompilationTask::class.java).configureEach {
            it.compilerOptions(configure)
        }
    }
}
