package com.freeletics.gradle.util

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class LegacyPackageNameTest {
    @Test
    fun `legacyPackageName for core api`() {
        assertThat(legacyPackageName(":core:foo:api", "com.company"))
            .isEqualTo("com.company.core.foo.api")
    }

    @Test
    fun `legacyPackageName for core implementation`() {
        assertThat(legacyPackageName(":core:foo:implementation", "com.company"))
            .isEqualTo("com.company.core.foo.implementation")
    }

    @Test
    fun `legacyPackageName for core testing`() {
        assertThat(legacyPackageName(":core:foo:testing", "com.company"))
            .isEqualTo("com.company.core.foo.testing")
    }

    @Test
    fun `legacyPackageName for core api with dash`() {
        assertThat(legacyPackageName(":core:foo-bar:api", "com.company"))
            .isEqualTo("com.company.core.foobar.api")
    }

    @Test
    fun `legacyPackageName for core implementation with dash`() {
        assertThat(legacyPackageName(":core:foo-bar:implementation", "com.company"))
            .isEqualTo("com.company.core.foobar.implementation")
    }

    @Test
    fun `legacyPackageName for core testing with dash`() {
        assertThat(legacyPackageName(":core:foo-bar:testing", "com.company"))
            .isEqualTo("com.company.core.foobar.testing")
    }

    @Test
    fun `legacyPackageName for core api with camel case`() {
        assertThat(legacyPackageName(":core:fooBar:api", "com.company"))
            .isEqualTo("com.company.core.fooBar.api")
    }

    @Test
    fun `legacyPackageName for core implementation with camel case`() {
        assertThat(legacyPackageName(":core:fooBar:implementation", "com.company"))
            .isEqualTo("com.company.core.fooBar.implementation")
    }

    @Test
    fun `legacyPackageName for core testing with camel case`() {
        assertThat(legacyPackageName(":core:fooBar:testing", "com.company"))
            .isEqualTo("com.company.core.fooBar.testing")
    }

    @Test
    fun `legacyPackageName for domain api`() {
        assertThat(legacyPackageName(":domain:foo:api", "com.company"))
            .isEqualTo("com.company.domain.foo.api")
    }

    @Test
    fun `legacyPackageName for domain implementation`() {
        assertThat(legacyPackageName(":domain:foo:implementation", "com.company"))
            .isEqualTo("com.company.domain.foo.implementation")
    }

    @Test
    fun `legacyPackageName for domain testing`() {
        assertThat(legacyPackageName(":domain:foo:testing", "com.company"))
            .isEqualTo("com.company.domain.foo.testing")
    }

    @Test
    fun `legacyPackageName for domain api with dash`() {
        assertThat(legacyPackageName(":domain:foo-bar:api", "com.company"))
            .isEqualTo("com.company.domain.foobar.api")
    }

    @Test
    fun `legacyPackageName for domain implementation with dash`() {
        assertThat(legacyPackageName(":domain:foo-bar:implementation", "com.company"))
            .isEqualTo("com.company.domain.foobar.implementation")
    }

    @Test
    fun `legacyPackageName for domain testing with dash`() {
        assertThat(legacyPackageName(":domain:foo-bar:testing", "com.company"))
            .isEqualTo("com.company.domain.foobar.testing")
    }

    @Test
    fun `legacyPackageName for domain api with camel case`() {
        assertThat(legacyPackageName(":domain:fooBar:api", "com.company"))
            .isEqualTo("com.company.domain.fooBar.api")
    }

    @Test
    fun `legacyPackageName for domain implementation with camel case`() {
        assertThat(legacyPackageName(":domain:fooBar:implementation", "com.company"))
            .isEqualTo("com.company.domain.fooBar.implementation")
    }

    @Test
    fun `legacyPackageName for domain testing with camel case`() {
        assertThat(legacyPackageName(":domain:fooBar:testing", "com.company"))
            .isEqualTo("com.company.domain.fooBar.testing")
    }

    @Test
    fun `legacyPackageName for domain-product api`() {
        assertThat(legacyPackageName(":domain-product:foo:api", "com.product"))
            .isEqualTo("com.product.domain.foo.api")
    }

    @Test
    fun `legacyPackageName for domain-product implementation`() {
        assertThat(legacyPackageName(":domain-product:foo:implementation", "com.product"))
            .isEqualTo("com.product.domain.foo.implementation")
    }

    @Test
    fun `legacyPackageName for domain-product testing`() {
        assertThat(legacyPackageName(":domain-product:foo:testing", "com.product"))
            .isEqualTo("com.product.domain.foo.testing")
    }

    @Test
    fun `legacyPackageName for domain-product api with dash`() {
        assertThat(legacyPackageName(":domain-product:foo-bar:api", "com.product"))
            .isEqualTo("com.product.domain.foobar.api")
    }

    @Test
    fun `legacyPackageName for domain-product implementation with dash`() {
        assertThat(legacyPackageName(":domain-product:foo-bar:implementation", "com.product"))
            .isEqualTo("com.product.domain.foobar.implementation")
    }

    @Test
    fun `legacyPackageName for domain-product testing with dash`() {
        assertThat(legacyPackageName(":domain-product:foo-bar:testing", "com.product"))
            .isEqualTo("com.product.domain.foobar.testing")
    }

    @Test
    fun `legacyPackageName for domain-product api with camel case`() {
        assertThat(legacyPackageName(":domain-product:foo:api", "com.product"))
            .isEqualTo("com.product.domain.foo.api")
    }

    @Test
    fun `legacyPackageName for domain-product implementation with camel case`() {
        assertThat(legacyPackageName(":domain-product:foo:implementation", "com.product"))
            .isEqualTo("com.product.domain.foo.implementation")
    }

    @Test
    fun `legacyPackageName for domain-product testing with camel case`() {
        assertThat(legacyPackageName(":domain-product:foo:testing", "com.product"))
            .isEqualTo("com.product.domain.foo.testing")
    }

    @Test
    fun `legacyPackageName for feature nav`() {
        assertThat(legacyPackageName(":feature:foo:nav", "com.company"))
            .isEqualTo("com.company.feature.foo.nav")
    }

    @Test
    fun `legacyPackageName for feature implementation`() {
        assertThat(legacyPackageName(":feature:foo:implementation", "com.company"))
            .isEqualTo("com.company.feature.foo.implementation")
    }

    @Test
    fun `legacyPackageName for feature nav with dash`() {
        assertThat(legacyPackageName(":feature:foo-bar:nav", "com.company"))
            .isEqualTo("com.company.feature.foobar.nav")
    }

    @Test
    fun `legacyPackageName for feature implementation with dash`() {
        assertThat(legacyPackageName(":feature:foo-bar:implementation", "com.company"))
            .isEqualTo("com.company.feature.foobar.implementation")
    }

    @Test
    fun `legacyPackageName for feature nav with camel case`() {
        assertThat(legacyPackageName(":feature:fooBar:nav", "com.company"))
            .isEqualTo("com.company.feature.fooBar.nav")
    }

    @Test
    fun `legacyPackageName for feature implementation with camel case`() {
        assertThat(legacyPackageName(":feature:fooBar:implementation", "com.company"))
            .isEqualTo("com.company.feature.fooBar.implementation")
    }

    @Test
    fun `legacyPackageName for feature-product nav`() {
        assertThat(legacyPackageName(":feature-product:foo:nav", "com.product"))
            .isEqualTo("com.product.feature.foo.nav")
    }

    @Test
    fun `legacyPackageName for feature-product implementation`() {
        assertThat(legacyPackageName(":feature-product:foo:implementation", "com.product"))
            .isEqualTo("com.product.feature.foo.implementation")
    }

    @Test
    fun `legacyPackageName for feature-product nav with dash`() {
        assertThat(legacyPackageName(":feature-product:foo-bar:nav", "com.product"))
            .isEqualTo("com.product.feature.foobar.nav")
    }

    @Test
    fun `legacyPackageName for feature-product implementation with dash`() {
        assertThat(legacyPackageName(":feature-product:foo-bar:implementation", "com.product"))
            .isEqualTo("com.product.feature.foobar.implementation")
    }

    @Test
    fun `legacyPackageName for feature-product nav with camel case`() {
        assertThat(legacyPackageName(":feature-product:fooBar:nav", "com.product"))
            .isEqualTo("com.product.feature.fooBar.nav")
    }

    @Test
    fun `legacyPackageName for feature-product implementation with camel case`() {
        assertThat(legacyPackageName(":feature-product:fooBar:implementation", "com.product"))
            .isEqualTo("com.product.feature.fooBar.implementation")
    }

    @Test
    fun `legacyPackageName for app`() {
        assertThat(legacyPackageName(":app:product", "com.product"))
            .isEqualTo("com.product.app.product")
    }

    @Test
    fun `legacyPackageName for app-platform`() {
        assertThat(legacyPackageName(":app:product-android", "com.product"))
            .isEqualTo("com.product.app.productandroid")
    }

    @Test
    fun `legacyPackageName for app with camel case`() {
        assertThat(legacyPackageName(":app:productName", "com.product"))
            .isEqualTo("com.product.app.productName")
    }

    @Test
    fun `legacyPackageName for app-platform with camel case`() {
        assertThat(legacyPackageName(":app:productName-android", "com.product"))
            .isEqualTo("com.product.app.productNameandroid")
    }
}
