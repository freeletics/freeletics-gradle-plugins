plugins {
    alias(libs.plugins.fgp.gradle)
    alias(libs.plugins.fgp.publish)
}

dependencies {
    compileOnly(libs.android.gradle.api)

    compileOnly(variantOf(libs.kotlin.gradle) { classifier("gradle76") }) {
        exclude("org.jetbrains.kotlin", "kotlin-gradle-plugin-api")
    }
    compileOnly(variantOf(libs.kotlin.gradle.api) { classifier("gradle76") })

    compileOnly(libs.compose.gradle)
    compileOnly(libs.ksp)
    compileOnly(libs.anvil.gradle)
    compileOnly(libs.moshix.gradle)
    compileOnly(libs.paparazzi.gradle)
    compileOnly(libs.dependency.analysis)
    compileOnly(libs.gr8)
    compileOnly(libs.publish)
    compileOnly(libs.licensee)
    compileOnly(libs.crashlytics)

    add("shadeClassPath", libs.android.gradle)

    testImplementation(libs.truth)
}

gradlePlugin {
    plugins {
        create("commonAndroidPlugin") {
            id = "com.freeletics.gradle.common.android"
            implementationClass = "com.freeletics.gradle.plugin.FreeleticsAndroidPlugin"
        }

        create("commonAndroidAppPlugin") {
            id = "com.freeletics.gradle.common.android.app"
            implementationClass = "com.freeletics.gradle.plugin.FreeleticsAndroidAppPlugin"
        }

        create("commonJvmPlugin") {
            id = "com.freeletics.gradle.common.jvm"
            implementationClass = "com.freeletics.gradle.plugin.FreeleticsJvmPlugin"
        }

        create("commonMultiplatformPlugin") {
            id = "com.freeletics.gradle.common.multiplatform"
            implementationClass = "com.freeletics.gradle.plugin.FreeleticsMultiplatformPlugin"
        }

        create("commonGradlePlugin") {
            id = "com.freeletics.gradle.common.gradle"
            implementationClass = "com.freeletics.gradle.plugin.FreeleticsGradlePluginPlugin"
        }

        create("commonPublishInternalPlugin") {
            id = "com.freeletics.gradle.common.publish.internal"
            implementationClass = "com.freeletics.gradle.plugin.FreeleticsPublishInternalPlugin"
        }

        create("commonPublishOssPlugin") {
            id = "com.freeletics.gradle.common.publish.oss"
            implementationClass = "com.freeletics.gradle.plugin.FreeleticsPublishOssPlugin"
        }

        create("monoAppPlugin") {
            id = "com.freeletics.gradle.app"
            implementationClass = "com.freeletics.gradle.monorepo.plugin.AppPlugin"
        }

        create("monoCoreAndroidPlugin") {
            id = "com.freeletics.gradle.core.android"
            implementationClass = "com.freeletics.gradle.monorepo.plugin.CoreAndroidPlugin"
        }

        create("monoCoreKotlinPlugin") {
            id = "com.freeletics.gradle.core.kotlin"
            implementationClass = "com.freeletics.gradle.monorepo.plugin.CoreKotlinPlugin"
        }

        create("monoDomainAndroidPlugin") {
            id = "com.freeletics.gradle.domain.android"
            implementationClass = "com.freeletics.gradle.monorepo.plugin.DomainAndroidPlugin"
        }

        create("monoDomainKotlinPlugin") {
            id = "com.freeletics.gradle.domain.kotlin"
            implementationClass = "com.freeletics.gradle.monorepo.plugin.DomainKotlinPlugin"
        }

        create("monoFeaturePlugin") {
            id = "com.freeletics.gradle.feature"
            implementationClass = "com.freeletics.gradle.monorepo.plugin.FeaturePlugin"
        }

        create("monoNavPlugin") {
            id = "com.freeletics.gradle.nav"
            implementationClass = "com.freeletics.gradle.monorepo.plugin.NavPlugin"
        }

        create("monoLegacyAndroidPlugin") {
            id = "com.freeletics.gradle.legacy.android"
            implementationClass = "com.freeletics.gradle.monorepo.plugin.LegacyAndroidPlugin"
        }

        create("monoLegacyKotlinPlugin") {
            id = "com.freeletics.gradle.legacy.kotlin"
            implementationClass = "com.freeletics.gradle.monorepo.plugin.LegacyKotlinPlugin"
        }
    }
}
