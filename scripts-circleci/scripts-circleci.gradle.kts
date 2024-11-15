plugins {
    id("com.freeletics.gradle.jvm")
    id("com.freeletics.gradle.publish.oss")
}

freeletics {
    useSerialization()
}

dependencies {
    api(libs.kotlin.stdlib)
    api(libs.clikt)
    api(libs.clikt.core)
    implementation(libs.okhttp)
    implementation(libs.kotlinx.serialization.json)
}
