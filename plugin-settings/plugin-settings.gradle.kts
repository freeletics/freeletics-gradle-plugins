plugins {
    id("com.freeletics.gradle.gradle")
}

freeletics {
    enableOssPublishing()
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
