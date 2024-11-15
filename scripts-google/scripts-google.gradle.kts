plugins {
    alias(libs.plugins.fgp.jvm)
    alias(libs.plugins.fgp.publish)
}

dependencies {
    api(libs.kotlin.stdlib)
    api(libs.clikt)
    api(libs.clikt.core)
    implementation(libs.google.apiclient)
    implementation(libs.google.oauth)
    implementation(libs.google.play)
    implementation(libs.google.sheets)
}
