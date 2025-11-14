plugins {
    id("com.freeletics.gradle.gradle")
    id("com.freeletics.gradle.publish.oss")
}

dependencies {
    implementation(libs.develocity.gradle)
    implementation(libs.toolchains.gradle)
}

gradlePlugin {
    plugins {
        create("settingsPlugin") {
            id = "com.freeletics.gradle.settings"
            implementationClass = "com.freeletics.gradle.plugin.SettingsPlugin"
        }
    }
}
