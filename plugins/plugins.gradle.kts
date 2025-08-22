plugins {
    id("com.freeletics.gradle.gradle")
    id("com.freeletics.gradle.publish.oss")
}

dependencies {
    api(libs.kotlin.gradle)
    api(libs.kotlin.gradle.api)
    api(libs.javax.inject)
    implementation(libs.annotations)
    implementation(libs.android.gradle)
    compileOnly(libs.kotlin.gradle.annotations)
    implementation(libs.kotlin.native.utils)
    implementation(libs.ksp.gradle)
    implementation(libs.sqldelight.gradle)
    implementation(libs.compose.gradle)
    implementation(libs.dependency.analysis.gradle)
    implementation(libs.publish.gradle)
    implementation(libs.licensee.gradle)
    implementation(libs.crashlytics.gradle)
    implementation(projects.codegen)
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

        create("commonJvmPlugin") {
            id = "com.freeletics.gradle.jvm"
            implementationClass = "com.freeletics.gradle.plugin.FreeleticsJvmPlugin"
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
            id = "com.freeletics.gradle.core.android"
            implementationClass = "com.freeletics.gradle.monorepo.plugin.CoreAndroidPlugin"
        }

        create("monoCoreKotlinPlugin") {
            id = "com.freeletics.gradle.core.kotlin"
            implementationClass = "com.freeletics.gradle.monorepo.plugin.CoreKotlinPlugin"
        }

        create("monoCoreMultiplatformPlugin") {
            id = "com.freeletics.gradle.core.multiplatform"
            implementationClass = "com.freeletics.gradle.monorepo.plugin.CoreMultiplatformPlugin"
        }

        create("monoDomainAndroidPlugin") {
            id = "com.freeletics.gradle.domain.android"
            implementationClass = "com.freeletics.gradle.monorepo.plugin.DomainAndroidPlugin"
        }

        create("monoDomainKotlinPlugin") {
            id = "com.freeletics.gradle.domain.kotlin"
            implementationClass = "com.freeletics.gradle.monorepo.plugin.DomainKotlinPlugin"
        }

        create("monoDomainMultiplatformPlugin") {
            id = "com.freeletics.gradle.domain.multiplatform"
            implementationClass = "com.freeletics.gradle.monorepo.plugin.DomainMultiplatformPlugin"
        }

        create("monoFeatureAndroidPlugin") {
            id = "com.freeletics.gradle.feature.android"
            implementationClass = "com.freeletics.gradle.monorepo.plugin.FeatureAndroidPlugin"
        }

        create("monoFeatureMultiplatformPlugin") {
            id = "com.freeletics.gradle.feature.multiplatform"
            implementationClass = "com.freeletics.gradle.monorepo.plugin.FeatureMultiplatformPlugin"
        }

        create("monoNavAndroidPlugin") {
            id = "com.freeletics.gradle.nav.android"
            implementationClass = "com.freeletics.gradle.monorepo.plugin.NavAndroidPlugin"
        }

        create("monoNavMultiplatformPlugin") {
            id = "com.freeletics.gradle.nav.multiplatform"
            implementationClass = "com.freeletics.gradle.monorepo.plugin.NavMultiplatformPlugin"
        }

        create("monoLegacyAndroidPlugin") {
            id = "com.freeletics.gradle.legacy.android"
            implementationClass = "com.freeletics.gradle.monorepo.plugin.LegacyAndroidPlugin"
        }

        create("codegenPlugin") {
            id = "com.freeletics.gradle.codegen"
            implementationClass = "com.freeletics.gradle.codegen.CodegenPlugin"
        }
    }
}
