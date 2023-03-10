package com.freeletics.gradle.setup

import app.cash.licensee.LicenseeExtension
import com.freeletics.gradle.tasks.UpdateLicensesTask.Companion.registerUpdateLicensesTask
import org.gradle.api.Project

internal fun Project.configureLicensee() {
    plugins.apply("app.cash.licensee")

    registerUpdateLicensesTask()

    extensions.configure(LicenseeExtension::class.java) {
        it.allow("Apache-2.0")
        it.allow("MIT")
        it.allow("MIT-0")
    }
}
