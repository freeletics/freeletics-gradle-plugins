package com.freeletics.gradle.scripts

import com.github.ajalt.clikt.parameters.groups.OptionGroup
import com.github.ajalt.clikt.parameters.options.convert
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import java.util.Base64
import kotlin.time.Duration

public class S3Options : OptionGroup("AWS S3 options") {
    public val region: String by option(
        "--s3-region",
        envvar = "AWS_REGION",
        help = "The region that the bucket is in",
    ).required()

    public val bucket: String by option(
        "--s3-bucket",
        help = "The bucket to upload to",
    ).required()

    public val key: String by option(
        "--s3-key",
        help = "The key of the uploaded object",
    ).required()

    public val validFor: Duration by option(
        "--valid-for",
        help = "How long the URL is valid for in ISO-8601 Duration format",
    ).convert { Duration.parse(it) }.required()
}
