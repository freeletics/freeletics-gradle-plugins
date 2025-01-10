package com.freeletics.gradle.monorepo.plugin

import com.android.build.api.variant.VariantOutputConfiguration
import com.freeletics.gradle.monorepo.setup.configureCrashlytics
import com.freeletics.gradle.monorepo.setup.configureLicensee
import com.freeletics.gradle.monorepo.tasks.ComputeGitShaTask.Companion.mapOutput
import com.freeletics.gradle.monorepo.tasks.ComputeGitShaTask.Companion.registerComputeGitShaTask
import com.freeletics.gradle.monorepo.tasks.ComputeGitTimestampTask.Companion.mapOutput
import com.freeletics.gradle.monorepo.tasks.ComputeGitTimestampTask.Companion.registerComputeGitTimestampTask
import com.freeletics.gradle.monorepo.tasks.ComputeVersionCodeTask.Companion.mapOutput
import com.freeletics.gradle.monorepo.tasks.ComputeVersionCodeTask.Companion.registerComputeVersionCodeTask
import com.freeletics.gradle.monorepo.tasks.ComputeVersionNameTask.Companion.mapOutput
import com.freeletics.gradle.monorepo.tasks.ComputeVersionNameTask.Companion.registerComputeVersionNameTask
import com.freeletics.gradle.monorepo.util.DEFAULT
import com.freeletics.gradle.monorepo.util.INTEGRATION
import com.freeletics.gradle.monorepo.util.PRODUCTION
import com.freeletics.gradle.monorepo.util.QA
import com.freeletics.gradle.monorepo.util.defaultEnvironment
import com.freeletics.gradle.monorepo.util.environmentBuildConfigFields
import com.freeletics.gradle.util.androidApp
import com.freeletics.gradle.util.androidComponentsApp
import com.freeletics.gradle.util.getVersion
import java.io.File
import org.gradle.api.Project

public abstract class AppExtension(private val project: Project) {
    public fun applicationId(applicationId: String) {
        project.androidApp {
            defaultConfig.applicationId = applicationId
        }
    }

    public fun applicationIdSuffix(buildType: String, suffix: String) {
        project.androidApp {
            buildTypes.getByName(buildType).applicationIdSuffix = suffix
        }
    }

    public fun limitLanguagesTo(vararg configs: String) {
        project.androidApp {
            @Suppress("UnstableApiUsage")
            androidResources.localeFilters += configs
        }
    }

    public fun minify(vararg files: File) {
        project.androidApp {
            buildTypes.getByName("release") {
                it.isMinifyEnabled = true
                it.proguardFiles += files
            }
        }

        project.dependencies.add("api", "com.freeletics.gradle:minify-common:${project.getVersion("fgp")}")
    }

    public fun checkLicenses() {
        project.configureLicensee()
    }

    @JvmOverloads
    public fun crashReporting(uploadNativeSymbols: Boolean = false) {
        project.configureCrashlytics(uploadNativeSymbols)
    }

    public fun versionFromGit(gitTagName: String) {
        val versionNameTask = project.registerComputeVersionNameTask(gitTagName)
        val versionCodeTask = project.registerComputeVersionCodeTask(gitTagName)
        val gitShaTask = project.registerComputeGitShaTask()
        val gitTimestampTask = project.registerComputeGitTimestampTask()

        project.androidComponentsApp {
            onVariants { variant ->
                val mainOutput = variant.outputs.single {
                    it.outputType == VariantOutputConfiguration.OutputType.SINGLE
                }
                mainOutput.versionName.set(versionNameTask.mapOutput())
                mainOutput.versionCode.set(versionCodeTask.mapOutput())
                variant.buildConfigFields.put("GIT_SHA1", gitShaTask.mapOutput())
                variant.buildConfigFields.put("BUILD_TIMESTAMP", gitTimestampTask.mapOutput())
            }
        }
    }

    public fun freeleticsBackend() {
        environmentBuildConfig(
            environments = listOf(PRODUCTION, INTEGRATION, QA),
            defaultDebugEnvironment = INTEGRATION,
            defaultReleaseEnvironment = PRODUCTION,
            fallbackEnvironment = QA,
        )
    }

    /**
     * Generates build config fields and res values for the given [defaultDebugEnvironment] and all given
     * [environments] in the `debug` build type and only for the given [defaultReleaseEnvironment] in the `release`
     * build type.
     *
     * Both [defaultDebugEnvironment] and [defaultReleaseEnvironment] can be overridden dynamically by passing
     * `fgp.injectBuildEnvironment` as property.
     *
     * For this it will find all Gradle properties starting with `fgp.app.config`, `fgp.{app-name}.config`,
     * `fgp.app.res` and `fgp.{app-name}.res` and uses them for this purpose. The former 2 will result in build config
     * fields, the latter 2 in res values. Properties will then also be filtered for each environment by using the name
     * of the environment as a suffix. [fallbackEnvironment] will be used for filtering if [environments] does not
     * contain the default environment.
     *
     * If the value of a property contains `{environment}` that value will be replaced with the name of the environment.
     *
     * The name of the result variable is a combination of the environment name and the part of the Gradle property name
     * between the prefix and suffix. For the default environments `DEFAULT` is used instead of the actual environment
     * For example `fpg.app.config.google.client_id.production` with `DEFAULT` as `environmentName` will result in
     * `DEFAULT_GOOGLE_CLIENT_ID`.
     */
    public fun environmentBuildConfig(
        environments: List<String>,
        defaultDebugEnvironment: String,
        defaultReleaseEnvironment: String,
        fallbackEnvironment: String,
    ) {
        project.androidApp {
            buildTypes {
                named("debug") { debugType ->
                    debugType.environmentBuildConfigFields(
                        environment = project.defaultEnvironment(defaultDebugEnvironment),
                        environmentName = DEFAULT,
                        supportedEnvironments = environments,
                        fallbackEnvironment = fallbackEnvironment,
                        project = project,
                    )
                    // contains values for all environments to enable switching between them
                    environments.forEach {
                        debugType.environmentBuildConfigFields(
                            environment = it,
                            environmentName = it,
                            supportedEnvironments = environments,
                            fallbackEnvironment = fallbackEnvironment,
                            project = project,
                        )
                    }
                }

                named("release") { releaseType ->
                    releaseType.environmentBuildConfigFields(
                        environment = project.defaultEnvironment(defaultReleaseEnvironment),
                        environmentName = DEFAULT,
                        supportedEnvironments = environments,
                        fallbackEnvironment = fallbackEnvironment,
                        project = project,
                    )
                }
            }
        }
    }
}
