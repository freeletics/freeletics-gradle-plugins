package com.freeletics.gradle.monorepo.util

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class AppTypeTest {
    @Test
    fun `AppType for app module`() {
        assertThat(":app:freeletics".toAppType()).isEqualTo(AppType("freeletics"))
    }

    @Test
    fun `AppType for app module with dash`() {
        assertThat(":app:movement-tracking".toAppType()).isEqualTo(AppType("movement-tracking"))
    }

    @Test
    fun `AppType for app module with platform suffix`() {
        assertThat(":app:freeletics-android".toAppType()).isEqualTo(AppType("freeletics"))
    }

    @Test
    fun `AppType for app module with dash and platform suffix`() {
        assertThat(":app:movement-tracking-android".toAppType()).isEqualTo(AppType("movement-tracking"))
    }

    @Test
    fun `AppType for generic feature module`() {
        assertThat(":feature:foo:bar".toAppType()).isEqualTo(null)
    }

    @Test
    fun `AppType for feature module`() {
        assertThat(":feature-freeletics:foo:bar".toAppType()).isEqualTo(AppType("freeletics"))
    }

    @Test
    fun `AppType for feature module with dash`() {
        assertThat(":feature-movement-tracking:foo:bar".toAppType()).isEqualTo(AppType("movement-tracking"))
    }

    @Test
    fun `AppType for generic domain module`() {
        assertThat(":domain:foo:bar".toAppType()).isEqualTo(null)
    }

    @Test
    fun `AppType for domain module`() {
        assertThat(":domain-freeletics:foo:bar".toAppType()).isEqualTo(AppType("freeletics"))
    }

    @Test
    fun `AppType for domain module with dash`() {
        assertThat(":domain-movement-tracking:foo:bar".toAppType()).isEqualTo(AppType("movement-tracking"))
    }

    @Test
    fun `AppType for core module`() {
        assertThat(":core:foo:bar".toAppType()).isEqualTo(null)
    }
}
