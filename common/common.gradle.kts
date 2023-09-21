plugins {
    alias(libs.plugins.fgp.gradle)
    alias(libs.plugins.fgp.publish)
}

dependencies {
    add("shade", project.projects.base)

    compileOnly(variantOf(libs.kotlin.gradle) { classifier("gradle76") }) {
        exclude("org.jetbrains.kotlin", "kotlin-gradle-plugin-api")
    }
    compileOnly(variantOf(libs.kotlin.gradle.api) { classifier("gradle76") })
    compileOnly(libs.dependency.analysis)
    compileOnly(libs.gr8)
    compileOnly(libs.publish)

    add("shadeClassPath", libs.android.gradle)
    add("shadeClassPath", libs.android.gradle.api)
    add("shadeClassPath", libs.ksp)
    add("shadeClassPath", libs.anvil.gradle)
    add("shadeClassPath", libs.moshix.gradle)
    add("shadeClassPath", libs.paparazzi.gradle)
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
    }
}
