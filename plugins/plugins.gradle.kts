plugins {
    id("com.freeletics.gradle.gradle")
    id("com.freeletics.gradle.publish.oss")
}

dependencies {
    api(libs.kotlin.gradle)
    api(libs.javax.inject)
    implementation(libs.annotations)
    implementation(libs.android.gradle)
    implementation(libs.kotlin.gradle.api)
    implementation(libs.kotlin.gradle.annotations)
    implementation(libs.kotlin.native.utils)
    implementation(libs.ksp.gradle)
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

        create("codegenPlugin") {
            id = "com.freeletics.gradle.codegen"
            implementationClass = "com.freeletics.gradle.codegen.CodegenPlugin"
        }
    }
}
