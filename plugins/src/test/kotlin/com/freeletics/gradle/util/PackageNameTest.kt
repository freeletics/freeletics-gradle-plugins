package com.freeletics.gradle.util

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class PackageNameTest {
    @Test
    fun `defaultPackageName for core api`() {
        assertThat(defaultPackageName(":core:foo:api", "com.company"))
            .isEqualTo("com.company.foo")
    }

    @Test
    fun `defaultPackageName for core implementation`() {
        assertThat(defaultPackageName(":core:foo:implementation", "com.company"))
            .isEqualTo("com.company.foo.implementation")
    }

    @Test
    fun `defaultPackageName for core testing`() {
        assertThat(defaultPackageName(":core:foo:testing", "com.company"))
            .isEqualTo("com.company.foo.testing")
    }

    @Test
    fun `defaultPackageName for core api with dash`() {
        assertThat(defaultPackageName(":core:foo-bar:api", "com.company"))
            .isEqualTo("com.company.foo.bar")
    }

    @Test
    fun `defaultPackageName for core implementation with dash`() {
        assertThat(defaultPackageName(":core:foo-bar:implementation", "com.company"))
            .isEqualTo("com.company.foo.bar.implementation")
    }

    @Test
    fun `defaultPackageName for core testing with dash`() {
        assertThat(defaultPackageName(":core:foo-bar:testing", "com.company"))
            .isEqualTo("com.company.foo.bar.testing")
    }

    @Test
    fun `defaultPackageName for core api with camel case`() {
        assertThat(defaultPackageName(":core:fooBar:api", "com.company"))
            .isEqualTo("com.company.foobar")
    }

    @Test
    fun `defaultPackageName for core implementation with camel case`() {
        assertThat(defaultPackageName(":core:fooBar:implementation", "com.company"))
            .isEqualTo("com.company.foobar.implementation")
    }

    @Test
    fun `defaultPackageName for core testing with camel case`() {
        assertThat(defaultPackageName(":core:fooBar:testing", "com.company"))
            .isEqualTo("com.company.foobar.testing")
    }

    @Test
    fun `defaultPackageName for domain api`() {
        assertThat(defaultPackageName(":domain:foo:api", "com.company"))
            .isEqualTo("com.company.foo")
    }

    @Test
    fun `defaultPackageName for domain implementation`() {
        assertThat(defaultPackageName(":domain:foo:implementation", "com.company"))
            .isEqualTo("com.company.foo.implementation")
    }

    @Test
    fun `defaultPackageName for domain testing`() {
        assertThat(defaultPackageName(":domain:foo:testing", "com.company"))
            .isEqualTo("com.company.foo.testing")
    }

    @Test
    fun `defaultPackageName for domain api with dash`() {
        assertThat(defaultPackageName(":domain:foo-bar:api", "com.company"))
            .isEqualTo("com.company.foo.bar")
    }

    @Test
    fun `defaultPackageName for domain implementation with dash`() {
        assertThat(defaultPackageName(":domain:foo-bar:implementation", "com.company"))
            .isEqualTo("com.company.foo.bar.implementation")
    }

    @Test
    fun `defaultPackageName for domain testing with dash`() {
        assertThat(defaultPackageName(":domain:foo-bar:testing", "com.company"))
            .isEqualTo("com.company.foo.bar.testing")
    }

    @Test
    fun `defaultPackageName for domain api with camel case`() {
        assertThat(defaultPackageName(":domain:fooBar:api", "com.company"))
            .isEqualTo("com.company.foobar")
    }

    @Test
    fun `defaultPackageName for domain implementation with camel case`() {
        assertThat(defaultPackageName(":domain:fooBar:implementation", "com.company"))
            .isEqualTo("com.company.foobar.implementation")
    }

    @Test
    fun `defaultPackageName for domain testing with camel case`() {
        assertThat(defaultPackageName(":domain:fooBar:testing", "com.company"))
            .isEqualTo("com.company.foobar.testing")
    }

    @Test
    fun `defaultPackageName for domain-product api`() {
        assertThat(defaultPackageName(":domain-product:foo:api", "com.product"))
            .isEqualTo("com.product.foo")
    }

    @Test
    fun `defaultPackageName for domain-product implementation`() {
        assertThat(defaultPackageName(":domain-product:foo:implementation", "com.product"))
            .isEqualTo("com.product.foo.implementation")
    }

    @Test
    fun `defaultPackageName for domain-product testing`() {
        assertThat(defaultPackageName(":domain-product:foo:testing", "com.product"))
            .isEqualTo("com.product.foo.testing")
    }

    @Test
    fun `defaultPackageName for domain-product api with dash`() {
        assertThat(defaultPackageName(":domain-product:foo-bar:api", "com.product"))
            .isEqualTo("com.product.foo.bar")
    }

    @Test
    fun `defaultPackageName for domain-product implementation with dash`() {
        assertThat(defaultPackageName(":domain-product:foo-bar:implementation", "com.product"))
            .isEqualTo("com.product.foo.bar.implementation")
    }

    @Test
    fun `defaultPackageName for domain-product testing with dash`() {
        assertThat(defaultPackageName(":domain-product:foo-bar:testing", "com.product"))
            .isEqualTo("com.product.foo.bar.testing")
    }

    @Test
    fun `defaultPackageName for domain-product api with camel case`() {
        assertThat(defaultPackageName(":domain-product:foo:api", "com.product"))
            .isEqualTo("com.product.foo")
    }

    @Test
    fun `defaultPackageName for domain-product implementation with camel case`() {
        assertThat(defaultPackageName(":domain-product:foo:implementation", "com.product"))
            .isEqualTo("com.product.foo.implementation")
    }

    @Test
    fun `defaultPackageName for domain-product testing with camel case`() {
        assertThat(defaultPackageName(":domain-product:foo:testing", "com.product"))
            .isEqualTo("com.product.foo.testing")
    }

    @Test
    fun `defaultPackageName for feature nav`() {
        assertThat(defaultPackageName(":feature:foo:nav", "com.company"))
            .isEqualTo("com.company.foo.nav")
    }

    @Test
    fun `defaultPackageName for feature implementation`() {
        assertThat(defaultPackageName(":feature:foo:implementation", "com.company"))
            .isEqualTo("com.company.foo")
    }

    @Test
    fun `defaultPackageName for feature nav with dash`() {
        assertThat(defaultPackageName(":feature:foo-bar:nav", "com.company"))
            .isEqualTo("com.company.foo.bar.nav")
    }

    @Test
    fun `defaultPackageName for feature implementation with dash`() {
        assertThat(defaultPackageName(":feature:foo-bar:implementation", "com.company"))
            .isEqualTo("com.company.foo.bar")
    }

    @Test
    fun `defaultPackageName for feature nav with camel case`() {
        assertThat(defaultPackageName(":feature:fooBar:nav", "com.company"))
            .isEqualTo("com.company.foobar.nav")
    }

    @Test
    fun `defaultPackageName for feature implementation with camel case`() {
        assertThat(defaultPackageName(":feature:fooBar:implementation", "com.company"))
            .isEqualTo("com.company.foobar")
    }

    @Test
    fun `defaultPackageName for feature-product nav`() {
        assertThat(defaultPackageName(":feature-product:foo:nav", "com.product"))
            .isEqualTo("com.product.foo.nav")
    }

    @Test
    fun `defaultPackageName for feature-product implementation`() {
        assertThat(defaultPackageName(":feature-product:foo:implementation", "com.product"))
            .isEqualTo("com.product.foo")
    }

    @Test
    fun `defaultPackageName for feature-product nav with dash`() {
        assertThat(defaultPackageName(":feature-product:foo-bar:nav", "com.product"))
            .isEqualTo("com.product.foo.bar.nav")
    }

    @Test
    fun `defaultPackageName for feature-product implementation with dash`() {
        assertThat(defaultPackageName(":feature-product:foo-bar:implementation", "com.product"))
            .isEqualTo("com.product.foo.bar")
    }

    @Test
    fun `defaultPackageName for feature-product nav with camel case`() {
        assertThat(defaultPackageName(":feature-product:fooBar:nav", "com.product"))
            .isEqualTo("com.product.foobar.nav")
    }

    @Test
    fun `defaultPackageName for feature-product implementation with camel case`() {
        assertThat(defaultPackageName(":feature-product:fooBar:implementation", "com.product"))
            .isEqualTo("com.product.foobar")
    }

    @Test
    fun `defaultPackageName for app`() {
        assertThat(defaultPackageName(":app:product", "com.product"))
            .isEqualTo("com.product")
    }

    @Test
    fun `defaultPackageName for app-platform`() {
        assertThat(defaultPackageName(":app:product-android", "com.product"))
            .isEqualTo("com.product.android")
    }

    @Test
    fun `defaultPackageName for app with camel case`() {
        assertThat(defaultPackageName(":app:productName", "com.product"))
            .isEqualTo("com.product")
    }

    @Test
    fun `defaultPackageName for app-platform with camel case`() {
        assertThat(defaultPackageName(":app:productName-android", "com.product"))
            .isEqualTo("com.product.android")
    }

    @Test
    fun `defaultPackageName for non monorepo project`() {
        assertThat(defaultPackageName(":navigation", "com.project"))
            .isEqualTo("com.project.navigation")
    }

    @Test
    fun `defaultPackageName for nested non monorepo project`() {
        assertThat(defaultPackageName(":navigation:testing", "com.project"))
            .isEqualTo("com.project.navigation.testing")
    }

    @Test
    fun `defaultPackageName for non monorepo project with dash`() {
        assertThat(defaultPackageName(":navigation-testing", "com.project"))
            .isEqualTo("com.project.navigation.testing")
    }

    @Test
    fun `defaultPackageName for non monorepo project with camel case`() {
        assertThat(defaultPackageName(":navigationTesting", "com.project"))
            .isEqualTo("com.project.navigationtesting")
    }
}
