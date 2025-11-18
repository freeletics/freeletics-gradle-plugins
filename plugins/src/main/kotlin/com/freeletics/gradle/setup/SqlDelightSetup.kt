package com.freeletics.gradle.setup

import app.cash.sqldelight.gradle.SqlDelightExtension
import com.freeletics.gradle.util.defaultPackageName
import com.freeletics.gradle.util.getDependencyOrNull
import org.gradle.api.Project
import org.gradle.api.artifacts.ProjectDependency

internal fun Project.setupSqlDelight(
    name: String,
    dependency: ProjectDependency?,
) {
    plugins.apply("app.cash.sqldelight")

    extensions.configure(SqlDelightExtension::class.java) { sqldelight ->
        sqldelight.databases.create(name) { db ->
            db.packageName.set(defaultPackageName())
            db.deriveSchemaFromMigrations.set(true)
            val dialect = getDependencyOrNull("sqldelight-dialect")
            if (dialect != null) {
                db.dialect(dialect)
            }
            if (dependency != null) {
                db.dependency(dependency)
            }
        }
    }
}
