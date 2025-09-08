package com.freeletics.gradle.monorepo.util

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class ProjectTypeTest {
    @Test
    fun `ProjectType for app module`() {
        assertThat(":app:freeletics".toProjectTypeOrNull()).isEqualTo(ProjectType.APP)
    }

    @Test
    fun `ProjectType for app module with dash`() {
        assertThat(":app:movement-tracking".toProjectTypeOrNull()).isEqualTo(ProjectType.APP)
    }

    @Test
    fun `ProjectType for app module with platform suffix`() {
        assertThat(":app:freeletics-android".toProjectTypeOrNull()).isEqualTo(ProjectType.APP)
    }

    @Test
    fun `ProjectType for app module with dash and platform suffix`() {
        assertThat(":app:movement-tracking-android".toProjectTypeOrNull()).isEqualTo(ProjectType.APP)
    }

    @Test
    fun `ProjectType for generic feature implementation module`() {
        assertThat(":feature:foo:implementation".toProjectTypeOrNull()).isEqualTo(ProjectType.FEATURE_IMPLEMENTATION)
    }

    @Test
    fun `ProjectType for feature implementation module`() {
        assertThat(
            ":feature-freeletics:foo:implementation".toProjectTypeOrNull(),
        ).isEqualTo(ProjectType.FEATURE_IMPLEMENTATION)
    }

    @Test
    fun `ProjectType for feature implementation module with dash`() {
        assertThat(
            ":feature-movement-tracking:foo:implementation".toProjectTypeOrNull(),
        ).isEqualTo(ProjectType.FEATURE_IMPLEMENTATION)
    }

    @Test
    fun `ProjectType for generic feature nav module`() {
        assertThat(":feature:foo:nav".toProjectTypeOrNull()).isEqualTo(ProjectType.FEATURE_NAV)
    }

    @Test
    fun `ProjectType for feature nav module`() {
        assertThat(":feature-freeletics:foo:nav".toProjectTypeOrNull()).isEqualTo(ProjectType.FEATURE_NAV)
    }

    @Test
    fun `ProjectType for feature nav module with dash`() {
        assertThat(":feature-movement-tracking:foo:nav".toProjectTypeOrNull()).isEqualTo(ProjectType.FEATURE_NAV)
    }

    @Test
    fun `ProjectType for generic domain implementation module`() {
        assertThat(":domain:foo:implementation".toProjectTypeOrNull()).isEqualTo(ProjectType.DOMAIN_IMPLEMENTATION)
    }

    @Test
    fun `ProjectType for domain implementation module`() {
        assertThat(
            ":domain-freeletics:foo:implementation".toProjectTypeOrNull(),
        ).isEqualTo(ProjectType.DOMAIN_IMPLEMENTATION)
    }

    @Test
    fun `ProjectType for domain implementation module with dash`() {
        assertThat(
            ":domain-movement-tracking:foo:implementation".toProjectTypeOrNull(),
        ).isEqualTo(ProjectType.DOMAIN_IMPLEMENTATION)
    }

    @Test
    fun `ProjectType for generic domain testing module`() {
        assertThat(":domain:foo:testing".toProjectTypeOrNull()).isEqualTo(ProjectType.DOMAIN_TESTING)
    }

    @Test
    fun `ProjectType for domain testing module`() {
        assertThat(":domain-freeletics:foo:testing".toProjectTypeOrNull()).isEqualTo(ProjectType.DOMAIN_TESTING)
    }

    @Test
    fun `ProjectType for domain testing module with dash`() {
        assertThat(":domain-movement-tracking:foo:testing".toProjectTypeOrNull()).isEqualTo(ProjectType.DOMAIN_TESTING)
    }

    @Test
    fun `ProjectType for generic domain api module`() {
        assertThat(":domain:foo:api".toProjectTypeOrNull()).isEqualTo(ProjectType.DOMAIN_API)
    }

    @Test
    fun `ProjectType for domain api module`() {
        assertThat(":domain-freeletics:foo:api".toProjectTypeOrNull()).isEqualTo(ProjectType.DOMAIN_API)
    }

    @Test
    fun `ProjectType for domain api module with dash`() {
        assertThat(":domain-movement-tracking:foo:api".toProjectTypeOrNull()).isEqualTo(ProjectType.DOMAIN_API)
    }

    @Test
    fun `ProjectType for core implementation module`() {
        assertThat(":core:foo:implementation".toProjectTypeOrNull()).isEqualTo(ProjectType.CORE_IMPLEMENTATION)
    }

    @Test
    fun `ProjectType for core testing module`() {
        assertThat(":core:foo:testing".toProjectTypeOrNull()).isEqualTo(ProjectType.CORE_TESTING)
    }

    @Test
    fun `ProjectType for core api module`() {
        assertThat(":core:foo:api".toProjectTypeOrNull()).isEqualTo(ProjectType.CORE_API)
    }

    @Test
    fun `ProjectType for unknown prefix`() {
        assertThat(":testing:foo:api".toProjectTypeOrNull()).isEqualTo(null)
    }

    @Test
    fun `ProjectType for unknown suffix`() {
        assertThat(":core:foo:bar".toProjectTypeOrNull()).isEqualTo(null)
    }
}
