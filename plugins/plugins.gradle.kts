plugins {
    id("com.freeletics.gradle.gradle")
}

freeletics {
    enableOssPublishing()
}

dependencies {
    api(libs.javax.inject)
    implementation(libs.android.gradle.api)
    implementation(libs.annotations)
    implementation(libs.kotlin.gradle.api)
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

        create("monoAppPlugin") {
            id = "com.freeletics.gradle.app"
            implementationClass = "com.freeletics.gradle.monorepo.plugin.AppPlugin"
        }

        create("monoCorePlugin") {
            id = "com.freeletics.gradle.core"
            implementationClass = "com.freeletics.gradle.monorepo.plugin.CorePlugin"
        }

        create("monoDomainPlugin") {
            id = "com.freeletics.gradle.domain"
            implementationClass = "com.freeletics.gradle.monorepo.plugin.DomainPlugin"
        }

        create("monoFeaturePlugin") {
            id = "com.freeletics.gradle.feature"
            implementationClass = "com.freeletics.gradle.monorepo.plugin.FeaturePlugin"
        }

        create("monoNavPlugin") {
            id = "com.freeletics.gradle.nav"
            implementationClass = "com.freeletics.gradle.monorepo.plugin.NavPlugin"
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
