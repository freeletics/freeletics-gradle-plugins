### Adding multiplatform targets

There are several functions to easily add targets to the module:

```kotlin
freeletics {
    multiplatform {
        // adds all targets that a also supported by the coroutines project
        // has a `limitToComposeTargets` boolean parameter that can be set to true to only add targets supported by
        // the compose runtime
        addCommonTargets()
        // adds jvm as a target
        addJvmTarget()
        // adds Android as a target
        addAndroidTarget {
            // Android DSL options
        }
        // adds `iosArm64`, `iosSimulatorArm64` as targets
        // has a `includeX64` boolean parameter to also include `iosX64`
        addIosTargets()
        // same as above but will also configure everything to create a XCFramework
        addIosTargetsWithXcFramework("frameworkName") { framework ->
            // optionally configure the framework
        }
    }
}
```
