package com.google.gms.googleservices

import com.android.build.api.variant.ApplicationVariantBuilder
import com.freeletics.gradle.monorepo.util.capitalize
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction

public abstract class GoogleServicesTask : DefaultTask() {
    @get:InputFile
    @get:PathSensitive(PathSensitivity.NONE)
    public abstract val googleServicesConfigFile: RegularFileProperty

    @get:OutputFile
    public abstract val gmpAppId: RegularFileProperty

    @TaskAction
    public fun extractAppId() {
        val regex = Regex("<string.*name=\"google_app_id\".*>([A-z0-9:]+)<\\/string>")
        val googleServicesConfig = googleServicesConfigFile.get().asFile.readText()
        val appIdMatch = regex.find(googleServicesConfig)
        if (appIdMatch != null) {
            gmpAppId.get().asFile.writeText(appIdMatch.groupValues[1])
        } else {
            throw GradleException("google_app_id not found in ${googleServicesConfigFile.get()}")
        }
    }

    internal companion object {
        fun Project.registerProcessGoogleResourcesTask(variant: ApplicationVariantBuilder) {
            val variantName = variant.name.capitalize()
            val googleServicesConfigFile = "src/${variant.buildType}/res/values/google-services.xml"
            val appIdFile = "gmpAppId/${variant.name}.txt"
            tasks.register("process${variantName}GoogleServices", GoogleServicesTask::class.java) { task ->
                task.googleServicesConfigFile.set(project.layout.projectDirectory.file(googleServicesConfigFile))
                task.gmpAppId.set(project.layout.buildDirectory.file(appIdFile))
            }
        }
    }
}
