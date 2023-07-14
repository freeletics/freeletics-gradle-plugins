package com.freeletics.gradle.setup

import com.freeletics.gradle.tasks.ProcessGoogleResourcesTask.Companion.registerProcessGoogleResourcesTask
import com.freeletics.gradle.util.androidApp
import com.freeletics.gradle.util.androidComponentsApp
import com.freeletics.gradle.util.computeInfoFromGit
import com.freeletics.gradle.util.getVersion
import com.google.firebase.crashlytics.buildtools.gradle.CrashlyticsExtension
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware

internal fun Project.configureCrashlytics(uploadNativeSymbols: Boolean) {
    plugins.apply("com.google.firebase.crashlytics")

    androidApp {
        buildTypes {
            named("debug") { debugType ->
                debugType.buildConfigField("boolean", "CRASHLYTICS_ENABLED", "false")
                (debugType as ExtensionAware).extensions.configure(CrashlyticsExtension::class.java) {
                    it.mappingFileUploadEnabled = false
                    it.nativeSymbolUploadEnabled = false
                }
            }

            named("release") { releaseType ->
                val enabled = computeInfoFromGit.get()
                releaseType.buildConfigField("boolean", "CRASHLYTICS_ENABLED", "$enabled")
                (releaseType as ExtensionAware).extensions.configure(CrashlyticsExtension::class.java) {
                    it.mappingFileUploadEnabled = enabled
                    it.nativeSymbolUploadEnabled = enabled && uploadNativeSymbols
                }
            }
        }
    }

    project.dependencies.add("releaseApi", "com.freeletics.gradle:minify-crashlytics:${project.getVersion("fgp")}")

    androidComponentsApp {
        onVariants { variant ->
            // dummy task to make the crashlytics plugin work without the google-services plugin
            registerProcessGoogleResourcesTask(variant)
        }
    }
}
