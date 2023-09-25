package com.freeletics.gradle.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project

public abstract class FreeleticsAndroidPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.plugins.apply(FreeleticsAndroidBasePlugin::class.java)
    }
}
