plugins {
    id("com.freeletics.gradle.jvm")
    id("com.freeletics.gradle.publish.oss")
}

dependencies {
    api(libs.kotlin.stdlib)
    api(libs.clikt)
    api(libs.clikt.core)
    api(libs.slack)
}
