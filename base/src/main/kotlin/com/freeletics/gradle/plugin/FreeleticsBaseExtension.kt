package com.freeletics.gradle.plugin

import com.freeletics.gradle.setup.configureDagger
import com.freeletics.gradle.setup.configureMoshi
import com.freeletics.gradle.util.kotlin
import org.gradle.api.Project

public abstract class FreeleticsBaseExtension(protected val project: Project) {

    fun explicitApi() {
        project.kotlin {
            explicitApi()
        }
    }

    fun optIn(vararg classes: String) {
        project.kotlin {
            compilerOptions {
                optIn.addAll(*classes)
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
