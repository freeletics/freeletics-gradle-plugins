package com.freeletics.gradle.tasks

import com.freeletics.gradle.util.FakeGit
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class ComputeVersionNameTest {

    @Test
    fun `when not on a release or hotfix branch and nothing is tagged it returns version 0 with git sha`() {
        val git = FakeGit(
            branch = "main",
            describe = "",
        )

        assertThat(computeVersionName(git, "fl")).isEqualTo("0.0.0-abcdefghij")
    }

    @Test
    fun `when on a matching release branch it returns version extracted from it`() {
        val git = FakeGit(
            branch = "release/fl/1.2.3",
            describe = "fl/v1.0.0",
        )

        assertThat(computeVersionName(git, "fl")).isEqualTo("1.2.3")
    }

    @Test
    fun `when on a not matching release branch it returns version from tag`() {
        val git = FakeGit(
            branch = "release/stdm/1.2.3",
            describe = "fl/v1.0.0",
        )

        assertThat(computeVersionName(git, "fl")).isEqualTo("1.0.0")
    }

    @Test
    fun `when on a not matching release branch it returns version 0 with git sha`() {
        val git = FakeGit(
            branch = "release/stdm/1.2.3",
        )

        assertThat(computeVersionName(git, "fl")).isEqualTo("0.0.0-abcdefghij")
    }

    @Test
    fun `when on a matching hotfix branch it returns version extracted from it`() {
        val git = FakeGit(
            branch = "hotfix/fl/1.2.3",
            describe = "fl/v1.0.0",
        )

        assertThat(computeVersionName(git, "fl")).isEqualTo("1.2.3")
    }

    @Test
    fun `when on a not matching hotfix branch it returns version from tag`() {
        val git = FakeGit(
            branch = "hotfix/stdm/1.2.3",
            describe = "fl/v1.0.0",
        )

        assertThat(computeVersionName(git, "fl")).isEqualTo("1.0.0")
    }

    @Test
    fun `when on a not matching hotfix branch it returns version 0 with git sha`() {
        val git = FakeGit(
            branch = "hotfix/stdm/1.2.3",
        )

        assertThat(computeVersionName(git, "fl")).isEqualTo("0.0.0-abcdefghij")
    }

    @Test
    fun `when there is a matching tag`() {
        val git = FakeGit(
            branch = "main",
            describe = "fl/v4.5.6",
        )

        assertThat(computeVersionName(git, "fl")).isEqualTo("4.5.6")
    }

    @Test
    fun `when there is a matching tag in the past`() {
        val git = FakeGit(
            branch = "main",
            describe = "fl/v4.5.6-23-abcdefghij",
        )

        assertThat(computeVersionName(git, "fl")).isEqualTo("4.5.6-23-abcdefghij")
    }
}
