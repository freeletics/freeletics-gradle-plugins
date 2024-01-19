package com.freeletics.gradle.setup

import org.gradle.api.Project
import org.gradle.api.attributes.java.TargetJvmEnvironment
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.testing.Test
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType.Companion.attribute

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

    dependencies.constraints { constraints ->
        constraints.add("testImplementation", "com.google.guava:guava") { constraint ->
            constraint.attributes {
                it.attribute(
                    TargetJvmEnvironment.TARGET_JVM_ENVIRONMENT_ATTRIBUTE,
                    objects.named(TargetJvmEnvironment::class.java, TargetJvmEnvironment.STANDARD_JVM),
                )
            }
            constraint.because("LayoutLib and sdk-common depend on Guava's -jre published variant." +
                "See https://github.com/cashapp/paparazzi/issues/906.")
        }
    }
}
