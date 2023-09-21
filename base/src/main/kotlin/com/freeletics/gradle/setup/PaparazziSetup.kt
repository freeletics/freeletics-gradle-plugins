package com.freeletics.gradle.setup

import app.cash.paparazzi.gradle.PaparazziPlugin
import org.gradle.api.Project
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.testing.Test

internal fun Project.configurePaparazzi() {
    plugins.apply("com.freeletics.fork.paparazzi")

    val copyFailures = tasks.register("copyPaparazziFailures", Copy::class.java) {
        it.from(layout.buildDirectory.dir("paparazzi/failures"))
        it.into(rootProject.layout.buildDirectory.dir("reports/paparazzi"))
        it.include("**/delta-*")
    }

    tasks.withType(Test::class.java).configureEach {
        it.finalizedBy(copyFailures)
    }

    tasks.withType(PaparazziPlugin.PaparazziTask::class.java).configureEach {
        if (it.name.startsWith("verify")) {
            it.dependsOn(copyFailures)
        }
    }
}
