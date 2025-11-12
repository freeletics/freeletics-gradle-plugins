package com.freeletics.gradle.scripts

import com.google.api.client.http.HttpBackOffUnsuccessfulResponseHandler
import com.google.api.client.http.HttpRequest
import com.google.api.client.util.ExponentialBackOff
import com.google.auth.Credentials
import com.google.auth.http.HttpCredentialsAdapter
import kotlin.time.Duration

/**
 * Wrapper around [Credentials] that increases the request timeout.
 */
internal class TimeoutHttpCredentialsAdapter(
    credentials: Credentials,
    private val timeout: Duration? = null,
) : HttpCredentialsAdapter(credentials) {
    override fun initialize(request: HttpRequest) {
        if (timeout != null) {
            val backOffHandler = HttpBackOffUnsuccessfulResponseHandler(
                ExponentialBackOff.Builder()
                    .setMaxElapsedTimeMillis(timeout.inWholeMilliseconds.toInt())
                    .build(),
            )
            request.setConnectTimeout(timeout.inWholeMilliseconds.toInt())
            request.setReadTimeout(timeout.inWholeMilliseconds.toInt())
            request.setUnsuccessfulResponseHandler(backOffHandler)
        }
        super.initialize(request)
    }
}
