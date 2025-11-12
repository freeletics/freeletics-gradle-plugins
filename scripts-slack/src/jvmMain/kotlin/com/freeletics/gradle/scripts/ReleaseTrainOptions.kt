
package com.freeletics.gradle.scripts

import com.github.ajalt.clikt.parameters.groups.OptionGroup
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required

public class ReleaseTrainOptions : OptionGroup("Release Train options") {
    public val name: String by option(
        "--app",
        help = "The app this executed for, e.g. `freeletics`",
    ).required()

    public val displayName: String get() = name.replaceFirstChar { it.titlecase() }

    public val versionName: String by option(
        "--version-name",
        help = "The version name of the app",
    ).required()

    public val versionCode: String? by option(
        "--version-code",
        help = "The version code of the app",
    )

    public val branch: String by option(
        "--branch",
        envvar = "GITHUB_REF_NAME",
        help = "The branch of the release train",
    ).required()
}
