package com.freeletics.gradle.setup

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

    // run the copy task after the test task, finalizedBy will even run if the test task fails
    tasks.withType(Test::class.java).configureEach {
        it.finalizedBy(copyFailures)
    }

    val verify = tasks.named("verifyPaparazzi") {
        it.dependsOn(copyFailures)
    }
    tasks.named("check").configure {
        it.dependsOn(verify)
    }
}
