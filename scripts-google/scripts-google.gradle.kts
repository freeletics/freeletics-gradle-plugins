plugins {
    alias(libs.plugins.fgp.jvm)
    alias(libs.plugins.fgp.publish)
}

dependencies {
    api(libs.kotlin.stdlib)
    api(libs.clikt)
    api(libs.clikt.core)
    api(libs.google.play)
    implementation(libs.google.sheets)
    implementation(libs.google.apiclient)
    implementation(libs.google.http)
    implementation(libs.google.http.gson)
    implementation(libs.google.credentials)
    implementation(libs.google.oauth)
}
