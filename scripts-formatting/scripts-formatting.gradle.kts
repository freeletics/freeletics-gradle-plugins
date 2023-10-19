plugins {
    alias(libs.plugins.fgp.jvm)
    alias(libs.plugins.fgp.publish)
}

dependencies {
    api(libs.kotlin.stdlib)
    api(libs.clikt)
    implementation(libs.coroutines)
    implementation(libs.ktlint.rule.engine)
    implementation(libs.ktlint.rule.engine.core)
    implementation(libs.ktlint.rules)
}
