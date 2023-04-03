package com.freeletics.gradle.plugin

import org.gradle.api.initialization.Settings

abstract class SettingsExtension(private val settings: Settings) {

    /**
     * @param androidXBuildId   buildId for androidx snapshot artifacts. Can be taken from here:
     *                          https://androidx.dev/snapshots/builds
     */
    @JvmOverloads
    fun snapshots(androidXBuildId: String? = null) {
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

    @JvmOverloads
    fun includeMad(path: String = "../mad") {
        settings.includeBuild(path) { build ->
            build.dependencySubstitution {
                it.substitute(it.module("com.freeletics.mad:state-machine")).using(it.project(":state-machine"))
                it.substitute(it.module("com.freeletics.mad:state-machine-testing")).using(
                    it.project(":state-machine:testing")
                )
                it.substitute(it.module("com.freeletics.mad:text-resource")).using(it.project(":text-resource"))
                it.substitute(it.module("com.freeletics.mad:navigator")).using(
                    it.project(":navigator:navigator-runtime")
                )
                it.substitute(it.module("com.freeletics.mad:navigator-androidx-nav")).using(
                    it.project(":navigator:androidx-nav")
                )
                it.substitute(it.module("com.freeletics.mad:navigator-compose")).using(
                    it.project(":navigator:navigator-runtime-compose")
                )
                it.substitute(it.module("com.freeletics.mad:navigator-experimental")).using(
                    it.project(":navigator:runtime-experimental")
                )
                it.substitute(it.module("com.freeletics.mad:navigator-fragment")).using(
                    it.project(":navigator:navigator-runtime-fragment")
                )
                it.substitute(it.module("com.freeletics.mad:navigator-testing")).using(it.project(":navigator:testing"))
                it.substitute(it.module("com.freeletics.mad:whetstone-navigation")).using(
                    it.project(":whetstone:navigation")
                )
                it.substitute(it.module("com.freeletics.mad:whetstone-navigation-compose")).using(
                    it.project(":whetstone:navigation-compose")
                )
                it.substitute(it.module("com.freeletics.mad:whetstone-navigation-fragment")).using(
                    it.project(":whetstone:navigation-fragment")
                )
                it.substitute(it.module("com.freeletics.mad:whetstone-runtime")).using(it.project(":whetstone:runtime"))
                it.substitute(it.module("com.freeletics.mad:whetstone-runtime-compose")).using(
                    it.project(":whetstone:runtime-compose")
                )
                it.substitute(it.module("com.freeletics.mad:whetstone-runtime-fragment")).using(
                    it.project(":whetstone:runtime-fragment")
                )
                it.substitute(it.module("com.freeletics.mad:whetstone-scope")).using(it.project(":whetstone:scope"))
                it.substitute(it.module("com.freeletics.mad:whetstone-compiler")).using(
                    it.project(":whetstone:compiler")
                )
            }
        }
    }

    @JvmOverloads
    fun includeFlowRedux(path: String = "../flowredux") {
        settings.includeBuild(path) { build ->
            build.dependencySubstitution {
                it.substitute(it.module("com.freeletics.flowredux:flowredux")).using(it.project(":flowredux"))
                it.substitute(it.module("com.freeletics.flowredux:compose")).using(it.project(":compose"))
            }
        }
    }
}
