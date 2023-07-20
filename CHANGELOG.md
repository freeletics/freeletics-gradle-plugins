Change Log
==========

## 0.4.1 **UNRELEASED**


## 0.4.0 *(2023-07-19)*

- now requires Kotlin 1.9.0
- `useRoom` will now use KSP
- the `fgp.kotlin.ksp` property was removed
- new `fgp.kotlin.daggerKsp` property to use KSP with Dagger
- `discoverProjects` and `discoverProjectsIn` now have a boolean parameter to declare
  whether kts build files are used or nor
- project auto-disovery works better with the configuration cache by scanning less
  directories and limiting the project depth (project auto discovery generally does
  not work well with Gradle 8.1 and 8.2 because of a performance issue)
- when enabling Dagger the new `dagger.warnIfInjectionFactoryNotGeneratedUpstream` flag
  is enabled automatically
- Multiplatform: new `addAndroidTarget` method to add Android as a target
- Multiplatform: new `addCommonTargets` now as a parameter to skip androidNative* targets


## 0.3.8 *(2023-07-10)*

- adjust APIs and expected version catalog names for MAD -> Khonshu renaming


## 0.3.7 *(2023-06-28)*

- fix auto project discovery failing with error
- fix some properties being required by the settings plugin unintenionally


## 0.3.6 *(2023-06-27)*

- adjust auto discovery to work better with Gradle 8.1 config cache
    - make it possible to disable the default auto discovery
    - add `discoverProjects` to settings extension which works like the default discovery
    - add `discoverProjectsIn` to settings extension which allows only search specified folders for proects
- change expected `fl-whetstone-...` version catalog names to `mad-whetstone-...`
- make it possible to enable mad experimental navigation through settings extension (`useMadExperimentalNavigation()` and `experimentalNavigation` parameter on `inlu)


## 0.3.4 *(2023-05-09)*

- fix issue with the `standard_final-newline` ktlint rule


## 0.3.3 *(2023-05-08)*

- Support ktlint 0.49.0
- `addCommonTargets` will now add all [tier 1, 2 and 3 Kotlin/Native targets](https://kotlinlang.org/docs/native-target-support.html)


## 0.3.2 *(2023-04-19)*


- Don't pass jvm specific compiler args to multiplatform compile tasks
- Fix `includeMad` in settings plugin
- Ignore unused dependency warning for `() -> java.io.File?` caused by KGP 1.8.20


## 0.3.1 *(2023-04-17)*

- Fixes exception when running `generatePluginVersion`


## 0.3.0 *(2023-04-17)*

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


## 0.2.3 *(2023-03-24)*

- Make uploading native symbols to Crashlytics optional by adding a boolean parameter to `enableCrashReporting(...)`. This
  defaults to `false`.
- When using the oss publish plugin automatically apply Dokka and disable Dokka for `-SNAPSHOT` builds.


## 0.2.2 *(2023-03-22)*

- don't create a `clean` task in the root plugin


## 0.2.1 *(2023-03-22)*

#### Common

- create shared `native` source sets when using `addCommonTargets` in the MPP plugin
- add `com.freeletics.gradle.common.android.app` plugin for app modules with the same functionality as the existing library module plugin

#### Monorepo

- only enable Crashlytics for release builds with real versioning to improve build caching of developer/ci release builds
- stop allowing nav modules to depend on legacy modules


## 0.2.0 *(2023-03-13)*

- initial release
