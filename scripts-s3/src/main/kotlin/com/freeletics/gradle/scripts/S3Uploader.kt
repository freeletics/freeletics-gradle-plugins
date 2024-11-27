package com.freeletics.gradle.scripts

import aws.sdk.kotlin.services.s3.S3Client
import aws.sdk.kotlin.services.s3.model.GetObjectRequest
import aws.sdk.kotlin.services.s3.presigners.presignGetObject
import aws.sdk.kotlin.services.s3.putObject
import aws.smithy.kotlin.runtime.content.ByteStream
import aws.smithy.kotlin.runtime.content.fromFile
import java.io.File

public suspend fun upload(byteArray: ByteArray, options: BaseS3Options): String {
    return upload(ByteStream.fromBytes(byteArray), options)
}

public suspend fun upload(content: String, options: BaseS3Options): String {
    return upload(ByteStream.fromString(content), options)
}

public suspend fun upload(file: File, options: BaseS3Options): String {
    return upload(ByteStream.fromFile(file), options)
}

private suspend fun upload(stream: ByteStream, options: BaseS3Options): String {
    S3Client {
        this.region = options.region
    }.use { s3 ->
        s3.putObject {
            bucket = options.bucket
            key = options.key
            body = stream
            if (options.key.endsWith(".html")) {
                metadata = mapOf("Content-Type" to "text/html")
            }
        }

        val unsignedRequest = GetObjectRequest {
            bucket = options.bucket
            key = options.key
        }
        val presignedRequest = s3.presignGetObject(unsignedRequest, options.validFor)
        return presignedRequest.url.toString()
    }
}
