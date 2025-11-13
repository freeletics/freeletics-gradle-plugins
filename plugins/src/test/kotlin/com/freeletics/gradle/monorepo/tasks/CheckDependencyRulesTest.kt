package com.freeletics.gradle.monorepo.tasks

import com.freeletics.gradle.monorepo.util.ProjectType
import com.google.common.truth.Truth.assertThat
import org.junit.Test

internal class CheckDependencyRulesTest {
    @Test
    fun `project has supported type and dependency has supported type`() {
        val result = checkDependencyRules(
            ":feature:test:implementation",
            "releaseRuntimeClasspath",
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
            "releaseRuntimeClasspath",
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
            "releaseRuntimeClasspath",
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
            "releaseRuntimeClasspath",
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
            "releaseRuntimeClasspath",
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
            "releaseRuntimeClasspath",
            ":domain-freeletics:test:api",
            allowedProjectTypes = listOf(ProjectType.FEATURE_IMPLEMENTATION),
            allowedDependencyProjectTypes = listOf(ProjectType.DOMAIN_API),
        )
        assertThat(result).isEmpty()
    }

    @Test
    fun `debug module is allowed to depend on debug module in any configuration`() {
        val result = checkDependencyRules(
            ":feature-freeletics:test:debug",
            "releaseRuntimeClasspath",
            ":domain-freeletics:test:debug",
            allowedProjectTypes = listOf(ProjectType.FEATURE_DEBUG),
            allowedDependencyProjectTypes = listOf(ProjectType.DOMAIN_DEBUG),
        )
        assertThat(result).isEmpty()
    }

    @Test
    fun `non debug module is not allowed to depend on debug module in any configuration`() {
        val result = checkDependencyRules(
            ":feature-freeletics:test:implementation",
            "releaseRuntimeClasspath",
            ":domain-freeletics:test:debug",
            allowedProjectTypes = listOf(ProjectType.FEATURE_IMPLEMENTATION),
            allowedDependencyProjectTypes = listOf(ProjectType.DOMAIN_DEBUG),
        )
        assertThat(result).containsExactly(
            ":feature-freeletics:test:implementation is not allowed to depend on debug module " +
                ":domain-freeletics:test:debug in configuration releaseRuntimeClasspath",
        )
    }

    @Test
    fun `non debug module is allowed to depend on debug module if configuration is debug`() {
        val result = checkDependencyRules(
            ":feature-freeletics:test:implementation",
            "debugRuntimeClasspath",
            ":domain-freeletics:test:debug",
            allowedProjectTypes = listOf(ProjectType.FEATURE_IMPLEMENTATION),
            allowedDependencyProjectTypes = listOf(ProjectType.DOMAIN_DEBUG),
        )
        assertThat(result).isEmpty()
    }

    @Test
    fun `non debug module is allowed to depend on debug module if configuration is test`() {
        val result = checkDependencyRules(
            ":feature-freeletics:test:implementation",
            "testRuntimeClasspath",
            ":domain-freeletics:test:debug",
            allowedProjectTypes = listOf(ProjectType.FEATURE_IMPLEMENTATION),
            allowedDependencyProjectTypes = listOf(ProjectType.DOMAIN_DEBUG),
        )
        assertThat(result).isEmpty()
    }

    @Test
    fun `testing module is allowed to depend on debug module in any configuration`() {
        val result = checkDependencyRules(
            ":domain-freeletics:test:testing",
            "releaseRuntimeClasspath",
            ":domain-freeletics:test:debug",
            allowedProjectTypes = listOf(ProjectType.DOMAIN_TESTING),
            allowedDependencyProjectTypes = listOf(ProjectType.DOMAIN_DEBUG),
        )
        assertThat(result).isEmpty()
    }

    @Test
    fun `testing module is allowed to depend on testing module in any configuration`() {
        val result = checkDependencyRules(
            ":domain-freeletics:test1:testing",
            "releaseRuntimeClasspath",
            ":domain-freeletics:test2:testing",
            allowedProjectTypes = listOf(ProjectType.DOMAIN_TESTING),
            allowedDependencyProjectTypes = listOf(ProjectType.DOMAIN_TESTING),
        )
        assertThat(result).isEmpty()
    }

    @Test
    fun `non testing module is not allowed to depend on testing module`() {
        val result = checkDependencyRules(
            ":feature-freeletics:test:implementation",
            "releaseRuntimeClasspath",
            ":domain-freeletics:test:testing",
            allowedProjectTypes = listOf(ProjectType.FEATURE_IMPLEMENTATION),
            allowedDependencyProjectTypes = listOf(ProjectType.DOMAIN_TESTING),
        )
        assertThat(result).containsExactly(
            ":feature-freeletics:test:implementation is not allowed to depend on testing module " +
                ":domain-freeletics:test:testing in configuration releaseRuntimeClasspath",
        )
    }

    @Test
    fun `non testing module is allowed to depend on testing module if configuration is test`() {
        val result = checkDependencyRules(
            ":feature-freeletics:test:implementation",
            "testRuntimeClasspath",
            ":domain-freeletics:test:testing",
            allowedProjectTypes = listOf(ProjectType.FEATURE_IMPLEMENTATION),
            allowedDependencyProjectTypes = listOf(ProjectType.DOMAIN_TESTING),
        )
        assertThat(result).isEmpty()
    }
}
