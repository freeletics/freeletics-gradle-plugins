plugins {
    id("com.freeletics.gradle.jvm")
    id("com.freeletics.gradle.publish.oss")
}

dependencies {
    api(libs.kotlin.stdlib)
    api(libs.clikt)
    api(libs.clikt.core)
    implementation(libs.coroutines)
    implementation(libs.ktlint.rule.engine)
    implementation(libs.ktlint.rule.engine.core)
    implementation(libs.ktlint.rules)
    implementation(libs.ktfmt)
}
