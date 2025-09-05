package com.freeletics.gradle.scripts

public sealed interface FormattingResult {
    public data object AlreadyFormatted : FormattingResult

    public data object Formatted : FormattingResult

    public data class Error(val message: String) : FormattingResult
}
