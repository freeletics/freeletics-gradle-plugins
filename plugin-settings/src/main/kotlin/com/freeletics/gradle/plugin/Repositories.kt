package com.freeletics.gradle.plugin

import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.artifacts.repositories.InclusiveRepositoryContentDescriptor
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.api.artifacts.repositories.MavenRepositoryContentDescriptor
import org.gradle.api.credentials.PasswordCredentials
import org.gradle.api.initialization.Settings

internal fun RepositoryHandler.addMavenCentral() {
    newRepository("Maven Central", "https://repo.maven.apache.org/maven2/") {
        releasesOnly()
    }
}

internal fun RepositoryHandler.addSonatypeSnapshotRepository() {
    newRepository("Central Portal Snapshots", "https://central.sonatype.com/repository/maven-snapshots/") {
        snapshotsOnly()
    }
}

internal fun RepositoryHandler.addKotlinSnapshotRepository() {
    newRepository("Kotlin Bootstrap", "https://redirector.kotlinlang.org/maven/bootstrap/") {
        includeVersionByRegex("^org\\.jetbrains\\.kotlin\\..*", ".*", ".*-(dev|release)-.*")
    }
    newRepository("Kotlin Dev", "https://redirector.kotlinlang.org/maven/dev") {
        includeVersionByRegex("^org\\.jetbrains\\.kotlin\\..*", ".*", ".*-(dev|release)-.*")
    }
}

internal fun RepositoryHandler.addGradlePluginPortalRepository() {
    newExclusiveContentRepository("Gradle Plugin Portal", "https://plugins.gradle.org/m2") {
        includeGroupByRegex("^com\\.gradle.*")
        includeGroupByRegex("^org\\.gradle.*")
    }
}

internal fun RepositoryHandler.addGoogleRepository() {
    newExclusiveContentRepository("Google", "https://dl.google.com/dl/android/maven2/") {
        includeGroupByRegex("^com\\.android.*")
        includeGroupByRegex("^android\\..*")
        // all of AndroidX except for -SNAPSHOT versions
        includeVersionByRegex("^androidx\\..*", ".*", ".*(?<!-SNAPSHOT)\$")
        includeGroupByRegex("^com.google.android.*")
        includeGroup("com.google.firebase")
        includeGroup("com.google.mediapipe")
        includeGroup("com.google.testing.platform")
        includeGroup("com.google.android.apps.common.testing.accessibility.framework")
    }
}

internal fun RepositoryHandler.addAndroidXSnapshotRepositories(androidXBuildId: String?) {
    if (androidXBuildId == null) {
        return
    }

    newExclusiveContentRepository(
        "Androidx Snapshots",
        "https://androidx.dev/snapshots/builds/$androidXBuildId/artifacts/repository/",
    ) {
        includeVersionByRegex("^androidx\\..*", ".*", ".*-SNAPSHOT\$")
    }
}

internal fun RepositoryHandler.addInternalRepository(settings: Settings) {
    val internalUrl = settings.stringProperty("fgp.internalArtifacts.url") ?: return

    val regex = settings.stringProperty("fgp.internalArtifacts.regex") ?: "^com\\.freeletics\\.internal.*"
    newExclusiveContentRepository("internalArtifacts", internalUrl) {
        includeGroupByRegex(regex)
    }.apply {
        credentials(PasswordCredentials::class.java)
    }
}

// TODO remove after https://youtrack.jetbrains.com/issue/KT-68533
internal fun RepositoryHandler.addKotlinJsRepositories() {
    exclusiveContent { content ->
        content.forRepository {
            ivy { ivy ->
                ivy.name = "Node Distributions"
                ivy.setUrl("https://nodejs.org/dist/")
                ivy.patternLayout { it.artifact("v[revision]/[artifact](-v[revision]-[classifier]).[ext]") }
                ivy.metadataSources { it.artifact() }
                ivy.content { it.includeModule("org.nodejs", "node") }
            }
        }

        content.filter { it.includeGroup("org.nodejs") }
    }

    exclusiveContent { content ->
        content.forRepository {
            ivy { ivy ->
                ivy.name = "Yarn Distributions"
                ivy.setUrl("https://github.com/yarnpkg/yarn/releases/download")
                ivy.patternLayout { it.artifact("v[revision]/[artifact](-v[revision]).[ext]") }
                ivy.metadataSources { it.artifact() }
                ivy.content { it.includeModule("com.yarnpkg", "yarn") }
            }
        }

        content.filter { it.includeGroup("com.yarnpkg") }
    }

    exclusiveContent { content ->
        content.forRepository {
            ivy { ivy ->
                ivy.name = "Binaryen Distributions"
                ivy.setUrl("https://github.com/WebAssembly/binaryen/releases/download")
                ivy.patternLayout {
                    it.artifact("version_[revision]/[module]-version_[revision]-[classifier].[ext]")
                }
                ivy.metadataSources { it.artifact() }
                ivy.content { it.includeModule("com.github.webassembly", "binaryen") }
            }
        }

        content.filter { it.includeGroup("com.github.webassembly") }
    }
}

private fun RepositoryHandler.newRepository(
    name: String,
    url: String,
    content: MavenRepositoryContentDescriptor.() -> Unit = {},
): MavenArtifactRepository {
    return maven {
        it.name = name
        it.setUrl(url)
        it.mavenContent(content)
    }
}

private fun RepositoryHandler.newExclusiveContentRepository(
    name: String,
    url: String,
    content: InclusiveRepositoryContentDescriptor.() -> Unit,
): MavenArtifactRepository {
    val repository = newRepository(name, url)
    exclusiveContent {
        it.forRepository { repository }
        it.filter {
            it.content()
        }
    }
    return repository
}
