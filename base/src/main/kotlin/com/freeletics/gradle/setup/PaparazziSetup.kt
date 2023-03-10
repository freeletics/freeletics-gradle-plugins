package com.freeletics.gradle.setup

import org.gradle.api.Project

internal fun Project.configurePaparazzi() {
    plugins.apply("com.freeletics.fork.paparazzi")
}
