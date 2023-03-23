# Common plugins

This is a collection of plugins that provide a set of defaults and automatic configuration together with extensions
to make configuring projects easy.


## Shared configuration

The following configuration is available in all the project types listed in the sections below.


### Kotlin

```groovy
freeletics {
    // enable explicit api mode
    explicitApi()   
    // opt in to experimental APIs for this project
    optIn("...", "...")
}
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
does not require KAPT. There is also a method to configure the [Whetstone][5] Anvil plugin.

```groovy
freeletics {
    // applies Anvil and will use it for all code generation (KAPT is not used)
    // for modules with @Component interfaces use `useDaggerWithComponent()` instead 
    useDagger()
    // same as the above but will also add the Whetstone Anvil plugin
    useDaggerWithWhetstone()
    // applies Anvil and KAPT
    useDaggerWithComponent()
}
```

Add the following to the `libs` version catalog:
```toml
[libraries]
# these will be automatically added as dependencies
inject = { module = "javax.inject:javax.inject", version = "..." }
anvil-annotations = { module = "com.squareup.anvil:annotations", version = "..." }
dagger = { module = "com.google.dagger:dagger", version = "..." }
# only for `useDaggerWithComponent()`
dagger-compiler = { module = "com.google.dagger:dagger-compiler", version = "..." }
# only for `useDaggerWithWhetstone()`
fl-whetstone-scope = { module = "com.freeletics.mad:whetstone-scope", version = "..." }
fl-whetstone-compiler = { module = "com.freeletics.mad:whetstone-compiler", version = "..." }
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
    id("com.freeletics.gradle.common.android").version("<latest-version>")
    // for app projects
    id("com.freeletics.gradle.common.android.app").version("<latest-version>")
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
kotlin-language = "1.8.10"

# the Android minSdkVersion to use
android-min = "26"
# the Android targetSdkVersion to use, only for app modules
android-target = "33"
# the Android compileSdkVersion to use
android-compile = "33"

[libraries]
# if this is present coreLibraryDesugaring will be enabled and this dependency is automatically added 
android-desugarjdklibs = { module = "com.android.tools:desugar_jdk_libs", version = "..." }
```

### Android build features

The plugin will by default disable most Android build features and offers an extension to enable them:

```groovy
freeletics {
    // apply the Kotlin parcelize plugin
    enableParcelize()
    // enables Compose and configures the compiler
    // requires `androidx.compose.compiler` to be present in the libs version catalog
    // supports suppressing the Kotlin version check by setting `fgp.compose.kotlinVersion=<kotlin-version>`
    enableCompose()
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
    // enable Android Tests for the debug build type (release will stay disabled)
    // the parameters are optional
    enableAndroidTests(
        "testInstrumentationRunner", // defaults to `androidx.test.runner.AndroidJUnitRunner`
        "testInstrumentationRunnerArguments", // defaults to `mapOf("clearPackageData" to "'true'")`
        "execution", // defaults to `ANDROIDX_TEST_ORCHESTRATOR`
        "animationsDisabled", // defaults to `true`
    )
}
```

### Paparazzi

To apply the paparazzi plugin and configure it call:

```groovy
freeletics {
    usePaparazzi()
}
```

### Room

To easily add room as a dependency and apply KSP or KAPT the following extension method can be used. By default ksp is 
used, to use kapt set this gradle.property: `fgp.kotlin.ksp=false`.

```groovy
freeletics {
    // add room as a dependency and configure kapt/ksp
    // 
    // requires `androidx-room-runtime` and `androidx-room-compiler` to be present in the version catalog
    useRoom()
}
```

Requires `androidx-room-runtime` and `androidx-room-compiler` to be present in the `libs` version catalog.


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
    id("com.freeletics.gradle.common.jvm").version("<latest-version>")
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
kotlin-language = "1.8.10"
```

### Android Lint

To apply the Android Lint plugin and configure it call:

```groovy
freeletics {
    useAndroidLint()
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
    id("com.freeletics.gradle.common.multiplatform").version("<latest-version>")
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
kotlin-language= "1.8.10"
```

### Adding targets

The following extension methods make it easy to add multiplatform targets to the project:

```groovy
freeletics {
    // adds jvm as a target
    addJvmTarget()
    // adds `iosArm64`, `iosX64`, `iosSimulatorArm64` as targets and creates shared iosMain and iosTest source sets
    addIosTargets("frameworkName")
    // same as above but will also configure everything to create a XCFramework
    addIosTargets("frameworkName", true)
    // adds all targets that a also supported by the coroutines project
    addCommonTargets()
}
```


## Gradle plugin projects

Applies:
- `java-gradle-plugin`
- `org.jetbrains.kotlin.jvm`
- `com.gradleup.gr8`
- `com.autonomousapps.dependency-analysis`
- `com.autonomousapps.plugin-best-practices-plugin`

General features:
- configures Java target and Java/Kotlin toolchain versions from the version catalog
- configures default options for the Kotlin compiler
- configures unit tests
    - reports are written to `<repo>/build/reports/tests` to make collecting them easier
- generates a `VERSION` constant that contains the current plugin version
  - can be used if the plugin needs to add dependencies on other artifacts published together with the plugin 
  - uses the `GROUP` and `POM_ARTIFACT_ID` Gradle properties for the package name
- configures [GR8][1] for shading

### Setup

```groovy
plugins {
    id("com.freeletics.gradle.common.gradle").version("<latest-version>")
}

dependencies
```

Add the following to `gradle.properties`:
```properties
kotlin.stdlib.default.dependency=false

# used for the package name of the generated VERSION constant, usually also used for publishing
GROUP=com.example
POM_ARTIFACT_ID=gradle-plugin
```

Add the following to the `libs` version catalog:
```toml
[versions]
# the Java version that the Java and Kotlin compilers will target
java-target = "11"
# the Java version that is used to run the Java and Kotlin compilers and various other tasks
java-toolchain = "17"

# optional, the Kotlin language version to use
kotlin-language = "1.8.10"

[libraries]
# will automatically be added and shaded
kotlin-stdlib = { module = "org.jetbrains.kotlin:kotlin-stdlib", version = "..." }
# the Gradle API to build against, will be automatically added as a compileOnly dependency
gradle-api = { module = "dev.gradleplugins:gradle-api", version = "..." }
```

Create a file called `rules.pro` in the root of the project and/or next to the projects build file containing (in
a multi project setup it is possible to split the config and put the shared parts into the root `rules.pro` file):
```
# The Gradle API jar isn't added to the classpath, ignore the missing symbols
-ignorewarnings
# Allow to make some classes public so that we can repackage them without breaking package-private members
-allowaccessmodification

# Keep kotlin metadata so that the Kotlin compiler knows about top level functions and other things
-keep class kotlin.Metadata { *; }

# Keep FunctionX because they are used in the public API of Gradle/AGP/KGP
-keep class kotlin.jvm.functions.** { *; }

# Keep Unit for kts compatibility, functions in a Gradle extension returning a relocated Unit won't work
-keep class kotlin.Unit

# We need to keep type arguments (Signature) for Gradle to be able to instantiate abstract models like `Property`
-keepattributes Signature,Exceptions,*Annotation*,InnerClasses,PermittedSubclasses,EnclosingMethod,Deprecated,SourceFile,LineNumberTable

# Keep your public API so that it's callable from scripts
-keep class com.example.** { *; }

# No need to obfuscate class names
-dontobfuscate

# Package that shaded classes are moved to
-repackageclasses com.example.relocated
```

### Android Lint

To apply the Android Lint plugin and configure it call:

```groovy
freeletics {
    useAndroidLint()
}
```


[1]: https://github.com/GradleUp/gr8
[2]: https://github.com/ZacSweers/MoshiX
[3]: https://github.com/google/dagger
[4]: https://github.com/square/anvil
[5]: https://freeletics.github.io/mad/whetstone/get-started/
