# Base plugins

This is a collection of plugins that provide a set of defaults and automatic configuration together with extensions
to make configuring projects easy.


## Shared configuration

The following configuration is available in all the project types listed in the sections below.

### Default dependencies

The plugins can dynamically add default dependencies to all modules. This can be enabled
by creating any of the following bundles:

```toml
[bundles]
# any dependency in this bundle is automatically added to all modules as implementation dependency
default-all = [ ... ]
# any dependency in this bundle is automatically added to all modules as compileOnly dependency
default-all-compile = [ ... ]
# any dependency in this bundle is automatically added to all Android modules as implementation dependency
default-android = [ ... ]
# any dependency in this bundle is automatically added to all Android modules as compileOnly dependency
default-android-compile = [ ... ]
# any dependency in this bundle is automatically added to all modules as testImplementation dependency
default-testing = [ ... ]
# any dependency in this bundle is automatically added to all modules as testCompileOnly dependency
default-testing-compile = [ ... ]
# any dependency in this bundle is automatically added to all modules as testRuntimeOnly dependency
default-testing-runtime = [ ... ]
# any dependency in this bundle is automatically added to all modules as lintChecks dependency
default-lint = [ ... ]
```

### Kotlin

```groovy
freeletics {
    // enable explicit api mode
    explicitApi()
    // opt in to experimental APIs for this project
    optIn("...", "...")
}
```

### Compose

This will enable Compose on the project and works on Android, JVM and multiplatform projects. For the latter 2 the
`org.jetbrains.compose` plugin needs to be on the classpath.

```groovy
freeletics {
    // requires `androidx.compose.compiler` to be present in the libs version catalog
    // supports suppressing the Kotlin version check by setting `fgp.compose.kotlinVersion=<kotlin-version>`
    useCompose()
}
```

Add the following to the `libs` version catalog:
```toml
[libraries]
# for Android projects
androidx-compose-compiler = { module = "androidx.compose.compiler:compiler", version = "..." }
# for non-Android or multiplatform projects
jetbrains-compose-compiler = { module = "org.jetbrains.compose.compiler:compiler", version = "..." }
```

There are a few optional Gradle properties for certain compose compiler options:
```properties
# Suppress the Kotlin version in the compiler for the given Kotlin version
fgp.compose.kotlinVersion=<kotlin-version>
# Set these to enable compiler metrics and/or reports, they will be located in the modules build folder
fgp.compose.enableCompilerMetrics=true
fgp.compose.enableCompilerReports=true
```

### Moshi

The following method will apply the [MoshiX plugin][2] (`dev.zacsweers.moshix`) which will enable Moshi code generation
through a compiler plugin and also adds the required dependencies on Moshi.

```groovy
freeletics {
    // the boolean will determine whether moshix-sealed is enabled as well, false if not specified
    useMoshi(true)
}
```

### Dagger/Anvil

It is possible to easily configure [Dagger][3] and [Anvil][4] with the following methods. Of the 3 available options
choose the one that is most appropriate for a module. The simple `useDagger` is the most performant one because it
does not require KAPT. There is also a method to configure the [Khonshu][5] Anvil plugin.

```groovy
freeletics {
    // applies Anvil and will use it for all code generation (KAPT is not used)
    // for modules with @Component interfaces use `useDaggerWithComponent()` instead
    useDagger()
    // same as the above but will also add the Khonshu Anvil plugin
    useDaggerWithKhonshu()
    // applies Anvil and KAPT
    useDaggerWithComponent()
}
```

Add the following to the `libs` version catalog:
```toml
[libraries]
# these will be automatically added as dependencies
inject = { module = "javax.inject:javax.inject", version = "..." }
dagger = { module = "com.google.dagger:dagger", version = "..." }
anvil-annotations = { module = "com.squareup.anvil:annotations", version = "..." }
anvil-annotations-optional = { module = "com.squareup.anvil:annotations-optional", version = "..." }
anvil-compiler = { module = "com.squareup.anvil:compiler", version = "..." }
# optional, if present it will be automatically added as a dependency
khonshu-codegen-runtime = { module = "com.freeletics.khonshu:codegen-runtime", version = "..." }
# only for `useDaggerWithComponent()`
dagger-compiler = { module = "com.google.dagger:dagger-compiler", version = "..." }
# only for `useDaggerWithKhonshu()`
khonshu-codegen-compiler = { module = "com.freeletics.khonshu:codegen-compiler", version = "..." }
```


## Android projects

Applies:
- `com.android.library`
- `org.jetbrains.kotlin.android`
- `com.autonomousapps.dependency-analysis`

General features:
- sets `android.namespace` based on the module name
- configures compile, min sdk, Java target and Java/Kotlin toolchain versions from the version catalog
- configures default options for the Kotlin compiler
- enables `coreLibraryDesugaring` based on the version catalog
- disables all `android.buildFeatures` and offers a DSL to enable and configure them if needed
- configures unit tests
  - disables them for the `release` build type
  - reports are written to `<repo>/build/reports/tests` to make collecting them easier
- configures lint
  - uses the lint config in `<repo>/gradle/lint.xml`
  - enables `checkDependencies`
  - makes the build fail on errors and warnings
  - reports are written to `<repo>/build/reports/lint` to make collecting them easier

### Setup

```groovy
plugins {
    // for library projects
    id("com.freeletics.gradle.android").version("<latest-version>")
    // for app projects
    id("com.freeletics.gradle.android.app").version("<latest-version>")
}
```

Add the following to `gradle.properties`:
```properties
# used to automatically set `android.namespace` based on the project name
# e.g. `:foo` would use `com.example.foo` as namespace
fgp.android.namespacePrefix=com.example
```

Add the following to the `libs` version catalog:
```toml
[versions]
# the Java version that the Java and Kotlin compilers will target
java-target = "11"
# the Java version that is used to run the Java and Kotlin compilers and various other tasks
java-toolchain = "17"

# optional, the Kotlin language version to use
kotlin-language = "1.8"

# the Android minSdkVersion to use
android-min = "26"
# the Android targetSdkVersion to use, only for app modules
android-target = "33"
# the Android compileSdkVersion to use
android-compile = "33"
# optional, the Android build tools version to use
android-buildTools = "33.0.2"

[libraries]
# if this is present coreLibraryDesugaring will be enabled and this dependency is automatically added
android-desugarjdklibs = { module = "com.android.tools:desugar_jdk_libs", version = "..." }
```

### Android build features

The plugin will by default disable most Android build features and offers an extension to enable them:

```groovy
freeletics {
    android {
        // apply the Kotlin parcelize plugin
        enableParcelize()
        // enables Android resource support
        enableAndroidResources()
        // enables ViewBinding generation
        enableViewBinding()
        // enables BuildConfig generation
        enableBuildConfig()
        // create a BuildConfig field with the given value
        buildConfigField("type", "name", "value")
        // create a BuildConfig field with separate values for debug and release
        buildConfigField("type", "name", "debug value", "release value")
        // enables res values generation
        enableResValues()
        // create a res value with the given value
        resValue("type", "name", "value")
        // create a res value with separate values for debug and release
        resValue("type", "name", "debug value", "release value")
    }
}
```

### Paparazzi

To apply the paparazzi plugin and configure it call:

```groovy
freeletics {
    android {
        usePaparazzi()
    }
}
```

### Room

To easily add room as a dependency and apply KSP the following extension method can be used.

```groovy
freeletics {
    android {
        useRoom()
    }
}
```

Add the following to the `libs` version catalog:
```toml
androidx-room-runtime = { module = "androidx.room:room-runtime", version = "..." }
androidx-room-compiler = { module = "androidx.room:room-compiler", version = "..." }
```


## Kotlin/JVM Library projects

Applies:
- `org.jetbrains.kotlin.jvm`
- `com.autonomousapps.dependency-analysis`

General features:
- configures Java target and Java/Kotlin toolchain versions from the version catalog
- configures default options for the Kotlin compiler
- configures unit tests
  - reports are written to `<repo>/build/reports/tests` to make collecting them easier

### Setup

```groovy
plugins {
    id("com.freeletics.gradle.jvm").version("<latest-version>")
}
```

Add the following to the `libs` version catalog:
```toml
[versions]
# the Java version that the Java and Kotlin compilers will target
java-target = "11"
# the Java version that is used to run the Java and Kotlin compilers and various other tasks
java-toolchain = "17"

# optional, the Kotlin language version to use
kotlin-language = "1.8"
```

### Android Lint

To apply the Android Lint plugin and configure it call:

```groovy
freeletics {
    jvm {
        useAndroidLint()
    }
}
```


## Kotlin/Multiplatform Library projects

Applies:
- `org.jetbrains.kotlin.multiplatform`
- `com.autonomousapps.dependency-analysis`

General features:
- configures Java target and Java/Kotlin toolchain versions from the version catalog
- configures default options for the Kotlin compiler
- configures unit tests
  - reports are written to `<repo>/build/reports/tests` to make collecting them easier

### Setup

```groovy
plugins {
    id("com.freeletics.gradle.multiplatform").version("<latest-version>")
}
```

Add the following to the `libs` version catalog:
```toml
[versions]
# the Java version that the Java and Kotlin compilers will target
java-target = "11"
# the Java version that is used to run the Java and Kotlin compilers and various other tasks
java-toolchain = "17"

# optional, the Kotlin language version to use
kotlin-language= "1.8"
```

### Adding targets

The following extension methods make it easy to add multiplatform targets to the project:

```groovy
freeletics {
    multiplatform {
        // adds all targets that a also supported by the coroutines project
        // has a `androidNativeTargets` boolean parameter to control adding androidNative* targets (defaults to enabled)
        addCommonTargets()
        // adds jvm as a target
        addJvmTarget()
        // adds Android as a target and automatically adds the Android Library plugin and common Android config
        // has a `publish` boolean parameter to control adding whether the target should be published (defaults to enabled)
        addAndroidTarget(true)
        // adds `iosArm64`, `iosX64`, `iosSimulatorArm64` as targets and creates shared iosMain and iosTest source sets
        addIosTargets("frameworkName")
        // same as above but will also configure everything to create a XCFramework
        addIosTargets("frameworkName", true)
    }
}
```


## Gradle plugin projects

Applies:
- `java-gradle-plugin`
- `org.jetbrains.kotlin.jvm`
- `com.autonomousapps.dependency-analysis`

General features:
- configures Java target and Java/Kotlin toolchain versions from the version catalog
- configures default options for the Kotlin compiler
- configures unit tests
    - reports are written to `<repo>/build/reports/tests` to make collecting them easier
- generates a `VERSION` constant that contains the current plugin version
  - can be used if the plugin needs to add dependencies on other artifacts published together with the plugin
  - uses the `GROUP` and `POM_ARTIFACT_ID` Gradle properties for the package name

### Setup

```groovy
plugins {
    id("com.freeletics.gradle.gradle").version("<latest-version>")
}
```

Add the following to the `libs` version catalog:
```toml
[versions]
# the Java version that the Java and Kotlin compilers will target
java-target = "11"
# the Java version that is used to run the Java and Kotlin compilers and various other tasks
java-toolchain = "17"

# optional, the Kotlin language version to use
kotlin-language = "1.8"
```

### Android Lint

To apply the Android Lint plugin and configure it call:

```groovy
freeletics {
    jvm {
        useAndroidLint()
    }
}
```


[2]: https://github.com/ZacSweers/MoshiX
[3]: https://github.com/google/dagger
[4]: https://github.com/square/anvil
[5]: https://freeletics.github.io/khonshu/codegen/get-started/
