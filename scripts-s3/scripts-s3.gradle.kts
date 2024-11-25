plugins {
    alias(libs.plugins.fgp.jvm)
    alias(libs.plugins.fgp.publish)
}

dependencies {
    api(libs.kotlin.stdlib)
    api(libs.clikt)
    api(libs.clikt.core)
    api(libs.google.play)
    implementation(libs.s3)
    implementation(libs.zxing)
}
