package com.freeletics.gradle.plugin

import java.io.File
import org.gradle.api.initialization.Settings

public abstract class SettingsExtension(private val settings: Settings) {

    /**
     * Automatically find and include Gradle projects in this build. It will start from the root folder and find any
     * project where the build file name matches the path, e.g. `:example` should have `example.gradle` as build file
     * and `:foo:bar` should have `foo-bar.gradle.
     *
     * This does not support nested projects. E.g. `foo:api` and `foo:impl` are generally fine, but if `:foo` also
     * is a project (has a build file) then these 2 are not considered.
     */
    public fun discoverProjects() {
        val root = settings.rootDir
        val rootPath = root.canonicalPath
        root.listFiles()!!.forEach {
            discoverProjectsIn(it, rootPath)
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
    public fun discoverProjectsIn(vararg directories: String) {
        val root = settings.rootDir
        val rootPath = root.canonicalPath
        directories.forEach {
            discoverProjectsIn(root.resolve(it), rootPath)
        }
    }

    private val gradleFileRegex = Regex(".+\\.gradle(\\.kts)?")
    private val ignoredDirectories = listOf("build", "gradle")

    private fun discoverProjectsIn(directory: File, rootPath: String) {
        if (!directory.isDirectory || directory.isHidden || ignoredDirectories.contains(directory.name)) {
            return
        }

        val files = directory.listFiles()!!.toList()
        val gradleFiles = files.filter { gradleFileRegex.matches(it.name) }
        if (gradleFiles.any { it.name.startsWith("settings.gradle") }) {
            return
        } else if (gradleFiles.isNotEmpty()) {
            val buildFile = gradleFiles.single()
            val relativePath = buildFile.parent.substringAfter(rootPath)
            if (relativePath.isNotEmpty()) {
                val projectName = relativePath.replace(File.separator, ":")
                settings.include(projectName)
                settings.project(projectName).buildFileName = buildFile.name
            }
        } else {
            files.forEach {
                discoverProjectsIn(it, rootPath)
            }
        }
    }

    /**
     * @param androidXBuildId   buildId for androidx snapshot artifacts. Can be taken from here:
     *                          https://androidx.dev/snapshots/builds
     */
    @JvmOverloads
    public fun snapshots(androidXBuildId: String? = null) {
        settings.dependencyResolutionManagement { management ->
            management.repositories { handler ->
                handler.maven {
                    it.setUrl("https://oss.sonatype.org/content/repositories/snapshots/")
                    it.mavenContent { content ->
                        content.snapshotsOnly()
                    }
                }
                handler.maven {
                    it.setUrl("https://s01.oss.sonatype.org/content/repositories/snapshots/")
                    it.mavenContent { content ->
                        content.snapshotsOnly()
                    }
                }
                handler.maven {
                    it.setUrl("https://androidx.dev/storage/compose-compiler/repository/")
                    it.mavenContent { content ->
                        // limit to androidx.compose.compiler dev versions
                        content.includeVersionByRegex("^androidx.compose.compiler\$", ".*", ".+-dev-k.+")
                    }
                }
                if (androidXBuildId != null) {
                    handler.maven {
                        it.setUrl("https://androidx.dev/snapshots/builds/$androidXBuildId/artifacts/repository/")
                        it.mavenContent { content ->
                            // limit to AndroidX and SNAPSHOT versions
                            content.includeGroup("^androidx\\..*")
                            content.snapshotsOnly()
                        }
                    }
                }
                handler.maven {
                    it.setUrl("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/bootstrap")
                    it.mavenContent { content ->
                        // limit to org.jetbrains.kotlin artifacts with -dev- or -release-
                        content.includeVersionByRegex("^org.jetbrains.kotlin.*", ".*", ".*-(dev|release)-.*")
                    }
                }
                handler.mavenLocal()
            }
        }
    }

    /**
     * Replace any usage of MAD `navigator-compose` with `navigator-experimental` to try out the
     * experimental navigation implementation.
     *
     * When using an included build the `experimentalNavigation` parameter on [includeMad] should be used instead.
     */
    public fun useMadExperimentalNavigation() {
        settings.gradle.beforeProject { project ->
            project.configurations.configureEach { configuration ->
                configuration.resolutionStrategy.eachDependency {
                    if (it.requested.group == "com.freeletics.mad" && it.requested.name == "navigator-compose") {
                        it.useTarget("com.freeletics.mad:navigator-experimental:${it.requested.version}")
                    }
                }
            }
        }
    }

    /**
     * Include a local clone of MAD in this build.
     *
     * When [experimentalNavigation] is `true` any usage of MAD `navigator-compose` will be replaced with
     * `navigator-experimental` to try out the experimental navigation implementation.
     */
    @JvmOverloads
    public fun includeMad(path: String = "../mad", experimentalNavigation: Boolean = false) {
        settings.includeBuild(path) { build ->
            build.dependencySubstitution {
                it.substitute(it.module("com.freeletics.mad:state-machine"))
                    .using(it.project(":state-machine:runtime"))
                it.substitute(it.module("com.freeletics.mad:state-machine-testing"))
                    .using(it.project(":state-machine:testing"))
                it.substitute(it.module("com.freeletics.mad:text-resource"))
                    .using(it.project(":text-resource"))
                it.substitute(it.module("com.freeletics.mad:navigator"))
                    .using(it.project(":navigator:runtime"))
                it.substitute(it.module("com.freeletics.mad:navigator-androidx-nav"))
                    .using(it.project(":navigator:androidx-nav"))
                if (experimentalNavigation) {
                    it.substitute(it.module("com.freeletics.mad:navigator-compose"))
                        .using(it.project(":navigator:runtime-experimental"))
                } else {
                    it.substitute(it.module("com.freeletics.mad:navigator-compose"))
                        .using(it.project(":navigator:runtime-compose"))
                }
                it.substitute(it.module("com.freeletics.mad:navigator-experimental"))
                    .using(it.project(":navigator:runtime-experimental"))
                it.substitute(it.module("com.freeletics.mad:navigator-fragment"))
                    .using(it.project(":navigator:runtime-fragment"))
                it.substitute(it.module("com.freeletics.mad:navigator-testing"))
                    .using(it.project(":navigator:testing"))
                it.substitute(it.module("com.freeletics.mad:whetstone-navigation"))
                    .using(it.project(":whetstone:navigation"))
                it.substitute(it.module("com.freeletics.mad:whetstone-navigation-compose"))
                    .using(it.project(":whetstone:navigation-compose"))
                it.substitute(it.module("com.freeletics.mad:whetstone-navigation-fragment"))
                    .using(it.project(":whetstone:navigation-fragment"))
                it.substitute(it.module("com.freeletics.mad:whetstone-runtime"))
                    .using(it.project(":whetstone:runtime"))
                it.substitute(it.module("com.freeletics.mad:whetstone-runtime-compose"))
                    .using(it.project(":whetstone:runtime-compose"))
                it.substitute(it.module("com.freeletics.mad:whetstone-runtime-fragment"))
                    .using(it.project(":whetstone:runtime-fragment"))
                it.substitute(it.module("com.freeletics.mad:whetstone-scope"))
                    .using(it.project(":whetstone:scope"))
                it.substitute(it.module("com.freeletics.mad:whetstone-compiler"))
                    .using(it.project(":whetstone:compiler"))

                if (experimentalNavigation) {
                    it.substitute(it.project(":navigator:runtime-compose"))
                        .using(it.project(":navigator:runtime-experimental"))
                }
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
