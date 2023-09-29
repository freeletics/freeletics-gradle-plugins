plugins {
    alias(libs.plugins.fgp.gradle)
    alias(libs.plugins.fgp.publish)
}

dependencies {
    compileOnly(libs.dependency.analysis)
}

gradlePlugin {
    plugins {
        create("rootPlugin") {
            id = "com.freeletics.gradle.root"
            implementationClass = "com.freeletics.gradle.plugin.RootPlugin"
        }
    }
}
