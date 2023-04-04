package com.freeletics.gradle.plugin

import com.freeletics.gradle.setup.configureStandaloneLint
import org.gradle.api.Project

public abstract class FreeleticsJvmExtension(project: Project) : FreeleticsBaseExtension(project) {

    fun useAndroidLint() {
        project.plugins.apply("com.android.lint")

        project.configureStandaloneLint()
    }
}
