package com.freeletics.gradle.monorepo.tasks

import com.freeletics.gradle.monorepo.util.ProjectType
import com.google.common.truth.Truth.assertThat
import org.junit.Test

internal class CheckDependencyRulesTest {

    @Test
    fun `project has supported type and dependency has supported type`() {
        val result = checkDependencyRules(
            ":feature:test:implementation",
            ":core:test:api",
            allowedProjectTypes = listOf(ProjectType.FEATURE_IMPLEMENTATION),
            allowedDependencyProjectTypes = listOf(ProjectType.CORE_API),
        )
        assertThat(result).isEmpty()
    }

    @Test
    fun `project has unsupported type`() {
        val result = checkDependencyRules(
            ":feature:test:implementation",
            ":core:test:api",
            allowedProjectTypes = listOf(ProjectType.DOMAIN_API),
            allowedDependencyProjectTypes = listOf(ProjectType.CORE_API),
        )
        assertThat(result).containsExactly(
            ":feature:test:implementation is a :feature:*:implementation project but the current plugin only " +
                "allows :domain:*:api",
        )
    }

    @Test
    fun `dependency has unsupported type`() {
        val result = checkDependencyRules(
            ":feature:test:implementation",
            ":core:test:api",
            allowedProjectTypes = listOf(ProjectType.FEATURE_IMPLEMENTATION),
            allowedDependencyProjectTypes = listOf(ProjectType.DOMAIN_API),
        )
        assertThat(result).containsExactly(
            ":feature:test:implementation is not allowed to depend on :core:*:api module :core:test:api",
        )
    }

    @Test
    fun `general project is not allowed to depend on app specific dependency`() {
        val result = checkDependencyRules(
            ":feature:test:implementation",
            ":domain-freeletics:test:api",
            allowedProjectTypes = listOf(ProjectType.FEATURE_IMPLEMENTATION),
            allowedDependencyProjectTypes = listOf(ProjectType.DOMAIN_API),
        )
        assertThat(result).containsExactly(
            ":feature:test:implementation is not allowed to depend on freeletics module :domain-freeletics:test:api",
        )
    }

    @Test
    fun `app specific project is not allowed to depend on app specific dependency of other app`() {
        val result = checkDependencyRules(
            ":feature-staedium:test:implementation",
            ":domain-freeletics:test:api",
            allowedProjectTypes = listOf(ProjectType.FEATURE_IMPLEMENTATION),
            allowedDependencyProjectTypes = listOf(ProjectType.DOMAIN_API),
        )
        assertThat(result).containsExactly(
            ":feature-staedium:test:implementation is not allowed to depend on freeletics module " +
                ":domain-freeletics:test:api",
        )
    }

    @Test
    fun `app specific project is allowed to depend on app specific dependency of same app`() {
        val result = checkDependencyRules(
            ":feature-freeletics:test:implementation",
            ":domain-freeletics:test:api",
            allowedProjectTypes = listOf(ProjectType.FEATURE_IMPLEMENTATION),
            allowedDependencyProjectTypes = listOf(ProjectType.DOMAIN_API),
        )
        assertThat(result).isEmpty()
    }
}
