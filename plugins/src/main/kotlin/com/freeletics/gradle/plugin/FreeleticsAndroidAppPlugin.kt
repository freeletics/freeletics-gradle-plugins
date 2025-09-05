package com.freeletics.gradle.plugin

import com.freeletics.gradle.util.androidApp
import com.freeletics.gradle.util.getVersion
import org.gradle.api.Plugin
import org.gradle.api.Project

public abstract class FreeleticsAndroidAppPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.plugins.apply("com.android.application")
        target.plugins.apply(FreeleticsAndroidPlugin::class.java)

        target.androidApp {
            defaultConfig.targetSdk = target.getVersion("android.target").toInt()
        }
    }
}
