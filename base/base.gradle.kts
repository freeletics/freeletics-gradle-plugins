plugins {
    alias(libs.plugins.fgp.jvm)
}

dependencies {
    compileOnly(libs.gradle.api)
    compileOnly(variantOf(libs.kotlin.gradle) { classifier("gradle76") }) {
        exclude("org.jetbrains.kotlin", "kotlin-gradle-plugin-api")
    }
    compileOnly(variantOf(libs.kotlin.gradle.api) { classifier("gradle76") })
    compileOnly(libs.android.gradle.api)
    compileOnly(libs.ksp)
    compileOnly(libs.anvil.gradle)
    compileOnly(libs.moshix.gradle)
}
