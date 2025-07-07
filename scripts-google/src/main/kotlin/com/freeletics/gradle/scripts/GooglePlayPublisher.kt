package com.freeletics.gradle.scripts

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.androidpublisher.AndroidPublisher
import com.google.api.services.androidpublisher.AndroidPublisherScopes.ANDROIDPUBLISHER
import com.google.api.services.androidpublisher.model.AppEdit
import com.google.auth.oauth2.GoogleCredentials
import java.io.ByteArrayInputStream
import java.io.File
import kotlin.time.Duration.Companion.minutes

public class GooglePlayPublisher(
    jsonKey: String,
    application: String,
    private val appId: String,
) {
    private val credentials = GoogleCredentials.fromStream(ByteArrayInputStream(jsonKey.toByteArray()))
        .createScoped(listOf(ANDROIDPUBLISHER))

    private val androidPublisher = AndroidPublisher.Builder(
        GoogleNetHttpTransport.newTrustedTransport(),
        GsonFactory.getDefaultInstance(),
        // long timeout because uploading an aab can take a while
        TimeoutHttpCredentialsAdapter(credentials, timeout = 3.minutes),
    ).setApplicationName(application).build()

    public fun edit(block: GooglePlayEdit.() -> Boolean) {
        val edit = androidPublisher.edits().insert(appId, AppEdit()).execute()
        try {
            val editManager = GooglePlayEdit(androidPublisher.edits(), appId, edit.id)
            val result = editManager.block()
            if (result) {
                androidPublisher.edits().commit(appId, edit.id)
                    .setChangesNotSentForReview(false)
                    .execute()
            } else {
                androidPublisher.edits().delete(appId, edit.id).execute()
            }
        } catch (t: Throwable) {
            androidPublisher.edits().delete(appId, edit.id).execute()
            throw t
        }
    }

    public fun downloadApkTo(file: File, versionCode: Int) {
        val result = androidPublisher.generatedapks().list(appId, versionCode).execute()
        val universalApk = result.generatedApks.single().generatedUniversalApk
        val request = androidPublisher.generatedapks().download(appId, versionCode, universalApk.downloadId)
        file.outputStream().use {
            request.executeMediaAndDownloadTo(it)
        }
    }
}
