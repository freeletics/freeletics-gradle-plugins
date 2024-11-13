package com.freeletics.gradle.monorepo.tasks

import com.freeletics.gradle.monorepo.util.FakeGit
import com.google.common.truth.Truth.assertThat
import java.time.LocalDate
import java.time.LocalDateTime
import org.junit.Assert.assertThrows
import org.junit.Test

internal class ComputeVersionCodeTest {
    private val defaultDate = LocalDateTime.now()

    @Test
    fun `when there is a matching tag it returns version code computed from it`() {
        val git = FakeGit(
            branch = "main",
            describe = "fl/v24.3.1",
        )

        assertThat(computeVersionCode(git, "fl", defaultDate)).isEqualTo(24003001)
    }

    @Test
    fun `when there is a matching tag and the major version is too high it fails`() {
        val git = FakeGit(
            branch = "main",
            describe = "fl/v100.3.1",
        )

        val exception = assertThrows(IllegalStateException::class.java) {
            computeVersionCode(git, "fl", defaultDate)
        }
        assertThat(exception).hasMessageThat().isEqualTo("Major version is limited to 99, was 100")
    }

    @Test
    fun `when there is a matching tag and the minor version is too high it fails`() {
        val git = FakeGit(
            branch = "main",
            describe = "fl/v4.100.1",
        )

        val exception = assertThrows(IllegalStateException::class.java) {
            computeVersionCode(git, "fl", defaultDate)
        }
        assertThat(exception).hasMessageThat().isEqualTo("Minor version is limited to 99, was 100")
    }

    @Test
    fun `when there is a matching tag and the patch version is too high it fails`() {
        val git = FakeGit(
            branch = "main",
            describe = "fl/v4.3.100",
        )

        val exception = assertThrows(IllegalStateException::class.java) {
            computeVersionCode(git, "fl", defaultDate)
        }
        assertThat(exception).hasMessageThat().isEqualTo("Patch is limited to 99, was 100")
    }

    @Test
    fun `when there is no tag and branch is not main it fails`() {
        val date = LocalDate.of(2022, 11, 4).atStartOfDay() // friday
        val git = FakeGit(
            branch = "test",
            describe = "",
        )

        val exception = assertThrows(IllegalStateException::class.java) {
            computeVersionCode(git, "fl", date)
        }
        assertThat(exception).hasMessageThat().isEqualTo(
            "Version code can only be computed on the main branch and tagged commits",
        )
    }

    @Test
    fun `when there is no tag it returns version computed from last tag and current day - friday`() {
        val date = LocalDate.of(2022, 11, 11).atStartOfDay() // friday
        val git = FakeGit(
            branch = "main",
            describe = "",
            describeNonExact = "fl/v24.44.3-45-abcdefgh",
        )

        assertThat(computeVersionCode(git, "fl", date)).isEqualTo(24044500)
    }

    @Test
    fun `when there is no tag it returns version computed from last tag and current day - friday evening`() {
        val date = LocalDate.of(2022, 11, 11).atTime(19, 35) // friday
        val git = FakeGit(
            branch = "main",
            describe = "",
            describeNonExact = "fl/v24.44.3-45-abcdefgh",
        )

        assertThat(computeVersionCode(git, "fl", date)).isEqualTo(24044578)
    }

    @Test
    fun `when there is no tag it returns version computed from last tag and current day - tuesday`() {
        val date = LocalDate.of(2022, 11, 8).atStartOfDay() // tuesday
        val git = FakeGit(
            branch = "main",
            describe = "",
            describeNonExact = "fl/v24.44.3-45-abcdefgh",
        )

        assertThat(computeVersionCode(git, "fl", date)).isEqualTo(24044200)
    }

    @Test
    fun `when there is no tag it returns version computed from last tag and current day - tuesday at noon`() {
        val date = LocalDate.of(2022, 11, 8).atTime(12, 0) // tuesday
        val git = FakeGit(
            branch = "main",
            describe = "",
            describeNonExact = "fl/v24.44.3-45-abcdefgh",
        )

        assertThat(computeVersionCode(git, "fl", date)).isEqualTo(24044248)
    }

    @Test
    fun `when there is no tag with extra commits it returns version from last tag and day of week`() {
        val date = LocalDate.of(2022, 11, 11).atStartOfDay() // friday
        val git = FakeGit(
            branch = "main",
            describe = "",
            describeNonExact = "fl/v24.44.3-0-abcdefgh",
        )

        assertThat(computeVersionCode(git, "fl", date)).isEqualTo(24044500)
    }

    @Test
    fun `when there is no tag and the major version is too high it fails`() {
        val git = FakeGit(
            branch = "main",
            describe = "",
            describeNonExact = "fl/v100.44.3-0-abcdefgh",
        )

        val exception = assertThrows(IllegalStateException::class.java) {
            computeVersionCode(git, "fl", defaultDate)
        }
        assertThat(exception).hasMessageThat().isEqualTo("Major version is limited to 99, was 100")
    }

    @Test
    fun `when there is no tag and the minor version is too high it fails`() {
        val git = FakeGit(
            branch = "main",
            describe = "",
            describeNonExact = "fl/v22.100.3-0-abcdefgh",
        )

        val exception = assertThrows(IllegalStateException::class.java) {
            computeVersionCode(git, "fl", defaultDate)
        }
        assertThat(exception).hasMessageThat().isEqualTo("Minor version is limited to 99, was 100")
    }

    @Test
    fun `when there are no tags at all it fails`() {
        val date = LocalDate.of(2022, 11, 11).atStartOfDay() // friday
        val git = FakeGit(
            branch = "main",
            describe = "",
            describeNonExact = "",
        )

        val exception = assertThrows(IllegalStateException::class.java) {
            computeVersionCode(git, "fl", date)
        }
        assertThat(exception).hasMessageThat().isEqualTo(
            "Did not find a previous release/tag",
        )
    }
}
