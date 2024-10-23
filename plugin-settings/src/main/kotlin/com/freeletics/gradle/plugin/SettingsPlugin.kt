package com.freeletics.gradle.plugin

import com.gradle.develocity.agent.gradle.DevelocityConfiguration
import com.gradle.develocity.agent.gradle.DevelocityPlugin
import org.gradle.api.Plugin
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
                scan.capture { capture ->
                    capture.buildLogging.set(false)
                    capture.testLogging.set(false)
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
            management.repositories.apply {
                addMavenCentral()
                addGoogleRepository()
                addGradlePluginPortalRepository()
                addKotlinJsRepositories()
                addInternalRepository(target)
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
}
