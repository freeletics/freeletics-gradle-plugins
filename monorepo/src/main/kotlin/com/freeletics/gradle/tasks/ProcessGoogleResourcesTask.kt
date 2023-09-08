package com.freeletics.gradle.tasks

import com.android.build.api.variant.Variant
import com.freeletics.gradle.util.capitalize
import java.io.File
import org.gradle.api.Project
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.Internal

public abstract class ProcessGoogleResourcesTask : Copy() {
    // the Crashlytics plugin needs this property
    @Internal
    public var intermediateDir: File? = null

    internal companion object {
        fun Project.registerProcessGoogleResourcesTask(variant: Variant) {
            val variantName = variant.name.capitalize()
            val variantResourceRoot = project.layout.buildDirectory.dir(
                "generated/res/google-services/" +
                    "${variant.buildType}/${variant.flavorName}",
            )

            tasks.register("process${variantName}GoogleServices", ProcessGoogleResourcesTask::class.java) { task ->
                task.from("src/${variant.buildType}/res/values/google-services.xml")
                task.into(variantResourceRoot.map { it.dir("values") })
                task.intermediateDir = variantResourceRoot.get().asFile

                task.rename("google-services.xml", "values.xml")
            }
        }
    }
}
