package com.freeletics.gradle.plugin

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
        discoverProjectsIn(kts)
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
        settings.discoverProjects(extensions, directories.toList())
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
