package com.freeletics.gradle.scripts

import com.github.ajalt.clikt.core.Context
import java.nio.file.PathMatcher

public class KtLintCli : FormattingCli() {
    override fun createPathMatcher(): PathMatcher = kotlinMatcher

    override fun createFormatter(): Formatter = KtLintFormatter()

    override fun help(context: Context): String = "CLI that runs ktlint."
}
