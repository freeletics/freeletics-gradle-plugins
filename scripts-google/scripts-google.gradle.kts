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
    "jvmMainApi"(libs.google.play)
    "jvmMainImplementation"(libs.google.sheets)
    "jvmMainImplementation"(libs.google.apiclient)
    "jvmMainImplementation"(libs.google.http)
    "jvmMainImplementation"(libs.google.http.gson)
    "jvmMainImplementation"(libs.google.credentials)
    "jvmMainImplementation"(libs.google.oauth)
}
