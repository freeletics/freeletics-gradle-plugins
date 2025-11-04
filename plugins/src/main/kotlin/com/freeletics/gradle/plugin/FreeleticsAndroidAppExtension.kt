package com.freeletics.gradle.plugin

import com.freeletics.gradle.util.androidApp
import org.gradle.api.Project

public abstract class FreeleticsAndroidAppExtension(private val project: Project) {
    public fun buildConfigField(type: String, name: String, value: String) {
        project.androidApp {
            defaultConfig.buildConfigField(type, name, value)
        }
    }

    public fun buildConfigField(type: String, name: String, debugValue: String, releaseValue: String) {
        project.androidApp {
            buildTypes.getByName("debug").buildConfigField(type, name, debugValue)
            buildTypes.getByName("release").buildConfigField(type, name, releaseValue)
        }
    }

    public fun resValue(type: String, name: String, value: String) {
        project.androidApp {
            defaultConfig.resValue(type, name, value)
        }
    }

    public fun resValue(type: String, name: String, debugValue: String, releaseValue: String) {
        project.androidApp {
            buildTypes.getByName("debug").resValue(type, name, debugValue)
            buildTypes.getByName("release").resValue(type, name, releaseValue)
        }
    }
}
