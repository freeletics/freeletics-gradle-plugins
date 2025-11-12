package com.freeletics.gradle.scripts

import com.facebook.ktfmt.format.Formatter as KtFormatter
import java.nio.file.Path
import kotlin.io.path.readText
import kotlin.io.path.writeText

internal class KtFmtFormatter : Formatter {
    private val options = KtFormatter.KOTLINLANG_FORMAT.copy(maxWidth = 120)

    override fun format(path: Path): FormattingResult {
        val code = path.readText()
        val formatted = try {
            KtFormatter.format(options, code)
        } catch (e: Exception) {
            return FormattingResult.Error("Unexpected error formatting $path: ${e.message}")
        }

        if (formatted == code) {
            return FormattingResult.AlreadyFormatted
        } else {
            path.writeText(formatted)
            return FormattingResult.Formatted
        }
    }
}
