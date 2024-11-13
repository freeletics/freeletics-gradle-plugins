plugins {
    alias(libs.plugins.fgp.jvm)
    alias(libs.plugins.fgp.publish)
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
