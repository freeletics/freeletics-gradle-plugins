package com.freeletics.gradle.scripts

import com.google.api.client.http.FileContent
import com.google.api.services.androidpublisher.AndroidPublisher
import com.google.api.services.androidpublisher.model.LocalizedText
import com.google.api.services.androidpublisher.model.Track
import com.google.api.services.androidpublisher.model.TrackRelease
import java.io.File

public class GooglePlayEdit(
    private val edits: AndroidPublisher.Edits,
    private val appId: String,
    private val editId: String,
) {
    public fun upload(bundleFile: File, version: GooglePlayReleaseVersion) {
        println("Uploading $version $bundleFile")
        val content = FileContent("application/octet-stream", bundleFile)
        val bundle = edits.bundles().upload(appId, editId, content).execute()
        check(version.code == bundle.versionCode.toLong()) {
            "Unexpected version code in uploaded bundle ${bundle.versionCode}, expected ${version.code}"
        }
        println("Successfully uploaded $version $bundleFile")
    }

    public fun publishToTrack(
        track: String,
        rollout: Double,
        version: GooglePlayReleaseVersion,
        releaseNotes: Map<String, String>,
    ) {
        println("Publishing $version to $track")
        val release = TrackRelease().apply {
            name = version.name
            versionCodes = listOf(version.code)
            setRolloutStatusAndFraction(rollout)
            setReleaseNotes(
                releaseNotes.entries.map { (language, text) ->
                    LocalizedText().apply {
                        this.language = language
                        this.text = text
                    }
                },
            )
        }
        val content = Track().apply {
            this.track = track
            this.releases = listOf(release)
        }
        edits.tracks().update(appId, editId, track, content).execute()
        println("Successfully published $release to $track")
    }

    public fun updateRolloutInTrack(track: String, rollout: Double, version: GooglePlayReleaseVersion) {
        println("Rolling out $version in $track to ${rollout * 100}%)")

        val content = edits.tracks().get(appId, editId, track).execute()
        val releases = content.releases.filter { it.versionCodes.contains(version.code) }
        check(releases.isNotEmpty()) {
            "Track $track does not contain version code ${version.code}. This can happen if there is a newer" +
                "release train already running. Please continue with that release train. Currently in $track:\n" +
                content.releases.toVersions().joinToString(separator = "\n")
        }

        releases.forEach {
            it.setRolloutStatusAndFraction(rollout)
        }
        // if the rollout is complete explicitly limit the releases to the one that has the rolled out version code
        if (rollout == 1.0) {
            content.releases = releases
        }
        edits.tracks().update(appId, editId, track, content).execute()
        println("Successfully rolled out $version in $track to ${rollout * 100f}%)")
    }

    private fun TrackRelease.setRolloutStatusAndFraction(rollout: Double) {
        status = if (rollout >= 1) STATUS_COMPLETED else STATUS_IN_PROGRESS
        // fraction should only be used if status is STATUS_IN_PROGRESS
        userFraction = rollout.takeIf { rollout < 1 }
    }

    public fun versionsInTrack(track: String): List<GooglePlayReleaseVersion> {
        val content = edits.tracks().get(appId, editId, track).execute()
        return content.releases.toVersions()
    }

    private fun List<TrackRelease>.toVersions() = flatMap { release ->
        release.versionCodes.map { GooglePlayReleaseVersion(release.name, it) }
    }

    public companion object {
        public const val TRACK_INTERNAL: String = "internal"
        public const val TRACK_BETA: String = "beta"
        public const val TRACK_PRODUCTION: String = "production"

        public const val STATUS_IN_PROGRESS: String = "inProgress"
        public const val STATUS_COMPLETED: String = "completed"
    }
}
