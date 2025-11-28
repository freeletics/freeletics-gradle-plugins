# Root plugin

Add the following to the root `build.gradle` or `build.gradle.kts` to apply the plugin:

```groovy
plugins {
    id("com.freeletics.gradle.root").version("<latest-version>")
}
```

## Features

### Java Platform

The root plugin applies the `java-platform` plugin and automatically adds all dependencies from
all version catalogs to it. This way subprojects can add the root project as a platform to enforce
versions across the whole repository. The monorepo plugins automatically use this.

### Dependency Analysis

Applies the [Dependency Analysis Gradle Plugin][1] and adds some default configuration for it:
- fail on any advice
- adds some default excludes based on default dependencies from the other plugins
- creates bundles for common libraries like compose

### Other

- Configures the `updateDaemonJvm` task to use `AZUL` as the JDK vendor.
