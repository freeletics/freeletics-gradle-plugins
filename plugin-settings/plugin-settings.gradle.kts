plugins {
    alias(libs.plugins.fgp.gradle)
    alias(libs.plugins.fgp.publish)
}

dependencies {
    implementation(libs.develocity)
    implementation(libs.gradle.toolchain)
    implementation(libs.versionCatalog)
}

gradlePlugin {
    plugins {
        create("settingsPlugin") {
            id = "com.freeletics.gradle.settings"
            implementationClass = "com.freeletics.gradle.plugin.SettingsPlugin"
        }
    }
}
