package com.freeletics.gradle.scripts

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.SheetsScopes.SPREADSHEETS_READONLY
import com.google.auth.http.HttpCredentialsAdapter
import com.google.auth.oauth2.GoogleCredentials
import java.io.ByteArrayInputStream
import java.util.Base64

public class GoogleSheetsReader(applicationName: String, jsonKey: String) {
    private val credentials = GoogleCredentials.fromStream(ByteArrayInputStream(Base64.getDecoder().decode(jsonKey)))
        .createScoped(listOf(SPREADSHEETS_READONLY))

    private val sheets = Sheets.Builder(
        GoogleNetHttpTransport.newTrustedTransport(),
        GsonFactory.getDefaultInstance(),
        HttpCredentialsAdapter(credentials),
    ).setApplicationName(applicationName).build()

    public fun read(spreadsheetId: String, range: String): List<MutableList<Any>> {
        return sheets.spreadsheets()
            .values()
            .get(spreadsheetId, range)
            .execute()!!
            .getValues()
    }
}
