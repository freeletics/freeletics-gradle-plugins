package com.freeletics.gradle.plugin

import java.io.File
import org.gradle.api.initialization.Settings

internal fun Settings.discoverProjects(extensions: List<String>, directories: List<String> = emptyList()) {
    val root = settings.rootDir
    val rootPath = root.canonicalPath
    directories.ifEmpty { root.listFileNames() }.forEach {
        settings.discoverProjects(root.resolve(it), extensions, rootPath)
    }
}

private fun File.listFileNames() = listFiles()!!.map { it.name }

private val ignoredDirectories = listOf("build", "gradle")

private fun Settings.discoverProjects(
    directory: File,
    extensions: List<String>,
    rootPath: String,
    depth: Int = 1,
) {
    if (!directory.isDirectory || directory.isHidden || ignoredDirectories.contains(directory.name)) {
        return
    }

    val relativePath = directory.path.substringAfter(rootPath)
    if (relativePath.isNotEmpty()) {
        val projectName = relativePath.replace(File.separator, ":")
        extensions.forEach { extension ->
            val expectedBuildFileName = "${projectName.drop(1).replace(":", "-")}.$extension"
            if (directory.resolve(expectedBuildFileName).exists()) {
                include(projectName)
                project(projectName).buildFileName = expectedBuildFileName
                return
            }
        }
    }

    if (depth < 3) {
        val files = directory.listFiles()!!.toList()
        if (files.none { it.name.startsWith("settings.gradle") }) {
            files.forEach {
                discoverProjects(it, extensions, rootPath, depth + 1)
            }
        }
    }
}
