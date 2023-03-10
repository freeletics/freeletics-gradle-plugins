# Root plugin

Add the following to the root `build.gradle` or `build.gradle.kts` to apply the plugin:

```groovy
plugins {
    id("com.freeletics.gradle.root").version("<latest-version>")
}
```

## Features

### Enforce JDK

If the `libs` version catalog contains a version called `java-gradle` the plugin will make sure that Gradle is run
using the specified JDK version. If the validation is running it will also check that it is a Azul Zulu JDK. With this
it is possible to make sure that all team members run the same JDK.

### Dependency Analysis

Applies the [Dependency Analysis Gradle Plugin][1] and adds some default configuration for it:
- enable `ignoreKtx`
- fail on any advice
- adds some default excludes based on default dependencies from the other plugins
- creates bundles for common libraries like compose

[1]: https://github.com/autonomousapps/dependency-analysis-android-gradle-plugin
