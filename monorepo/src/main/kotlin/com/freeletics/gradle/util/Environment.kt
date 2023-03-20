package com.freeletics.gradle.util

import com.android.build.api.dsl.VariantDimension
import org.gradle.api.Project

private const val INJECT_BUILD_PROPERTIES_PROPERTY = "fgp.computeInfoFromGit"

/**
 * This property is used to determine whether real version information should be used for the app
 * and whether Crashlytics should be enabled. If this is false or not set dummy values will be used
 * for versioning and Crashlytics so that builds stay reproducible.
 */
internal val Project.computeInfoFromGit
    get() = booleanProperty(INJECT_BUILD_PROPERTIES_PROPERTY, false)

private const val INJECT_ENVIRONMENT_PROPERTY = "fgp.injectBuildEnvironment"

internal fun Project.defaultEnvironment(default: String): String {
    return stringProperty(INJECT_ENVIRONMENT_PROPERTY).getOrElse(default)
}

internal const val DEFAULT = "default"
internal const val PRODUCTION = "production"
internal const val INTEGRATION = "integration"
internal const val QA = "qa"

/**
 * Generates build config fields and res values for the given [environment]. Finds all Gradle properties starting with
 * `fgp.app.config`, `fgp.{app-name}.config`, `fgp.app.res` and `fgp.{app-name}.res` and uses them for this purpose.
 * The former 2 will result in build config fields, the latter 2 in res values.
 *
 * Properties will be filtered to those that use [environment] as suffix unless [supportedEnvironments] does not contain
 * [environment] in which case [fallbackEnvironment] will be used as suffix for filtering.
 *
 * If the value of a property contains `{environment}` that value will be replaced with [environment].
 *
 * The name of the result variable is a combination of [environmentName] and the part of the Gradle property name
 * between the prefix and suffix. For example `fpg.app.config.google.client_id.production` with `DEFAULT` as
 * `environmentName` will result in `DEFAULT_GOOGLE_CLIENT_ID`.
 */
@Suppress("UnstableApiUsage")
internal fun VariantDimension.environmentBuildConfigFields(
    environment: String,
    environmentName: String,
    supportedEnvironments: List<String>,
    fallbackEnvironment: String,
    project: Project,
) {
    val appType = project.appType()!!
    val generatedPrefix = environmentName.uppercase()
    // for qa stacks with a passed in name we need to use QA variables
    val propertySuffix = if (supportedEnvironments.contains(environment)) {
        ".$environment"
    } else {
        ".$fallbackEnvironment"
    }

    buildConfigField("boolean", "${generatedPrefix}_IS_PRODUCTION", "${environment == PRODUCTION}")
    buildConfigFields(project.stringProperties("fgp.app.config.", propertySuffix, environment), generatedPrefix)
    buildConfigFields(
        project.stringProperties("fgp.${appType.name}.config.", propertySuffix, environment),
        generatedPrefix
    )
    resValues(project.stringProperties("fgp.app.res.", propertySuffix, environment), generatedPrefix)
    resValues(project.stringProperties("fgp.${appType.name}.res.", propertySuffix, environment), generatedPrefix)
}

@Suppress("UnstableApiUsage")
internal fun VariantDimension.buildConfigFields(
    properties: List<Pair<String, String>>,
    generatedPrefix: String,
) {
    properties.forEach { entry ->
        buildConfigField("String", "${generatedPrefix}_${entry.first}", "\"${entry.second}\"")
    }
}

@Suppress("UnstableApiUsage")
internal fun VariantDimension.resValues(
    properties: List<Pair<String, String>>,
    generatedPrefix: String,
) {
    properties.forEach { entry ->
        resValue("string", "${generatedPrefix}_${entry.first}", "\"${entry.second}\"")
    }
}

private fun Project.stringProperties(prefix: String, suffix: String, environment: String): List<Pair<String, String>> {
    return stringProperties(prefix).get()
        .mapNotNull {
            if (it.key.endsWith(suffix)) {
                it.keyForVariable(prefix, suffix) to it.valueForEnvironment(environment)
            } else {
                null
            }
        }
}

internal fun Map.Entry<String, String>.keyForVariable(
    propertyPrefix: String,
    propertySuffix: String,
): String {
    return key.drop(propertyPrefix.length)
        .dropLast(propertySuffix.length)
        .replace(".", "_")
        .uppercase()
}

internal fun Map.Entry<String, String>.valueForEnvironment(environment: String): String {
    return value.replace("{environment}", environment)
}
