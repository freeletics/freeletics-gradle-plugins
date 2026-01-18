# App plugin

Add the following to the root `build.gradle` or `build.gradle.kts` to apply the plugin:

```groovy
plugins {
    id("com.freeletics.gradle.app.android").version("<latest-version>")
}
```

## Default configurations

--8<-- "docs/defaults/base-1-configuration.md"
--8<-- "docs/defaults/base-2-default-dependencies.md"
--8<-- "docs/defaults/android-app-1-config.md"

## Features

--8<-- "docs/features/base-1-explicit-api.md"
--8<-- "docs/features/base-2-opt-in.md"
--8<-- "docs/features/base-3-compose.md"
--8<-- "docs/features/base-4-serialization.md"
--8<-- "docs/features/base-5-metro.md"
--8<-- "docs/features/base-6-khonshu.md"
--8<-- "docs/features/base-6-poko.md"
--8<-- "docs/features/base-7-kopy.md"
--8<-- "docs/features/base-8-sqldelight.md"
--8<-- "docs/features/base-9-room.md"
--8<-- "docs/features/base-10-burst.md"
--8<-- "docs/features/base-11-oss-publishing.md"
--8<-- "docs/features/base-12-internal-publishing.md"
--8<-- "docs/features/android-app-1-buildconfig.md"
--8<-- "docs/features/android-app-2-resvalue.md)

### Android options

```groovy
freeletics {
    app {
        // the application id that will be used for the app
        applicationId("com.example")
        // sets an application id suffix for the given build type
        applicationIdSuffix("release", ".suffix")
        // resources will be limited to the given locales
        limitLanguagesTo("en", "de", "fr")
        // enable minification with R8 and use the given proguard files as additional config (parameters are optional)
        minify(
                rootProject.file("proguard/library1.pro"),
                rootProject.file("proguard/library2.pro"),
        )
    }
}
```

### Git based versioning

When enabled the `versionName` and `versionCode` will be computed based on information from Git.
```groovy
freeletics {
    app {
        // the passed in value will be used for matching git tags and branches as described below
        // can be anything identifying an app, e.g. `fl` for `freeletics` or just `freeletics` directly
        versionFromGit("<short-app-name>")
    }
}
```

The version information can come from:
- a tag for the current commit in the format of `<short-app-name>/v<app-version>` -> `<app-version>` is used as version name
- otherwise the output of `git describe` (`<short-app-name>/v<last-app-version>-<commits-since-tag>-<current-commit-sha>`) is used which would result in `<last-app-version>-<commits-since-tag>-<current-commit-sha>`

The version code is then computed by taking the version and applying the following formula
```md
<major> * 1_000_000 + <minor> * 10_000 + <patch>
```

For builds on the main branch when the current commit is not tagged, the following formula is used:
```md
<major> * 1_000_000 + <minor> * 10_000 + <day_of_week> * 1_000 + <commits since last release>
```


Also creates `BuildConfig.GIT_SHA1`, `BuildConfig.BUILD_TIMESTAMP` fields containing information from the current commit.

To not break incremental builds and build cache `versionName`, `versionCode` and the 2 build config fields will default
to constants. The computation will only happen if `-Pfgp.computeInfoFromGit=true` is passed to the build.

### License checks

Applies [Licensee][1] (`app.cash.licensee`) and already configures it to accept `Apache-2.0`, `MIT` and `MIT-0` licenses.

```groovy
freeletics {
    app {
        checkLicenses()
    }
}
```

This will also register a task `updateLicenses` that will copy the output of `licenseeRelease` to
`src/main/assets/license_acknowledgements.json` and strip the `version` information from that file.
This file will be shipped wit the app and can be used as data source for a license acknowledgement screen
in the app.

### Crashlytics

Applies the Crashlytics Gradle plugin and configures it for the release build type. This configuration
does not require the Google services Gradle plugin but it expects `src/debug/res/values/google-services.xml`
and `src/release/res/values/google-services.xml` to exist. The Crashlytics mapping upload will only be enabled
when `-Pfgp.computeInfoFromGit=true` is passed to the build.

```groovy
freeletics {
    app {
        crashReporting()
    }
}
```

There will also be a generated `BuildConfig.CRASHLYTICS_ENABLED` boolean field that will only be `true` if the mapping
upload was enabled.

[1]: https://github.com/autonomousapps/dependency-analysis-android-gradle-plugin
