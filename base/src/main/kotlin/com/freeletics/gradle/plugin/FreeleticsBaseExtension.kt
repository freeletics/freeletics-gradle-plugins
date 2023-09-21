package com.freeletics.gradle.plugin

import com.freeletics.gradle.setup.configureDagger
import com.freeletics.gradle.setup.configureMoshi
import com.freeletics.gradle.util.kotlin
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware

public abstract class FreeleticsBaseExtension(private val project: Project) : ExtensionAware {

    public fun explicitApi() {
        project.kotlin {
            explicitApi()
        }
    }

    public fun optIn(vararg classes: String) {
        project.kotlin {
            compilerOptions {
                optIn.addAll(*classes)
            }
        }
    }

    @JvmOverloads
    public fun useMoshi(sealed: Boolean = false) {
        project.configureMoshi(sealed)
    }

    public fun useDagger() {
        project.configureDagger(DaggerMode.ANVIL_ONLY)
    }

    public fun useDaggerWithKhonshu() {
        project.configureDagger(DaggerMode.ANVIL_WITH_KHONSHU)
    }

    public fun useDaggerWithComponent() {
        project.configureDagger(DaggerMode.ANVIL_WITH_FULL_DAGGER)
    }

    internal enum class DaggerMode {
        ANVIL_ONLY,
        ANVIL_WITH_KHONSHU,
        ANVIL_WITH_FULL_DAGGER,
    }
}