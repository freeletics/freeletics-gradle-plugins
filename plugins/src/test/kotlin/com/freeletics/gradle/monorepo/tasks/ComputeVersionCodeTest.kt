package com.freeletics.gradle.monorepo.tasks

import com.freeletics.gradle.monorepo.util.FakeGit
import com.google.common.truth.Truth.assertThat
import java.time.LocalDate
import org.junit.Assert.assertThrows
import org.junit.Test

internal class ComputeVersionCodeTest {

    @Test
    fun `when there is a matching tag it returns version code computed from it`() {
        val git = FakeGit(
            branch = "main",
            describe = "fl/v4.3.1",
        )

        assertThat(computeVersionCode(git, "fl", LocalDate.now())).isEqualTo(4030001)
    }

    @Test
    fun `when there is a matching tag and the major version is too high it fails`() {
        val git = FakeGit(
            branch = "main",
            describe = "fl/v100.3.1",
        )

        val exception = assertThrows(IllegalStateException::class.java) {
            computeVersionCode(git, "fl", LocalDate.now())
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
            computeVersionCode(git, "fl", LocalDate.now())
        }
        assertThat(exception).hasMessageThat().isEqualTo("Minor version is limited to 99, was 100")
    }

    @Test
    fun `when there is a matching tag and the patch version is too high it fails`() {
        val git = FakeGit(
            branch = "main",
            describe = "fl/v4.3.1000",
        )

        val exception = assertThrows(IllegalStateException::class.java) {
            computeVersionCode(git, "fl", LocalDate.now())
        }
        assertThat(exception).hasMessageThat().isEqualTo("Patch is limited to 999, was 1000")
    }

    @Test
    fun `when there is no tag and branch is not main it fails`() {
        val date = LocalDate.of(2022, 11, 4) // friday
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
    fun `when there is no tag it returns version computed from last tag, the commit count and day of week - friday`() {
        val date = LocalDate.of(2022, 11, 11) // friday
        val git = FakeGit(
            branch = "main",
            describe = "",
            describeNonExact = "fl/v22.44.3-45-abcdefgh",
        )

        assertThat(computeVersionCode(git, "fl", date)).isEqualTo(22445045)
    }

    @Test
    fun `when there is no tag it returns version computed from last tag, the commit count and day of week - tuesday`() {
        val date = LocalDate.of(2022, 11, 8) // tuesday
        val git = FakeGit(
            branch = "main",
            describe = "",
            describeNonExact = "fl/v22.44.3-45-abcdefgh",
        )

        assertThat(computeVersionCode(git, "fl", date)).isEqualTo(22442045)
    }

    @Test
    fun `when there is no tag with extra commits it returns version from last tag and day of week`() {
        val date = LocalDate.of(2022, 11, 11) // friday
        val git = FakeGit(
            branch = "main",
            describe = "",
            describeNonExact = "fl/v22.44.3-0-abcdefgh",
        )

        assertThat(computeVersionCode(git, "fl", date)).isEqualTo(22445000)
    }

    @Test
    fun `when there is no tag and the major version is too high it fails`() {
        val git = FakeGit(
            branch = "main",
            describe = "",
            describeNonExact = "fl/v100.44.3-0-abcdefgh",
        )

        val exception = assertThrows(IllegalStateException::class.java) {
            computeVersionCode(git, "fl", LocalDate.now())
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
            computeVersionCode(git, "fl", LocalDate.now())
        }
        assertThat(exception).hasMessageThat().isEqualTo("Minor version is limited to 99, was 100")
    }

    @Test
    fun `when there is no tag and there are too many extra commits it fails`() {
        val date = LocalDate.of(2022, 11, 11) // friday
        val git = FakeGit(
            branch = "main",
            describe = "",
            describeNonExact = "fl/v22.44.3-1000-abcdefgh",
        )

        val exception = assertThrows(IllegalStateException::class.java) {
            computeVersionCode(git, "fl", date)
        }
        assertThat(exception).hasMessageThat().isEqualTo(
            "More than 999 commits found since the last release was created",
        )
    }

    @Test
    fun `when there are no tags at all it fails`() {
        val date = LocalDate.of(2022, 11, 11) // friday
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
