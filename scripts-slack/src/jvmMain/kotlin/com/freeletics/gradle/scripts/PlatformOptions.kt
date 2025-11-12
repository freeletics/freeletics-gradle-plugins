package com.freeletics.gradle.scripts

import com.github.ajalt.clikt.parameters.groups.OptionGroup
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.enum

public class PlatformOptions : OptionGroup("Platform options") {
    public val platform: Platform by option(
        "--platform",
        help = "The platform this command is executed for",
    ).enum<Platform>().required()
}

public enum class Platform {
    ANDROID,
    IOS,
    ;

    public val value: String
        get() = name.lowercase()
}
