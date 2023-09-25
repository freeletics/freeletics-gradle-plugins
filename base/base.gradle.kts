plugins {
    alias(libs.plugins.fgp.gradle)
    alias(libs.plugins.fgp.publish)
}

dependencies {
    compileOnly(libs.gradle.api)
    compileOnly(variantOf(libs.kotlin.gradle) { classifier("gradle76") }) {
        exclude("org.jetbrains.kotlin", "kotlin-gradle-plugin-api")
    }
    compileOnly(variantOf(libs.kotlin.gradle.api) { classifier("gradle76") })
    compileOnly(libs.android.gradle.api)
    compileOnly(libs.compose.gradle)
    compileOnly(libs.ksp)
    compileOnly(libs.anvil.gradle)
    compileOnly(libs.moshix.gradle)
    compileOnly(libs.paparazzi.gradle)
    compileOnly(libs.gr8)
    compileOnly(libs.publish)

    add("shadeClassPath", libs.android.gradle)
}

gradlePlugin {
    plugins {
        create("androidPlugin") {
            id = "com.freeletics.gradle.android"
            implementationClass = "com.freeletics.gradle.plugin.FreeleticsAndroidPlugin"
        }

        create("androidAppPlugin") {
            id = "com.freeletics.gradle.android.app"
            implementationClass = "com.freeletics.gradle.plugin.FreeleticsAndroidAppPlugin"
        }

        create("jvmPlugin") {
            id = "com.freeletics.gradle.jvm"
            implementationClass = "com.freeletics.gradle.plugin.FreeleticsJvmPlugin"
        }

        create("multiplatformPlugin") {
            id = "com.freeletics.gradle.multiplatform"
            implementationClass = "com.freeletics.gradle.plugin.FreeleticsMultiplatformPlugin"
        }

        create("gradlePlugin") {
            id = "com.freeletics.gradle.gradle"
            implementationClass = "com.freeletics.gradle.plugin.FreeleticsGradlePluginPlugin"
        }

        create("publishInternalPlugin") {
            id = "com.freeletics.gradle.publish.internal"
            implementationClass = "com.freeletics.gradle.plugin.FreeleticsPublishInternalPlugin"
        }

        create("publishOssPlugin") {
            id = "com.freeletics.gradle.publish.oss"
            implementationClass = "com.freeletics.gradle.plugin.FreeleticsPublishOssPlugin"
        }
    }
}
