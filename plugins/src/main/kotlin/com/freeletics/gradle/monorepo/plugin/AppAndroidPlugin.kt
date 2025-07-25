package com.freeletics.gradle.monorepo.plugin

import com.freeletics.gradle.monorepo.setup.applyPlatformConstraints
import com.freeletics.gradle.monorepo.setup.disableAndroidApplicationTasks
import com.freeletics.gradle.monorepo.tasks.CheckDependencyRulesTask.Companion.registerCheckDependencyRulesTasks
import com.freeletics.gradle.monorepo.util.ProjectType
import com.freeletics.gradle.monorepo.util.appType
import com.freeletics.gradle.plugin.FreeleticsAndroidAppPlugin
import com.freeletics.gradle.util.androidApp
import com.freeletics.gradle.util.androidComponents
import com.freeletics.gradle.util.freeleticsAndroidExtension
import com.freeletics.gradle.util.freeleticsExtension
import com.freeletics.gradle.util.stringProperty
import org.gradle.api.Plugin
import org.gradle.api.Project

public abstract class AppAndroidPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.plugins.apply(FreeleticsAndroidAppPlugin::class.java)

        target.freeleticsExtension.extensions.create("app", AppAndroidExtension::class.java)

        target.freeleticsAndroidExtension.minSdkVersion(target.appType()?.minSdkVersion(target))
        target.freeleticsAndroidExtension.enableBuildConfig()
        target.freeleticsAndroidExtension.enableAndroidResources()
        target.freeleticsAndroidExtension.enableResValues()

        target.androidApp {
            signingConfigs {
                named("debug") {
                    val debugKeystore = target.rootProject.file("gradle/debug.keystore")
                    if (debugKeystore.exists()) {
                        it.storeFile = debugKeystore
                    }
                    // enable v4 signing for incremental installs on Android 12
                    it.enableV3Signing = true
                    it.enableV4Signing = true
                }

                val keyStoreFile = target.stringProperty("fgpReleaseStoreFile")
                if (keyStoreFile.isPresent) {
                    register("release") {
                        it.storeFile = target.rootProject.file(keyStoreFile.get())
                        it.storePassword = target.stringProperty("fgpReleaseStorePassword").get()
                        it.keyAlias = target.stringProperty("fgpReleaseKeyAlias").get()
                        it.keyPassword = target.stringProperty("fgpReleaseKeyPassword").get()
                        it.enableV3Signing = true
                        it.enableV4Signing = true
                    }
                }
            }

            androidResources {
                @Suppress("UnstableApiUsage")
                generateLocaleConfig = true
            }

            buildTypes {
                named("debug") {
                    it.applicationIdSuffix = ".debug"
                    it.signingConfig = signingConfigs.getByName("debug")
                    it.isPseudoLocalesEnabled = true
                }

                named("release") {
                    it.signingConfig = signingConfigs.findByName("release") ?: signingConfigs.getByName("debug")
                }
            }

            lint {
                baseline = target.file("lint-baseline.xml")
            }

            testOptions {
                // include test resources in app module to be able to test the manifest
                unitTests.isIncludeAndroidResources = true
            }
        }

        target.androidComponents {
            onVariants(selector().withBuildType("release")) {
                it.packaging.resources.excludes.addAll(
                    // produced by Kotlin for each module/library that uses it, they are not needed inside an app
                    "kotlin/*.kotlin_builtins",
                    "kotlin/**/*.kotlin_builtins",
                    "META-INF/*.kotlin_module",
                    // these are part of AndroidX and only contain version information
                    "META-INF/*.version",
                    // these contain build meta data for various Google Play and Firebase libraries
                    "core.properties",
                    "billing.properties",
                    "build-data.properties",
                    "play-services-*.properties",
                    "firebase-*.properties",
                    "transport-*.properties",
                    // metadata with which Kotlin version the app was built
                    "kotlin-tooling-metadata.json",
                    // various license files or other random files that were packaged in some libraries
                    "asm-license.txt",
                    "LICENSE",
                    "LICENSE_OFL",
                    "LICENSE_UNICODE",
                    "LICENSE.txt",
                    "NOTICE",
                    "README.txt",
                    "META-INF/LICENSE.txt",
                    "META-INF/LICENSE-FIREBASE.txt",
                    "META-INF/LICENSE",
                    "META-INF/NOTICE.txt",
                    "META-INF/NOTICE",
                    "META-INF/DEPENDENCIES.txt",
                )
            }
        }

        target.registerCheckDependencyRulesTasks(
            allowedProjectTypes = listOf(ProjectType.APP),
            allowedDependencyProjectTypes = listOfNotNull(
                ProjectType.CORE_API,
                ProjectType.CORE_IMPLEMENTATION,
                ProjectType.CORE_TESTING,
                ProjectType.DOMAIN_API,
                ProjectType.DOMAIN_IMPLEMENTATION,
                ProjectType.DOMAIN_TESTING,
                ProjectType.FEATURE_IMPLEMENTATION,
                ProjectType.FEATURE_NAV,
                ProjectType.LEGACY,
                ProjectType.APP,
            ),
        )

        target.applyPlatformConstraints()
        target.disableAndroidApplicationTasks()
    }
}
