package com.freeletics.gradle.plugin

import com.gradle.develocity.agent.gradle.DevelocityConfiguration
import com.gradle.develocity.agent.gradle.DevelocityPlugin
import org.gradle.api.Plugin
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.credentials.PasswordCredentials
import org.gradle.api.initialization.Settings
import org.gradle.api.initialization.resolve.RepositoriesMode
import org.gradle.caching.http.HttpBuildCache
import org.gradle.kotlin.dsl.jvm
import org.gradle.toolchains.foojay.FoojayToolchainResolver
import org.gradle.toolchains.foojay.FoojayToolchainsPlugin

public abstract class SettingsPlugin : Plugin<Settings> {
    override fun apply(target: Settings) {
        target.plugins.apply(FoojayToolchainsPlugin::class.java)
        target.plugins.apply(DevelocityPlugin::class.java)

        val extension = target.extensions.create("freeletics", SettingsExtension::class.java, target)

        target.extensions.configure(DevelocityConfiguration::class.java) {
            it.buildScan { scan ->
                scan.termsOfUseUrl.set("https://gradle.com/terms-of-service")
                scan.termsOfUseAgree.set("yes")
                scan.capture {
                    it.buildLogging.set(false)
                    it.testLogging.set(false)
                }
                scan.publishing.onlyIf { false }
            }
        }

        @Suppress("UnstableApiUsage")
        target.toolchainManagement {
            it.jvm {
                javaRepositories.repository("foojay") { repository ->
                    repository.resolverClass.set(FoojayToolchainResolver::class.java)
                }
            }
        }

        target.enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

        if (target.booleanProperty("fgp.stableConfigurationCache", true)) {
            target.enableFeaturePreview("STABLE_CONFIGURATION_CACHE")
        }

        target.dependencyResolutionManagement { management ->
            @Suppress("UnstableApiUsage")
            management.repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)

            @Suppress("UnstableApiUsage")
            management.repositories { handler ->
                val internalUrl = target.stringProperty("fgp.internalArtifacts.url")
                if (internalUrl != null) {
                    handler.exclusiveContent { content ->
                        content.forRepository {
                            handler.maven {
                                it.name = "internalArtifacts"
                                it.setUrl(internalUrl)
                                it.credentials(PasswordCredentials::class.java)
                            }
                        }

                        content.filter {
                            it.includeGroupByRegex("^com\\.freeletics\\.internal.*")
                        }
                    }
                }

                handler.exclusiveContent { content ->
                    content.forRepository { handler.google() }

                    content.filter {
                        it.includeGroupByRegex("^com\\.android.*")
                        it.includeGroupByRegex("^android\\..*")
                        // all of AndroidX except for androidx.compose.compiler and -SNAPSHOT versions
                        it.includeVersionByRegex("^androidx\\..*(?<!compose\\.compiler)\$", ".*", ".*(?<!-SNAPSHOT)\$")
                        // androidx.compose.compiler but not dev versions because they come from a different repository
                        it.includeVersionByRegex("^androidx.compose.compiler\$", ".*", "^((?!-dev-k).)*\$")

                        it.includeGroupByRegex("^com.google.android.*")
                        it.includeGroup("com.google.firebase")
                        it.includeGroup("com.google.testing.platform")
                        it.includeGroup("com.google.android.apps.common.testing.accessibility.framework")
                    }
                }

                handler.exclusiveContent { content ->
                    content.forRepository { handler.gradlePluginPortal() }

                    content.filter {
                        it.includeGroupByRegex("^com.gradle.*")
                        it.includeGroupByRegex("^org.gradle.*")
                    }
                }

                handler.mavenCentral {
                    it.mavenContent { content ->
                        content.releasesOnly()
                    }
                }

                handler.jsRepositoryWorkaround()
            }

            val prefix = "fgp.version.override."
            management.versionCatalogs {
                val libs = it.maybeCreate("libs")
                @Suppress("UnstableApiUsage")
                target.providers.gradlePropertiesPrefixedBy(prefix).get().forEach { (name, version) ->
                    libs.version(name.substringAfter(prefix), version)
                }
            }
        }

        target.buildCache {
            it.local { cache ->
                cache.isEnabled = target.booleanProperty("fgp.buildcache.local", true)
            }

            if (target.booleanProperty("fgp.buildcache.remote", false)) {
                it.remote(HttpBuildCache::class.java) { cache ->
                    cache.setUrl(target.stringProperty("fgp.buildcache.url")!!)
                    cache.isPush = target.booleanProperty("fgp.buildcache.push", false)
                    cache.isEnabled = true

                    cache.credentials { credentials ->
                        credentials.username = target.stringProperty("fgp.buildcache.username")!!
                        credentials.password = target.stringProperty("fgp.buildcache.password")!!
                    }
                }
            }
        }

        val autoDiscover = target.booleanProperty("fgp.discoverProjects.automatically", true)
        if (autoDiscover) {
            extension.discoverProjects(listOf("gradle", "gradle.kts"))
        }
    }

    private fun Settings.stringProperty(name: String): String? {
        return providers.gradleProperty(name).orNull
    }

    private fun Settings.booleanProperty(name: String, default: Boolean): Boolean {
        return providers.gradleProperty(name).map { it.toBoolean() }.orElse(default).get()
    }

    // TODO remove after https://youtrack.jetbrains.com/issue/KT-68533
    private fun RepositoryHandler.jsRepositoryWorkaround() {
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
}
