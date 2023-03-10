package com.freeletics.gradle.plugin

import com.gradle.enterprise.gradleplugin.GradleEnterpriseExtension
import com.gradle.enterprise.gradleplugin.GradleEnterprisePlugin
import org.gradle.api.Plugin
import org.gradle.api.credentials.AwsCredentials
import org.gradle.api.initialization.Settings
import org.gradle.api.initialization.resolve.RepositoriesMode
import org.gradle.caching.http.HttpBuildCache
import org.gradle.kotlin.dsl.jvm
import org.gradle.toolchains.foojay.FoojayToolchainResolver
import org.gradle.toolchains.foojay.FoojayToolchainsPlugin

abstract class SettingsPlugin : Plugin<Settings> {

    override fun apply(target: Settings) {
        target.plugins.apply(FoojayToolchainsPlugin::class.java)
        target.plugins.apply(GradleEnterprisePlugin::class.java)

        target.extensions.create("freeletics", SettingsExtension::class.java, target)

        target.extensions.configure(GradleEnterpriseExtension::class.java) {
            it.buildScan { scan ->
                scan.termsOfServiceUrl = "https://gradle.com/terms-of-service"
                scan.termsOfServiceAgree = "yes"
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

        target.dependencyResolutionManagement { management ->
            @Suppress("UnstableApiUsage")
            management.repositories { handler ->
                val internalUrl = target.providers.gradleProperty("freeleticsAndroidArtifactsUrl").orNull
                if (internalUrl != null) {
                    handler.exclusiveContent { content ->
                        content.forRepository {
                            handler.maven {
                                it.name = "freeleticsAndroidArtifacts"
                                it.setUrl(internalUrl)
                                it.credentials(AwsCredentials::class.java)
                            }
                        }

                        content.filter {
                            it.includeGroupByRegex("com\\.freeletics\\.internal.*")
                            // manually uploaded because only published on jitpack
                            it.includeModule("com.github.kamikat.moshi-jsonapi", "core")
                            it.includeModule("com.github.kamikat.moshi-jsonapi", "retrofit-converter")
                        }
                    }
                }

                handler.exclusiveContent { content ->
                    content.forRepository { handler.google() }

                    content.filter {
                        it.includeGroupByRegex("com\\.android.*")
                        it.includeGroupByRegex("androidx\\..*")
                        it.includeGroupByRegex("android\\..*")

                        it.includeGroup("com.google.android.exoplayer")
                        it.includeGroup("com.google.android.material")
                        it.includeGroup("com.google.android.gms")
                        it.includeGroup("com.google.android.play")
                        it.includeGroup("com.google.android.datatransport")
                        it.includeGroup("com.google.android.flexbox")
                        it.includeGroup("com.google.firebase")
                        it.includeGroup("com.google.testing.platform")
                        it.includeGroup("com.google.android.apps.common.testing.accessibility.framework")
                    }
                }

                handler.exclusiveContent { content ->
                    content.forRepository { handler.gradlePluginPortal() }

                    content.filter {
                        it.includeGroupByRegex("com.gradle.*")
                        it.includeGroupByRegex("org.gradle.*")
                    }
                }

                handler.mavenCentral()
            }

            // TODO https://youtrack.jetbrains.com/issue/KT-51379
            val mpp = target.providers.gradleProperty("fgp.kotlin.multiplatformProject").getOrElse("false").toBoolean()
            if (!mpp) {
                @Suppress("UnstableApiUsage")
                management.repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
            }
        }

        target.buildCache {
            it.local { cache ->
                cache.isEnabled = target.providers.gradleProperty("fgp.buildcache.local").getOrElse("true").toBoolean()
            }

            if (target.providers.gradleProperty("fgp.buildcache.remote").orNull.toBoolean()) {
                it.remote(HttpBuildCache::class.java) { cache ->
                    cache.setUrl(target.providers.gradleProperty("fgp.buildcache.url").get())
                    cache.isPush = target.providers.gradleProperty("fgp.buildcache.push").orNull.toBoolean()
                    cache.isEnabled = true

                    cache.credentials { credentials ->
                        credentials.username = target.providers.gradleProperty("fgp.buildcache.username").get()
                        credentials.password = target.providers.gradleProperty("fgp.buildcache.password").get()
                    }
                }
            }
        }

        val root = target.rootDir
        root.walkTopDown()
            // skip hidden directories as well as directories called build, gradle or src
            .onEnter {
                it == root || (
                    !it.isHidden && it.name != "build" && it.name != "gradle" && it.name != "src" &&
                        it.name != "buildSrc" && !it.resolve("settings.gradle").exists() &&
                        !it.resolve("settings.gradle.kts").exists()
                    )
            }
            .filter { it.name.endsWith(".gradle") || it.name.endsWith(".gradle.kts") }
            .forEach {
                val relativePath = it.parent.substringAfter(root.canonicalPath)
                if (relativePath.isNotEmpty()) {
                    val projectName = relativePath.replace("/", ":")
                    target.include(projectName)
                    target.project(projectName).buildFileName = it.name
                }
            }
    }
}
