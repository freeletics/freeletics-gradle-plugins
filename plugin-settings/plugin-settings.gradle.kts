plugins {
    alias(libs.plugins.fgp.gradle)
    alias(libs.plugins.fgp.publish)
}

dependencies {
    implementation(libs.gradle.enterprise)
    implementation(libs.gradle.toolchain)
}

gradlePlugin {
    plugins {
        create("settingsPlugin") {
            id = "com.freeletics.gradle.settings"
            implementationClass = "com.freeletics.gradle.plugin.SettingsPlugin"
        }
    }
}
