package com.freeletics.gradle.monorepo.setup

import app.cash.licensee.LicenseeExtension
import app.cash.licensee.SpdxId
import com.freeletics.gradle.monorepo.tasks.UpdateLicensesTask.Companion.registerUpdateLicensesTask
import com.freeletics.gradle.monorepo.tasks.VerifyLicensesTask.Companion.registerVerifyLicensesTask
import org.gradle.api.Project

internal fun Project.configureLicensee() {
    plugins.apply("app.cash.licensee")

    registerUpdateLicensesTask()
    registerVerifyLicensesTask()

    extensions.configure(LicenseeExtension::class.java) {
        it.allow(SpdxId.Apache_20)
        it.allow(SpdxId.BSD_3_Clause)
        it.allow(SpdxId.MIT)
        it.allow(SpdxId.MIT_0)
    }
}
