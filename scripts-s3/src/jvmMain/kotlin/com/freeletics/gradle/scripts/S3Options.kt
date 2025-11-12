package com.freeletics.gradle.scripts

import com.github.ajalt.clikt.parameters.groups.OptionGroup
import com.github.ajalt.clikt.parameters.options.convert
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import kotlin.time.Duration

public interface BaseS3Options {
    public val region: String
    public val bucket: String
    public val key: String
    public val validFor: Duration
}

public data class SimpleS3Options(
    override val region: String,
    override val bucket: String,
    override val key: String,
    override val validFor: Duration,
) : BaseS3Options

public class S3Options : OptionGroup("AWS S3 options"), BaseS3Options {
    override val region: String by option(
        "--s3-region",
        envvar = "AWS_REGION",
        help = "The region that the bucket is in",
    ).required()

    override val bucket: String by option(
        "--s3-bucket",
        help = "The bucket to upload to",
    ).required()

    override val key: String by option(
        "--s3-key",
        help = "The key of the uploaded object",
    ).required()

    override val validFor: Duration by option(
        "--valid-for",
        help = "How long the URL is valid for in ISO-8601 Duration format",
    ).convert { Duration.parse(it) }.required()
}
