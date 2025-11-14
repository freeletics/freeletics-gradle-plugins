plugins {
    id("com.freeletics.gradle.multiplatform")
}

freeletics {
    enableOssPublishing()
    multiplatform {
        addJvmTarget()
    }
}
