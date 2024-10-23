package com.freeletics.gradle.plugin

import java.io.File
import org.gradle.api.initialization.Settings

public fun Settings.freeletics(configure: SettingsExtension.() -> Unit) {
    extensions.configure(SettingsExtension::class.java, configure)
}

public abstract class SettingsExtension(private val settings: Settings) {
    /**
     * Automatically find and include Gradle projects in this build. It will start from the root folder and find any
     * project where the build file name matches the path, e.g. `:example` should have `example.gradle` as build file
     * and `:foo:bar` should have `foo-bar.gradle.
     *
     * This does not support nested projects. E.g. `foo:api` and `foo:impl` are generally fine, but if `:foo` also
     * is a project (has a build file) then these 2 are not considered.
     */
    @JvmOverloads
    public fun discoverProjects(kts: Boolean = true) {
        val extensions = if (kts) listOf("gradle.kts") else listOf("gradle")
        discoverProjects(extensions)
    }

    internal fun discoverProjects(extensions: List<String>) {
        val root = settings.rootDir
        val rootPath = root.canonicalPath
        root.listFiles()!!.forEach {
            discoverProjectsIn(it, extensions, rootPath, depth = 1)
        }
    }

    /**
     * Automatically find and include Gradle projects in this build. It will only search in the given folders and find
     * any project where the build file name matches the path, e.g. `:example` should have `example.gradle` as build
     * file and `:foo:bar` should have `foo-bar.gradle.
     *
     * This does not support nested projects. E.g. `foo:api` and `foo:impl` are generally fine, but if `:foo` also
     * is a project (has a build file) then these 2 are not considered.
     */
    @JvmOverloads
    public fun discoverProjectsIn(kts: Boolean = true, vararg directories: String) {
        val extensions = if (kts) listOf("gradle.kts") else listOf("gradle")
        val root = settings.rootDir
        val rootPath = root.canonicalPath
        directories.forEach {
            discoverProjectsIn(root.resolve(it), extensions, rootPath, depth = 1)
        }
    }

    private val ignoredDirectories = listOf("build", "gradle")

    private fun discoverProjectsIn(directory: File, extensions: List<String>, rootPath: String, depth: Int) {
        if (!directory.isDirectory || directory.isHidden || ignoredDirectories.contains(directory.name)) {
            return
        }

        val relativePath = directory.path.substringAfter(rootPath)
        if (relativePath.isNotEmpty()) {
            val projectName = relativePath.replace(File.separator, ":")
            extensions.forEach { extension ->
                val expectedBuildFileName = "${projectName.drop(1).replace(":", "-")}.$extension"
                if (directory.resolve(expectedBuildFileName).exists()) {
                    settings.include(projectName)
                    settings.project(projectName).buildFileName = expectedBuildFileName
                    return
                }
            }
        }

        if (depth < 3) {
            val files = directory.listFiles()!!.toList()
            if (files.none { it.name.startsWith("settings.gradle") }) {
                files.forEach {
                    discoverProjectsIn(it, extensions, rootPath, depth + 1)
                }
            }
        }
    }

    /**
     * @param androidXBuildId   buildId for androidx snapshot artifacts. Can be taken from here:
     *                          https://androidx.dev/snapshots/builds
     */
    @JvmOverloads
    public fun snapshots(androidXBuildId: String? = null) {
        @Suppress("UnstableApiUsage")
        settings.dependencyResolutionManagement.repositories.apply {
            addSonatypeSnapshotRepositories()
            addKotlinSnapshotRepository()
            addAndroidXSnapshotRepositories(androidXBuildId)
            mavenLocal()
        }
    }

    /**
     * Include a local clone of Khonshu in this build.
     */
    @JvmOverloads
    public fun includeKhonshu(path: String = "../khonshu") {
        settings.includeBuild(path) { build ->
            build.dependencySubstitution {
                it.substitute(it.module("com.freeletics.khonshu:state-machine"))
                    .using(it.project(":state-machine"))
                it.substitute(it.module("com.freeletics.khonshu:state-machine-testing"))
                    .using(it.project(":state-machine-testing"))
                it.substitute(it.module("com.freeletics.khonshu:text-resource"))
                    .using(it.project(":text-resource"))
                it.substitute(it.module("com.freeletics.khonshu:navigation"))
                    .using(it.project(":navigation"))
                it.substitute(it.module("com.freeletics.khonshu:navigation-testing"))
                    .using(it.project(":navigation-testing"))
                it.substitute(it.module("com.freeletics.khonshu:codegen-runtime"))
                    .using(it.project(":codegen"))
                it.substitute(it.module("com.freeletics.khonshu:codegen-compiler"))
                    .using(it.project(":codegen-compiler"))
            }
        }
    }

    @JvmOverloads
    public fun includeFlowRedux(path: String = "../flowredux") {
        settings.includeBuild(path) { build ->
            build.dependencySubstitution {
                it.substitute(it.module("com.freeletics.flowredux:flowredux"))
                    .using(it.project(":flowredux"))
                it.substitute(it.module("com.freeletics.flowredux:compose"))
                    .using(it.project(":compose"))
            }
        }
    }
}
