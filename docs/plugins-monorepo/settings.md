# Settings plugin

Add the following to `settings.gradle` or `settings.gradle.kts` to apply the plugin:

```groovy
plugins {
    id("com.freeletics.gradle.settings").version("<latest-version>")
}
```

## Features

### Project discovery

The plugin will automatically discover all projects by finding their build files and include them. This means it is
not necessary anymore to manually add `include("...")` for each project to `settings.gradle.kts`. The requirement for this
to work is that the build file is named after the path. For example the project `:foo:bar` should have a build file
called `foo-bar.gradle.kts` (or `foo-bar.gradle`). `

Instead of using project root directory as a base for automatic discoverability it is possible to only include
certain subdirectories by adding `fgp.discoverProjects.automatically=false` to project's `gradle.properties` and adding
the following:

```kotlin
freeletics {
    discoverProjectsIn("app", "features")
}
```

### `dependencyResolutionManagement`

The project will be configured to fail when repositories are defined on a project instead of through
`dependencyResolutionManagement`.

The following repositories are automatically added to `dependencyResolutionManagement`:
- Maven Central
- the Google Maven Repository with `exclusiveContent` rules
- the Gradle plugin portal limited to `com.gradle.*` and `org.gradle.*`
- the Kotlin JS and WASM repositories

If `fgp.internalArtifacts.url` is set a repository for that URL is created. The content of that repository is limited
to the group regex specified by `fgp.internalArtifacts.regex`. The username and password for this repository are expected
to be set through `internalArtifactsUsername` and `internalArtifactsPassword`.

### Snapshots

By adding the following snipped to `settings.gradle` it is possible to add various snapshot repositories to the project:

```groovy
freeletics {
    snapshots()
    // or to include one of the AndroidX snapshot repositories from https://androidx.dev/snapshots/builds use
    snapshots("<build id>")
}
```

This adds:
- both Maven Central snapshot repository
- the Kotlin bootstrap repository which contains dev builds
- the AndroidX snapshot repository if a build id was specified
- maven local (`~/.m2`)

### Build cache

If the Gradle build cache is generally enabled the plugin allows configuring it through Gradle properties. By default
the local build cache is enabled but can be disabled by setting `fgp.buildcache.local=false`.

The remote build cache can be enabled and configured like this:
```properties
fgp.buildcache.remote=true
fgp.buildcache.url=https://...
fgp.buildcache.push=true
fgp.buildcache.username=...
fgp.buildcache.password=...
```

### Included builds

With the following snippet it is possible to configure an included build for [Khonshu][4] and [FlowRedux][5] Freeletics
open source projects.

```groovy
freeletics {
    includeKhonshu("path/to/cloned/khonshu/repository") // path can be omitted if it is ../khonshu
    includeFlowRedux("path/to/cloned/flowredux/repository") // path can be omitted if it is ../flowredux
}
```

### Other

- [Type-safe project accessors][2] are enabled by default.
- [Strict configuration cache][3] is enabled by default.
- Configures the jvm toolchain management for auto provisioning using the [Foojay Toolchains Plugin][1]
- Configures the Gradle enterprise plugin


[1]: https://github.com/gradle/foojay-toolchains
[2]: https://docs.gradle.org/current/userguide/declaring_dependencies_basics.html#sec:type-safe-project-accessors
[3]: https://docs.gradle.org/current/userguide/configuration_cache_enabling.html#config_cache:stable
[4]: https://github.com/freeletics/khonshu
[5]: https://github.com/freeletics/flowredux
