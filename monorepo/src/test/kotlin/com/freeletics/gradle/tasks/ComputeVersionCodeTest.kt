package com.freeletics.gradle.tasks

import com.freeletics.gradle.util.FakeGit
import com.google.common.truth.Truth.assertThat
import java.time.LocalDate
import org.junit.Assert.assertThrows
import org.junit.Test

class ComputeVersionCodeTest {

    @Test
    fun `when on a matching release branch it returns version computed from it`() {
        val git = FakeGit(
            branch = "release/fl/1.2.0",
            describe = "fl/v1.0.0",
            commitsSince = 0
        )

        assertThat(computeVersionCode(git, "fl", LocalDate.now())).isEqualTo(1020000)
    }

    @Test
    fun `when on a matching release branch with extra commits it returns version computed from it`() {
        val git = FakeGit(
            branch = "release/fl/1.2.0",
            describe = "fl/v1.0.0",
            commitsSince = 23
        )

        assertThat(computeVersionCode(git, "fl", LocalDate.now())).isEqualTo(1020023)
    }

    @Test
    fun `when on a matching release branch with too many extra commits it fails`() {
        val git = FakeGit(
            branch = "release/fl/1.2.5",
            describe = "fl/v1.0.0",
            commitsSince = 100
        )

        val exception = assertThrows(IllegalStateException::class.java) {
            computeVersionCode(git, "fl", LocalDate.now())
        }
        assertThat(exception).hasMessageThat().isEqualTo("More than 99 commits found since the release was created")
    }

    @Test
    fun `when on a matching release branch and the major version is too high it fails`() {
        val git = FakeGit(
            branch = "release/fl/100.2.5",
            describe = "fl/v1.0.0",
            commitsSince = 0
        )

        val exception = assertThrows(IllegalStateException::class.java) {
            computeVersionCode(git, "fl", LocalDate.now())
        }
        assertThat(exception).hasMessageThat().isEqualTo("Major version is limited to 99, was 100")
    }

    @Test
    fun `when on a matching release branch and the minor version is too high it fails`() {
        val git = FakeGit(
            branch = "release/fl/1.100.5",
            describe = "fl/v1.0.0",
            commitsSince = 0
        )

        val exception = assertThrows(IllegalStateException::class.java) {
            computeVersionCode(git, "fl", LocalDate.now())
        }
        assertThat(exception).hasMessageThat().isEqualTo("Minor version is limited to 99, was 100")
    }

    @Test
    fun `when on a matching release branch and the patch version is too high it fails`() {
        val git = FakeGit(
            branch = "release/fl/1.2.10",
            describe = "fl/v1.0.0",
            commitsSince = 0
        )

        val exception = assertThrows(IllegalStateException::class.java) {
            computeVersionCode(git, "fl", LocalDate.now())
        }
        assertThat(exception).hasMessageThat().isEqualTo("Patch is limited to 9, was 10")
    }

    @Test
    fun `when on a matching hotfix branch it returns version computed from it`() {
        val git = FakeGit(
            branch = "hotfix/fl/1.2.5",
            describe = "fl/v1.0.0",
            commitsSince = 0
        )

        assertThat(computeVersionCode(git, "fl", LocalDate.now())).isEqualTo(1020500)
    }

    @Test
    fun `when on a matching hotfix branch with extra commits it returns version computed from it`() {
        val git = FakeGit(
            branch = "hotfix/fl/1.2.5",
            describe = "fl/v1.0.0",
            commitsSince = 23
        )

        assertThat(computeVersionCode(git, "fl", LocalDate.now())).isEqualTo(1020523)
    }

    @Test
    fun `when on a matching hotfix branch with too many extra commits it fails`() {
        val git = FakeGit(
            branch = "hotfix/fl/1.2.5",
            describe = "fl/v1.0.0",
            commitsSince = 100
        )

        val exception = assertThrows(IllegalStateException::class.java) {
            computeVersionCode(git, "fl", LocalDate.now())
        }
        assertThat(exception).hasMessageThat().isEqualTo("More than 99 commits found since the release was created")
    }

    @Test
    fun `when on a matching hotifx branch and the major version is too high it fails`() {
        val git = FakeGit(
            branch = "hotfix/fl/100.2.1",
            describe = "fl/v1.0.0",
            commitsSince = 0
        )

        val exception = assertThrows(IllegalStateException::class.java) {
            computeVersionCode(git, "fl", LocalDate.now())
        }
        assertThat(exception).hasMessageThat().isEqualTo("Major version is limited to 99, was 100")
    }

    @Test
    fun `when on a matching hotifx branch and the minor version is too high it fails`() {
        val git = FakeGit(
            branch = "hotfix/fl/1.100.1",
            describe = "fl/v1.0.0",
            commitsSince = 0
        )

        val exception = assertThrows(IllegalStateException::class.java) {
            computeVersionCode(git, "fl", LocalDate.now())
        }
        assertThat(exception).hasMessageThat().isEqualTo("Minor version is limited to 99, was 100")
    }

    @Test
    fun `when on a matching hotifx branch and the patch version is too high it fails`() {
        val git = FakeGit(
            branch = "hotfix/fl/1.2.10",
            describe = "fl/v1.0.0",
            commitsSince = 0
        )

        val exception = assertThrows(IllegalStateException::class.java) {
            computeVersionCode(git, "fl", LocalDate.now())
        }
        assertThat(exception).hasMessageThat().isEqualTo("Patch is limited to 9, was 10")
    }

    @Test
    fun `when there is a matching tag it returns version computed from it`() {
        val git = FakeGit(
            branch = "main",
            describe = "fl/v4.3.1",
            commitsSince = 0
        )

        assertThat(computeVersionCode(git, "fl", LocalDate.now())).isEqualTo(4030100)
    }

    @Test
    fun `when there is a matching tag with extra commits it returns version computed from it`() {
        val git = FakeGit(
            branch = "main",
            describe = "fl/v4.3.1",
            commitsSince = 23
        )

        assertThat(computeVersionCode(git, "fl", LocalDate.now())).isEqualTo(4030123)
    }

    @Test
    fun `when there is a matching tag with too many extra commits it fails`() {
        val git = FakeGit(
            branch = "main",
            describe = "fl/v4.3.1",
            commitsSince = 100
        )

        val exception = assertThrows(IllegalStateException::class.java) {
            computeVersionCode(git, "fl", LocalDate.now())
        }
        assertThat(exception).hasMessageThat().isEqualTo("More than 99 commits found since the release was created")
    }

    @Test
    fun `when there is a matching tag and the major version is too high it fails`() {
        val git = FakeGit(
            branch = "main",
            describe = "fl/v100.3.1",
            commitsSince = 0
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
            commitsSince = 0
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
            describe = "fl/v4.3.10",
            commitsSince = 0
        )

        val exception = assertThrows(IllegalStateException::class.java) {
            computeVersionCode(git, "fl", LocalDate.now())
        }
        assertThat(exception).hasMessageThat().isEqualTo("Patch is limited to 9, was 10")
    }

    @Test
    fun `when there is no release branch or tag and branch is not main it fails`() {
        val date = LocalDate.of(2022, 11, 4) // friday
        val git = FakeGit(
            branch = "test",
            describe = "",
            commitsSince = 0,
        )

        val exception = assertThrows(IllegalStateException::class.java) {
            computeVersionCode(git, "fl", date)
        }
        assertThat(exception).hasMessageThat().isEqualTo("Version code can only be computed on main, release and hotfix branches as well as tags")
    }

    @Test
    fun `when there is no release branch or tag it returns version computed from last week's date`() {
        val date = LocalDate.of(2022, 11, 11) // friday
        val git = FakeGit(
            branch = "main",
            describe = "",
            commitsSince = 0,
        )

        assertThat(computeVersionCode(git, "fl", date)).isEqualTo(22441000)
    }

    @Test
    fun `when there is no release branch or tag with extra commits it returns version from last week's date`() {
        val date = LocalDate.of(2022, 11, 11) // friday
        val git = FakeGit(
            branch = "main",
            describe = "",
            commitsSince = 23,
        )

        assertThat(computeVersionCode(git, "fl", date)).isEqualTo(22441023)
    }

    @Test
    fun `when there is no release branch or tag with too many extra commits it fails`() {
        val date = LocalDate.of(2022, 11, 11) // friday
        val git = FakeGit(
            branch = "main",
            describe = "",
            commitsSince = 9000,
        )

        val exception = assertThrows(IllegalStateException::class.java) {
            computeVersionCode(git, "fl", date)
        }
        assertThat(exception).hasMessageThat().isEqualTo("More than 8999 commits found since the last release was created")
    }

    @Test
    fun `when there is no release branch or tag and year is too high it fails`() {
        val date = LocalDate.of(2100, 11, 11) // friday
        val git = FakeGit(
            branch = "main",
            describe = "",
            commitsSince = 0,
        )

        val exception = assertThrows(IllegalStateException::class.java) {
            computeVersionCode(git, "fl", date)
        }
        assertThat(exception).hasMessageThat().isEqualTo("Major version is limited to 99, was 100")
    }
}
