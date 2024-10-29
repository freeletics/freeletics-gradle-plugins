package com.freeletics.gradle.plugin

import com.gradle.develocity.agent.gradle.DevelocityConfiguration
import com.gradle.develocity.agent.gradle.DevelocityPlugin
import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings
import org.gradle.api.initialization.resolve.DependencyResolutionManagement
import org.gradle.api.initialization.resolve.RepositoriesMode
import org.gradle.caching.http.HttpBuildCache
import org.gradle.kotlin.dsl.jvm
import org.gradle.toolchains.foojay.FoojayToolchainResolver
import org.gradle.toolchains.foojay.FoojayToolchainsPlugin

public abstract class SettingsPlugin : Plugin<Settings> {
    override fun apply(target: Settings) {
        target.extensions.create("freeletics", SettingsExtension::class.java, target)

        target.enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
        if (target.booleanProperty("fgp.stableConfigurationCache", true)) {
            target.enableFeaturePreview("STABLE_CONFIGURATION_CACHE")
        }

        target.configureDependencyResolution()
        target.configureBuildCache()
        target.configureToolchains()
        target.configureDevelocity()

        if (target.booleanProperty("fgp.discoverProjects.automatically", true)) {
            target.discoverProjects(listOf("gradle", "gradle.kts"))
        }
    }

    private fun Settings.configureDependencyResolution() {
        dependencyResolutionManagement.apply {
            @Suppress("UnstableApiUsage")
            repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)

            @Suppress("UnstableApiUsage")
            repositories.apply {
                addMavenCentral()
                addGoogleRepository()
                addGradlePluginPortalRepository()
                addKotlinJsRepositories()
                addInternalRepository(settings)
            }

            addVersionCatalogOverrides(settings)
        }
    }

    private fun DependencyResolutionManagement.addVersionCatalogOverrides(settings: Settings) {
        val prefix = "fgp.version.override."
        val libs = versionCatalogs.maybeCreate("libs")
        @Suppress("UnstableApiUsage")
        settings.providers.gradlePropertiesPrefixedBy(prefix).get().forEach { (name, version) ->
            libs.version(name.substringAfter(prefix), version)
        }
    }

    private fun Settings.configureToolchains() {
        plugins.apply(FoojayToolchainsPlugin::class.java)
        @Suppress("UnstableApiUsage")
        toolchainManagement.apply {
            jvm {
                javaRepositories.repository("foojay") { repository ->
                    repository.resolverClass.set(FoojayToolchainResolver::class.java)
                }
            }
        }
    }

    private fun Settings.configureBuildCache() {
        buildCache.apply {
            local.apply {
                isEnabled = settings.booleanProperty("fgp.buildcache.local", true)
            }

            if (settings.booleanProperty("fgp.buildcache.remote", false)) {
                remote(HttpBuildCache::class.java).apply {
                    setUrl(settings.stringProperty("fgp.buildcache.url")!!)
                    isPush = settings.booleanProperty("fgp.buildcache.push", false)
                    isEnabled = true

                    credentials.apply {
                        username = settings.stringProperty("fgp.buildcache.username")!!
                        password = settings.stringProperty("fgp.buildcache.password")!!
                    }
                }
            }
        }
    }

    private fun Settings.configureDevelocity() {
        plugins.apply(DevelocityPlugin::class.java)
        extensions.configure(DevelocityConfiguration::class.java) {
            it.buildScan.apply {
                termsOfUseUrl.set("https://gradle.com/terms-of-service")
                termsOfUseAgree.set("yes")
                capture.apply {
                    buildLogging.set(false)
                    testLogging.set(false)
                }
                publishing.onlyIf { false }
            }
        }
    }
}
