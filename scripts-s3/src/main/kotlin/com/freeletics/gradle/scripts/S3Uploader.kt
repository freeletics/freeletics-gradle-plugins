package com.freeletics.gradle.scripts

import aws.sdk.kotlin.services.s3.S3Client
import aws.sdk.kotlin.services.s3.model.GetObjectRequest
import aws.sdk.kotlin.services.s3.presigners.presignGetObject
import aws.sdk.kotlin.services.s3.putObject
import aws.smithy.kotlin.runtime.content.ByteStream
import aws.smithy.kotlin.runtime.content.fromFile
import java.io.File

public suspend fun upload(byteArray: ByteArray, options: S3Options): String {
    return upload(ByteStream.fromBytes(byteArray), options)
}

public suspend fun upload(file: File, options: S3Options): String {
    return upload(ByteStream.fromFile(file), options)
}

private suspend fun upload(stream: ByteStream, options: S3Options): String {
    S3Client {
        this.region = options.region
    }.use { s3 ->
        s3.putObject {
            bucket = options.bucket
            key = options.key
            body = stream
        }


        val unsignedRequest = GetObjectRequest {
            bucket = options.bucket
            key = options.key
        }
        val presignedRequest = s3.presignGetObject(unsignedRequest, options.validFor)
        return presignedRequest.url.toString()
    }

}
