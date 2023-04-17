package com.freeletics.gradle.scripts

import com.pinterest.ktlint.core.LintError
import java.nio.file.Path

internal data class KtLintError(
    val file: Path,
    val error: LintError,
    val corrected: Boolean
) {
    fun print(prefix: String) {
        println("$prefix ${file}:${error.line} ${error.ruleId} ${error.detail}")
    }
}
