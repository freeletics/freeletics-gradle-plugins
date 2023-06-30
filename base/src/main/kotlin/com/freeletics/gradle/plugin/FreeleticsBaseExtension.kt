package com.freeletics.gradle.plugin

import com.freeletics.gradle.setup.configureDagger
import com.freeletics.gradle.setup.configureMoshi
import com.freeletics.gradle.util.compilerOptions
import com.freeletics.gradle.util.kotlin
import org.gradle.api.Project

public abstract class FreeleticsBaseExtension(protected val project: Project) {

    fun explicitApi() {
        project.kotlin {
            explicitApi()

            // TODO for Android projects the above isn't enough https://youtrack.jetbrains.com/issue/KT-37652
            compilerOptions(project) {
                freeCompilerArgs.add("-Xexplicit-api=strict")
            }
        }
    }

    fun optIn(vararg optIn: String) {
        project.kotlin {
            compilerOptions(project) {
                freeCompilerArgs.addAll(optIn.map { "-opt-in=$it" })
            }
        }
    }

    @JvmOverloads
    fun useMoshi(sealed: Boolean = false) {
        project.configureMoshi(sealed)
    }

    fun useDagger() {
        project.configureDagger(DaggerMode.ANVIL_ONLY)
    }

    fun useDaggerWithKhonshu() {
        project.configureDagger(DaggerMode.ANVIL_WITH_KHONSHU)
    }

    fun useDaggerWithComponent() {
        project.configureDagger(DaggerMode.ANVIL_WITH_FULL_DAGGER)
    }

    enum class DaggerMode {
        ANVIL_ONLY,
        ANVIL_WITH_KHONSHU,
        ANVIL_WITH_FULL_DAGGER,
    }
}
