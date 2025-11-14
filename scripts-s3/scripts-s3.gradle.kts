plugins {
    id("com.freeletics.gradle.multiplatform")
}

freeletics {
    enableOssPublishing()
    multiplatform {
        addJvmTarget()
    }
}

dependencies {
    "jvmMainApi"(libs.kotlin.stdlib)
    "jvmMainApi"(libs.clikt)
    "jvmMainApi"(libs.clikt.core)
    "jvmMainImplementation"(libs.s3)
    "jvmMainImplementation"(libs.zxing)
}
