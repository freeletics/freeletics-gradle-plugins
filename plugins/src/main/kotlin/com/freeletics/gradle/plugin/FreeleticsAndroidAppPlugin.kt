package com.freeletics.gradle.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project

public abstract class FreeleticsAndroidAppPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.plugins.apply("com.android.application")
        target.plugins.apply(FreeleticsAndroidPlugin::class.java)
    }
}
