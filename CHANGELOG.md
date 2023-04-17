Change Log
==========

## 0.3.1 **[2023-04-17]**

- Fixes exception when running `generatePluginVersion`


## 0.3.0 **[2023-04-17]**

- Changes for Kotlin 1.8.20 and AGP 8.0
- Enable `STABLE_CONFIGURATION_CACHE` feature preview by default, can be disabled with `fgp.stableConfigurationCache=false`
- Improve default repository filters
  - Stop trying to load snapshots from maven central
  - Only load snapshots from snapshot repositories
  - Added ability to pass an AndroidX snapshot repository id to the `snapshot` function
  - Fix compose compiler snapshots not working because of exclusiveContent 
- `com.freeletics.gradle.common.publish.oss`: automatically enable `explicitApi` mode
- Fix configuration caching for `generatePluginVersion` task
- Improved gr8 config to reduce warnings 
- Kotlin compiler options are now configured for `KotlinCompilationTask` instead of `KotlinCompile`
  which will include compilation of non jvm targets
- Added `com.freeletics.gradle:scripts` which contains classes to run ktlint from a kts script


## 0.2.3 **[2023-03-24]**

- Make uploading native symbols to Crashlytics optional by adding a boolean parameter to `enableCrashReporting(...)`. This
  defaults to `false`.
- When using the oss publish plugin automatically apply Dokka and disable Dokka for `-SNAPSHOT` builds.


## 0.2.2 **[2023-03-22]**

- don't create a `clean` task in the root plugin


## 0.2.1 **[2023-03-22]**

#### Common

- create shared `native` source sets when using `addCommonTargets` in the MPP plugin
- add `com.freeletics.gradle.common.android.app` plugin for app modules with the same functionality as the existing library module plugin

#### Monorepo

- only enable Crashlytics for release builds with real versioning to improve build caching of developer/ci release builds
- stop allowing nav modules to depend on legacy modules


## 0.2.0 **[2023-03-13]**

- initial release
