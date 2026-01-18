# Monorepo plugins

These plugins are used in the Freeletics monorepo. They are built on top of the same foundation as the common
plugins but are more opinionated and tailored to the structure of our codebase.

## Structure

The codebase is heavily modularized and has the following groups of modules. For each group of modules there is a
specific Gradle plugin.

- `app`: The actual Android apps
    - Gradle plugin: `com.freeletics.gradle.app`
    - Should be as small as possible and generally just aggregate their dependencies
- `core`: A generic building block that can be used to build features
    - Gradle plugin: `com.freeletics.gradle.core`
    - Should not contain anything Freeletics-specific (API clients/models, business logic, analytics)
    - Core modules are split into 2 actual modules, `core:*:api` and `core:*:implementation`. API contains only the
      parts that are used directly by other modules, which in many cases is just an interface. The implementation
      as well as potential Dagger contributions go into the implementation module. This is done to keep
      implementation classes as well as dependencies only needed by the implementation hidden from consumers of the
      API.
- `domain`: A Freeletics-specific building block that can be used to build features
    - Gradle plugin: `com.freeletics.gradle.domain`
    - Very similar to `core` but with Freeletics-specific code. We've split this out of `core` because the number of
      core modules got so big that it was hard to find what you wanted.
    - Domain modules have the same api/implementation split as `core` modules
- `feature:*:implementation`: A feature for the app
    - Gradle plugin: `com.freeletics.gradle.feature`
    - This is usually a single screen of the app with its UI and logic.
- `feature:*:nav`
    - Gradle plugin: `com.freeletics.gradle.nav`
    - Contains a `NavRoute` class that has all the required arguments to navigate to the screen of the corresponding
      `feature` module. Other `feature` modules can use this to navigate to it.

There are rules of which type of module is allowed on which other type:
![Module dependencies](images/project-structure.png)
*The dotted line means the dependency is optional and only present if needed while a regular line signifies a dependency
that always exists*

In case the monorepo contains multiple apps, the `domain` and `feature` groups can be split up into multiple app-specific
groups. For that, a suffix can be added with a `-` to the top-level folder. The suffix should equal the name of the app
module. For example with `:app:foo` and `:app:bar` anything inside `:domain` and `:feature` can be used by either app
while `:domain-foo` and `:feature-foo` would be for `:app:foo` and `:domain-bar` and `:feature-bar` for `:app:bar`.

In addition to the `api` and `implementation` modules there are 2 other types for each category. `debug` modules that
can provide debug-only functionality and `testing` modules that can provide test helpers or fake implementations of the
`api` module.

Each module has a `checkDependencyRules` task that will ensure that it only depends on modules that it is allowed to
depend on based on the rules above. This includes checks based on the module group (e.g. an `implementation` module
is not allowed to depend on another `implementation` module) and based on the app (e.g. `:app:bar` or
`feature-bar:...:implementation` are not allowed to depend on a `domain-foo` module).

## Getting started

For the monorepo Gradle plugins to work certain Gradle properties and version catalog entries are required.

Add the following to `gradle.properties`:
```properties
# used to automatically set `android.namespace` based on the project name
# e.g. `:foo` would use `com.example.foo` as namespace
fgp.defaultPackageName=com.example

# adds an Android target to all modules, defaults to false
fgp.kotlin.targets.android=true
# adds a JVM target to all modules, defaults to false
fgp.kotlin.targets.jvm=true
# adds an iOS target to all modules, defaults to false
fgp.kotlin.targets.ios=true
```

Add the following to the `libs` version catalog:
```toml
[versions]
# the Java version that the Java and Kotlin compilers will target
java-target = "17"
# the Java version that is used to run the Java and Kotlin compilers and various other tasks
java-toolchain = "25"

# optional, the Kotlin language version to use, if not specified the default will be used
kotlin-language = "2.3"

# the Android minSdkVersion to use
android-min = "26"
# the Android target to use
android-target = "36"
# the Android compileSdkVersion to use
android-compile = "36"

[libraries]
# if this is present coreLibraryDesugaring will be enabled and this dependency is automatically added
android-desugarjdklibs = { module = "com.android.tools:desugar_jdk_libs", version = "..." }


# the following bundles are optional but provide a way to add default dependencies to all modules
[bundles]
# any dependency in this bundle is automatically added to all modules as implementation dependency
default-all = [ ]
# any dependency in this bundle is automatically added to all modules as compileOnly dependency
default-all-compile = [ ]
# any dependency in this bundle is automatically added to all Android modules as implementation dependency
default-android = [ ]
# any dependency in this bundle is automatically added to all Android modules as compileOnly dependency
default-android-compile = [ ]
# any dependency in this bundle is automatically added to all modules as testImplementation dependency
default-testing = [ ]
# any dependency in this bundle is automatically added to all modules as testCompileOnly dependency
default-testing-compile = [ ]
# any dependency in this bundle is automatically added to all modules as testRuntimeOnly dependency
default-testing-runtime = [ ]
# any dependency in this bundle is automatically added to all modules as lintChecks dependency
default-lint = [ ]
```

Add the [settings plugin](plugins-monorepo/settings.md) to the `settings.gradle` or `settings.gradle.kts` file:
```groovy
plugins {
    id("com.freeletics.gradle.settings").version("<latest-version>")
}
```

Add the [root plugin](plugins-monorepo/root.md) to the root `build.gradle` or `build.gradle.kts` file:
```groovy
plugins {
    id("com.freeletics.gradle.root").version("<latest-version>")
}
```

After this it's possible to start creating modules in the structure described above. With the settings
plugin applied, individual modules don't need to be included manually in the settings.gradle(.kts) file, they are
automatically picked up if the file is named after the path. For example instead of having `app/example/build.gradle`
the file can be named `app/example/app-example.gradle` and it will be automatically included.

The individual module categories have their own Gradle plugins. Check out their docs for more information on them:
- [app plugin](plugins-monorepo/app.md)
- [core plugin](plugins-monorepo/core.md)
- [domain plugin](plugins-monorepo/domain.md)
- [feature plugin](plugins-monorepo/feature.md)
- [nav plugin](plugins-monorepo/nav.md)
