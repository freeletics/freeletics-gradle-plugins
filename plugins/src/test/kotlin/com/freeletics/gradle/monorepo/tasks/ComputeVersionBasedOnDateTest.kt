package com.freeletics.gradle.monorepo.tasks

import com.google.common.truth.Truth.assertThat
import java.time.LocalDate
import org.junit.Test

class ComputeVersionBasedOnDateTest {
    @Test
    fun simple() {
        assertThat(versionBasedOnDate(LocalDate.of(2024, 11, 12))).isEqualTo("24.46.0")
    }

    @Test
    fun `end of year`() {
        assertThat(versionBasedOnDate(LocalDate.of(2024, 12, 29))).isEqualTo("24.52.0")
    }

    @Test
    fun `first week of next year, but still in previous year`() {
        assertThat(versionBasedOnDate(LocalDate.of(2024, 12, 30))).isEqualTo("25.1.0")
    }

    @Test
    fun `beginning of year`() {
        assertThat(versionBasedOnDate(LocalDate.of(2025, 1, 1))).isEqualTo("25.1.0")
    }
}
