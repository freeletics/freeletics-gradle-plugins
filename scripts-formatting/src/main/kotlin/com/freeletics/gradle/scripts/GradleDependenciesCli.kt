package com.freeletics.gradle.scripts

import com.github.ajalt.clikt.core.Context
import java.nio.file.PathMatcher

public class GradleDependenciesCli : FormattingCli() {
    override fun createPathMatcher(): PathMatcher = gradleMatcher

    override fun createFormatter(): Formatter = GradleDependenciesFormatter()

    override fun help(context: Context): String = "CLI that runs gradle-dependency-sorter."
}
