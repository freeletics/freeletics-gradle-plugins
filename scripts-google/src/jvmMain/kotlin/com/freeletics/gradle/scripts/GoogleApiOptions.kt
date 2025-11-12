package com.freeletics.gradle.scripts

import com.github.ajalt.clikt.parameters.groups.OptionGroup
import com.github.ajalt.clikt.parameters.options.convert
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import java.util.Base64

public class GoogleApiOptions : OptionGroup("Google API options") {
    public val jsonKeyData: String by option(
        "--json-key-data",
        envvar = "GOOGLE_PLAY_JSON_KEY_DATA",
        help = "The json key for a Google Play service account. Needs to be provided in base64 encoded form.",
    ).convert { Base64.getDecoder().decode(it).toString(Charsets.UTF_8) }.required()
}
