plugins {
    id("com.freeletics.gradle.gradle")
    id("com.freeletics.gradle.publish.oss")
}

dependencies {
    api(libs.kotlin.gradle.api)
    api(libs.javax.inject)
    implementation(libs.android.gradle.api)
    implementation(libs.annotations)
    implementation(libs.kotlin.native.utils)
    implementation(projects.codegen)
    compileOnly(libs.kotlin.gradle.annotations)

    // plugins needed at both compile time for configuration and at runtime as default version
    api(libs.kotlin.gradle)
    implementation(libs.ksp.gradle)
    implementation(libs.sqldelight.gradle)
    implementation(libs.compose.gradle)
    implementation(libs.dependency.analysis.gradle)
    implementation(libs.publish.gradle)
    implementation(libs.licensee.gradle)
    implementation(libs.crashlytics.gradle)

    // already brought in by settings plugin
    compileOnly(libs.develocity.gradle)

    // add to runtime so that consumers can get a default version
    runtimeOnly(libs.android.gradle)
    runtimeOnly(libs.kotlin.gradle.compose)
    runtimeOnly(libs.kotlin.gradle.atomicfu)
    runtimeOnly(libs.kotlin.gradle.serialization)
    runtimeOnly(libs.metro.gradle)
    runtimeOnly(libs.dokka.gradle)
    runtimeOnly(libs.poko.gradle)
    runtimeOnly(libs.kopy.gradle)
    runtimeOnly(libs.skie.gradle)
    runtimeOnly(libs.paparazzi.gradle)

    testImplementation(libs.junit)
    testImplementation(libs.truth)
}

configurations.configureEach {
    exclude("com.google.android", "annotations")
}

gradlePlugin {
    plugins {
        create("commonAndroidPlugin") {
            id = "com.freeletics.gradle.android"
            implementationClass = "com.freeletics.gradle.plugin.FreeleticsAndroidPlugin"
        }

        create("commonAndroidAppPlugin") {
            id = "com.freeletics.gradle.android.app"
            implementationClass = "com.freeletics.gradle.plugin.FreeleticsAndroidAppPlugin"
        }

        create("commonMultiplatformPlugin") {
            id = "com.freeletics.gradle.multiplatform"
            implementationClass = "com.freeletics.gradle.plugin.FreeleticsMultiplatformPlugin"
        }

        create("commonGradlePlugin") {
            id = "com.freeletics.gradle.gradle"
            implementationClass = "com.freeletics.gradle.plugin.FreeleticsGradlePluginPlugin"
        }

        create("commonPublishInternalPlugin") {
            id = "com.freeletics.gradle.publish.internal"
            implementationClass = "com.freeletics.gradle.plugin.FreeleticsPublishInternalPlugin"
        }

        create("commonPublishOssPlugin") {
            id = "com.freeletics.gradle.publish.oss"
            implementationClass = "com.freeletics.gradle.plugin.FreeleticsPublishOssPlugin"
        }

        create("rootPlugin") {
            id = "com.freeletics.gradle.root"
            implementationClass = "com.freeletics.gradle.plugin.RootPlugin"
        }

        create("monoAppAndroidPlugin") {
            id = "com.freeletics.gradle.app.android"
            implementationClass = "com.freeletics.gradle.monorepo.plugin.AppAndroidPlugin"
        }

        create("monoAppDesktopPlugin") {
            id = "com.freeletics.gradle.app.desktop"
            implementationClass = "com.freeletics.gradle.monorepo.plugin.AppDesktopPlugin"
        }

        create("monoAppMultiplatformPlugin") {
            id = "com.freeletics.gradle.app.multiplatform"
            implementationClass = "com.freeletics.gradle.monorepo.plugin.AppMultiplatformPlugin"
        }

        create("monoCoreAndroidPlugin") {
            id = "com.freeletics.gradle.core"
            implementationClass = "com.freeletics.gradle.monorepo.plugin.CoreAndroidPlugin"
        }

        create("monoCoreMultiplatformPlugin") {
            id = "com.freeletics.gradle.core.multiplatform"
            implementationClass = "com.freeletics.gradle.monorepo.plugin.CoreMultiplatformPlugin"
        }

        create("monoDomainAndroidPlugin") {
            id = "com.freeletics.gradle.domain"
            implementationClass = "com.freeletics.gradle.monorepo.plugin.DomainAndroidPlugin"
        }

        create("monoDomainMultiplatformPlugin") {
            id = "com.freeletics.gradle.domain.multiplatform"
            implementationClass = "com.freeletics.gradle.monorepo.plugin.DomainMultiplatformPlugin"
        }

        create("monoFeatureAndroidPlugin") {
            id = "com.freeletics.gradle.feature"
            implementationClass = "com.freeletics.gradle.monorepo.plugin.FeatureAndroidPlugin"
        }

        create("monoFeatureMultiplatformPlugin") {
            id = "com.freeletics.gradle.feature.multiplatform"
            implementationClass = "com.freeletics.gradle.monorepo.plugin.FeatureMultiplatformPlugin"
        }

        create("monoNavAndroidPlugin") {
            id = "com.freeletics.gradle.nav"
            implementationClass = "com.freeletics.gradle.monorepo.plugin.NavAndroidPlugin"
        }

        create("monoNavMultiplatformPlugin") {
            id = "com.freeletics.gradle.nav.multiplatform"
            implementationClass = "com.freeletics.gradle.monorepo.plugin.NavMultiplatformPlugin"
        }

        create("monoLegacyAndroidPlugin") {
            id = "com.freeletics.gradle.legacy"
            implementationClass = "com.freeletics.gradle.monorepo.plugin.LegacyAndroidPlugin"
        }

        create("codegenPlugin") {
            id = "com.freeletics.gradle.codegen"
            implementationClass = "com.freeletics.gradle.codegen.CodegenPlugin"
        }

        create("appiumPlugin") {
            id = "com.freeletics.gradle.appium"
            implementationClass = "com.freeletics.gradle.plugin.AppiumPlugin"
        }
    }
}
