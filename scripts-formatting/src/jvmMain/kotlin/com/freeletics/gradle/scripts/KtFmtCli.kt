package com.freeletics.gradle.scripts

import com.github.ajalt.clikt.core.Context
import java.nio.file.PathMatcher

public class KtFmtCli : FormattingCli() {
    override fun createPathMatcher(): PathMatcher = kotlinMatcher

    override fun createFormatter(): Formatter = KtFmtFormatter()

    override fun help(context: Context): String = "CLI that runs ktfmt."
}
