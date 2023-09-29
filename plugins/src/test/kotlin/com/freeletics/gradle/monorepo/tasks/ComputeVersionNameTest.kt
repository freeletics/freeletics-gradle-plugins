package com.freeletics.gradle.monorepo.tasks

import com.freeletics.gradle.monorepo.util.FakeGit
import com.google.common.truth.Truth.assertThat
import org.junit.Assert
import org.junit.Test

internal class ComputeVersionNameTest {

    @Test
    fun `when there is a matching tag`() {
        val git = FakeGit(
            branch = "main",
            describeNonExact = "fl/v4.5.6",
        )

        assertThat(computeVersionName(git, "fl")).isEqualTo("4.5.6")
    }

    @Test
    fun `when there is a matching tag in the past`() {
        val git = FakeGit(
            branch = "main",
            describeNonExact = "fl/v4.5.6-23-abcdefghij",
        )

        assertThat(computeVersionName(git, "fl")).isEqualTo("4.5.6-23-abcdefghij")
    }

    @Test
    fun `when there are no tags`() {
        val git = FakeGit(
            branch = "main",
            describeNonExact = "",
        )

        val exception = Assert.assertThrows(IllegalStateException::class.java) {
            computeVersionName(git, "fl")
        }
        assertThat(exception).hasMessageThat().isEqualTo(
            "Did not find a previous release/tag",
        )
    }
}
