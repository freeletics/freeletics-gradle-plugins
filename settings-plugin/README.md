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
not necessary anymore to manually add `include("...")` for each project to `settings.gradle`. The requirement for this
to work is that the build file is named after the path. For example the project `:foo:bar` should have a build file
called `foo-bar.gradle` (or `foo-bar.gradle.kts`).

### `dependencyResolutionManagement`

The following repositories are automatically added to `dependencyResolutionManagement`:
- Maven Central
- the Google Maven Repository with `exclusiveContent` rules
- the Gradle plugin portal limited to `com.gradle.*` and `org.gradle.*`
- optionally the internal Freeletics repository if `freeleticsAndroidArtifactsUrl` is set
  - requires `freeleticsAndroidArtifactsAccessKey` and `freeleticsAndroidArtifactsSecretKey` properties to be set with valid S3 credentials

By default the project will also be configured to fail when repositories are defined on a project instead of through
`dependencyResolutionManagement`. This can be disabled by setting `fgp.kotlin.multiplatformProject=true` if the 
repository has Kotlin Multiplatform projects because of [this issue][3].

#### Snapshots

By adding the following snipped to `settings.gradle` it is possible to add various snapshot repositories to the project:

```groovy
freeletics {
    snapshots()
}
```

This adds:
- both Sonatype snapshot repositories
- the Jetpack Compose compiler snapshot repository
- the Kotlin bootstrap repository which contains dev builds

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

With the following snippet it is possible to configure an included build for [MAD][4] abd [FlowRedux][5] Freeletics 
open source projects.

```groovy
freeletics {
    includeMad("path/to/cloned/mad/repository") // path can be omitted if it is ../mad
    includeFlowRedux("path/to/cloned/flowredux/repository") // path can be omitted if it is ../flowredux
}
```

### Other

- enables the [type safe project accessor feature preview][2]
- configures the jvm toolchain management for auto provisioning using the [Foojay Toolchains Plugin][1]
- configures the Gradle enterprise plugin 


[1]: https://github.com/gradle/foojay-toolchains
[2]: https://docs.gradle.org/current/userguide/declaring_dependencies.html#sec:type-safe-project-accessors
[3]: https://youtrack.jetbrains.com/issue/KT-51379
[4]: https://github.com/freeletics/mad
[5]: https://github.com/freeletics/flowredux
