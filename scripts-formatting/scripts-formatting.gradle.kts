plugins {
    id("com.freeletics.gradle.multiplatform")
}

freeletics {
    enableOssPublishing()
    multiplatform {
        addJvmTarget()
    }
}

dependencies {
    "jvmMainApi"(libs.kotlin.stdlib)
    "jvmMainApi"(libs.clikt)
    "jvmMainApi"(libs.clikt.core)
    "jvmMainImplementation"(libs.coroutines)
    "jvmMainImplementation"(libs.ktlint.rule.engine)
    "jvmMainImplementation"(libs.ktlint.rule.engine.core)
    "jvmMainImplementation"(libs.ktlint.rules)
    "jvmMainImplementation"(libs.ktfmt)
    "jvmMainImplementation"(libs.gradle.sorter)
}
