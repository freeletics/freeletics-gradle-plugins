plugins {
    id("com.freeletics.gradle.multiplatform")
    id("com.freeletics.gradle.publish.oss")
}

freeletics {
    multiplatform {
        addJvmTarget()
    }
}

dependencies {
    "jvmMainApi"(libs.kotlin.stdlib)
    "jvmMainApi"(libs.clikt)
    "jvmMainApi"(libs.clikt.core)
}
