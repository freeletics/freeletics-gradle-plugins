package com.freeletics.gradle.scripts

import java.nio.file.Path

public interface Formatter {
    public fun format(path: Path): FormattingResult
}
