package com.freeletics.gradle.scripts

import com.squareup.sort.Sorter
import java.nio.file.Path
import kotlin.io.path.writeText

internal class GradleDependenciesFormatter : Formatter {
    override fun format(path: Path): FormattingResult {
        val code = Sorter.of(path, Sorter.Config(insertBlankLines = true))
        return if (code.hasParseErrors()) {
            FormattingResult.Error("Unexpected error formatting $path: ${code.getParseError()!!.message}")
        } else if (code.isSorted()) {
            return FormattingResult.AlreadyFormatted
        } else {
            path.writeText(code.rewritten())
            return FormattingResult.Formatted
        }
    }
}
