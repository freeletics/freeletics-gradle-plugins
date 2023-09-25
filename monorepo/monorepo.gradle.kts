import org.gradle.kotlin.dsl.accessors.runtime.addConfiguredDependencyTo

plugins {
    alias(libs.plugins.fgp.gradle)
    alias(libs.plugins.fgp.publish)
}

dependencies {
    api(project.projects.base)

    compileOnly(libs.android.gradle.api)
    compileOnly(libs.crashlytics.gradle)
    compileOnly(libs.licensee)

    add("shadeClassPath", libs.android.gradle)
    addConfiguredDependencyTo(this, "shadeClassPath", variantOf(libs.kotlin.gradle) { classifier("gradle76") }) {
        exclude("org.jetbrains.kotlin", "kotlin-gradle-plugin-api")
    }
    add("shadeClassPath", variantOf(libs.kotlin.gradle.api) { classifier("gradle76") })
    add("shadeClassPath", libs.compose.gradle)
    add("shadeClassPath", libs.ksp)
    add("shadeClassPath", libs.anvil.gradle)
    add("shadeClassPath", libs.moshix.gradle)
    add("shadeClassPath", libs.paparazzi.gradle)
    add("shadeClassPath", libs.publish)

    testImplementation(libs.junit)
    testImplementation(libs.truth)
}

gradlePlugin {
    plugins {
        create("monoAppPlugin") {
            id = "com.freeletics.gradle.app"
            implementationClass = "com.freeletics.gradle.plugin.AppPlugin"
        }

        create("monoCoreAndroidPlugin") {
            id = "com.freeletics.gradle.core.android"
            implementationClass = "com.freeletics.gradle.plugin.CoreAndroidPlugin"
        }

        create("monoCoreKotlinPlugin") {
            id = "com.freeletics.gradle.core.kotlin"
            implementationClass = "com.freeletics.gradle.plugin.CoreKotlinPlugin"
        }

        create("monoDomainAndroidPlugin") {
            id = "com.freeletics.gradle.domain.android"
            implementationClass = "com.freeletics.gradle.plugin.DomainAndroidPlugin"
        }

        create("monoDomainKotlinPlugin") {
            id = "com.freeletics.gradle.domain.kotlin"
            implementationClass = "com.freeletics.gradle.plugin.DomainKotlinPlugin"
        }

        create("monoFeaturePlugin") {
            id = "com.freeletics.gradle.feature"
            implementationClass = "com.freeletics.gradle.plugin.FeaturePlugin"
        }

        create("monoNavPlugin") {
            id = "com.freeletics.gradle.nav"
            implementationClass = "com.freeletics.gradle.plugin.NavPlugin"
        }

        create("monoLegacyAndroidPlugin") {
            id = "com.freeletics.gradle.legacy.android"
            implementationClass = "com.freeletics.gradle.plugin.LegacyAndroidPlugin"
        }

        create("monoLegacyKotlinPlugin") {
            id = "com.freeletics.gradle.legacy.kotlin"
            implementationClass = "com.freeletics.gradle.plugin.LegacyKotlinPlugin"
        }
    }
}
