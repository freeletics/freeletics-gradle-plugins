plugins {
    alias(libs.plugins.fgp.gradle)
    alias(libs.plugins.fgp.publish)
}

configurations.named("shadeClassPath").configure {
    // included in gradle-api and would cause duplicate class issues
    exclude(mapOf("group" to "com.google.code.findbugs", "module" to "jsr305"))
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
