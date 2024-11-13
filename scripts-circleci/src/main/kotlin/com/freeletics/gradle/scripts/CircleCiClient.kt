package com.freeletics.gradle.scripts

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request

public class CircleCiClient(
    private val token: String,
) {
    private val client = OkHttpClient()
    private val json = Json {
        ignoreUnknownKeys = true
    }

    /**
     * https://circleci.com/docs/api/v2/index.html#tag/Job/operation/getTests
     */
    public fun getTestResults(options: CircleCiOptions): List<CircleCiTest>? {
        return getTestResults(options.repoSlug, options.buildNumber)
    }

    /**
     * https://circleci.com/docs/api/v2/index.html#tag/Job/operation/getTests
     */
    public fun getTestResults(repoSlug: String, buildNumber: String): List<CircleCiTest>? {
        return getTestResults(repoSlug, buildNumber, pageToken = null)
    }

    /**
     * https://circleci.com/docs/api/v2/index.html#tag/Job/operation/getTests
     */
    private fun getTestResults(repoSlug: String, buildNumber: String, pageToken: String?): List<CircleCiTest>? {
        val url = "https://circleci.com/api/v2/project/github/$repoSlug/$buildNumber/tests".toHttpUrl()
            .newBuilder()
            .addQueryParameter("page-token", pageToken)
            .build()

        val request = Request.Builder()
            .get()
            .url(url)
            .header("Circle-Token", token)
            .build()

        val response = client.newCall(request).execute()
        if (response.isSuccessful) {
            val body = response.body?.string()
            if (body != null) {
                val testsResponse = json.decodeFromString<CircleCiTestsResponse>(body)
                if (testsResponse.nextPageToken == null) {
                    return testsResponse.items
                }
                val remainingPages = getTestResults(repoSlug, buildNumber, testsResponse.nextPageToken)
                if (remainingPages == null) {
                    // this means one of the subsequent calls failed, return null instead of a partial result
                    return null
                }
                return testsResponse.items + remainingPages
            }
        }
        println("Request failed with ${response.code} ${response.body?.string()}")
        return null
    }
}

/**
 * https://circleci.com/docs/api/v2/index.html#tag/Job/operation/getTests
 *
 * @param items
 * @param nextPageToken A token to pass as a page-token query parameter to return the next page of results.
 */
@Serializable
private data class CircleCiTestsResponse(
    val items: List<CircleCiTest>,
    @SerialName("next_page_token")
    val nextPageToken: String?,
)

/**
 * https://circleci.com/docs/api/v2/index.html#tag/Job/operation/getTests
 *
 * @param name The name of the test.
 * @param classname The programmatic location of the test.
 * @param file The file in which the test is defined.
 * @param result Indication of whether the test succeeded.
 * @param message The failure message associated with the test.
 * @param source The program that generated the test results
 * @param runtime The time it took to run the test in seconds
 */
@Serializable
public data class CircleCiTest(
    val name: String,
    val classname: String,
    val file: String? = null,
    val result: Result,
    val message: String,
    val source: String,
    @SerialName("run_time")
    val runtime: Double,
)

@Serializable
public enum class Result {
    @SerialName("success")
    SUCCESS,

    @SerialName("failure")
    FAILURE,

    @SerialName("skipped")
    SKIPPED,
}
