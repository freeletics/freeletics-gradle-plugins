Change Log
==========

## 0.13.0 *(2024-06-06)*

- Update Kotlin to 2.0.0.
- `dependencyResolutionManagement.repositoriesMode = RepositoriesMode.FAIL_ON_PROJECT_REPOS` is now always
  enabled. The opt out Gradle property `fgp.kotlin.multiplatformProject` is not used anymore.
- `useDagger` and `useSerialization` can now be used in multiplatform projects.


## 0.12.2 *(2024-05-15)*

- Support for Crashlytics Gradle Plugin 3.0.0.


## 0.12.1 *(2024-04-24)*

- Add support for using Compose in Kotlin 2.0.0 through the new Compose compiler
  plugin that is shipped as part of Kotlin.
- Only enable Kotlin's progressive mode if the language version matches or 
  exceeds the current Kotlin version.
- Separate activating Anvil KSP for app modules from library modules. This
  allows using Anvil KSP and K2 in most of the code base until Anvil KSP
  is supporting contributed subcomponents.
- `addIosTargets` now is not creating a framework anymore. `addIosTargetsWithXcFramework`
  can be used for that instead.
- Don't publish build scans by default.


## 0.12.0 *(2024-04-04)*

- Add mechanism to override version catalog versions through Gradle properties. The property should have
  `fgp.version.override.<name-of-version-in-catalog>` as name.
- Add support for setting a `room.schemaLocation`.
- Add support for enabling kotlinx.serialization through `useSerialization()`.
- Set `-Xjdk-release` option for Kotlin/JVM projects.
- Update the setup for Anvil KSP to support Anvil 2.5.0.
- Removed options for Khonshu's experimental navigation.
- Fixed missing `wasmJs` test targets warning.


## 0.11.0 *(2024-02-21)*

- `addCommonTargets` now adds the `wasmJs` target.
- `addIosTargets` doesn't add `iosX64` anymore.
- Expose the created framework for `addIosTargets`.


## 0.10.1 *(2024-02-02)*

- Actually don't apply gr8 anymore.


## 0.10.0 *(2024-02-02)*

- `com.freeletics.gradle.gradle` does not apply gr8 and best-practices anymore.
- Updated dependencies.


## 0.9.0 *(2023-12-01)*

- Updated to Gradle 8.5.
- Updated to Kotlin 1.9.20.
- Updated to AGP 8.5.
- Support for AGP 8.3.0 alpha.
- `com.freeletics.gradle.app` now enables `generateLocaleConfig` by default.
- The build number logic has been changed from major version 24 on (2024 releases), see
  [#205](https://github.com/freeletics/freeletics-gradle-plugins/pull/205) for more details.
- Removed parts of the legacy project config in monorepo plugins
- Updated to Kotlin 1.9.20 and enabled the default hierarchy template for multiplatform projects.
- `addIosTargets` with enabled XcFrameworks and `com.freeletics.gradle.publish.internal` applied
  will automatically set up everything to publish the framework (as zip) to Maven repositories
- Suppress compiler warning about expect/actual classes being experimental


## 0.8.1 *(2023-10-20)*

- Only use the root project as `java-platform` on projects using the monorepo
  plugins
- Don't make `khonshu-codegen-runtime` dependency required for `useDagger()`.


## 0.8.0 *(2023-10-18)*

- Default dependencies are now defined through the `default-all`, `default-all-compile`,
  `default-android` and `default-android-compile` bundles which are automatically added
  to all modules if the bundle exists.
- Default test dependencies are now defined through the `default-test`,
  `default-test-compile` and `default-test-runtime` bundles.
- Default lint check dependencies are now defined through the `default-lint` bundle.
- The root plugin is now automatically applying the `java-platform` plugin and adds all
  version catalog entries to the platform. The monorepo plugins are also automatically
  using the rootProject as platform.
- The `check` task now depends on `checkDependencyRules` and `verifyPaparazzi`.
- Disable the `assemble*` and lint reporting tasks for libraries in the monorepo plugins
  to avoid accidentally running too many tasks when running something like `./gradlew assemble`,
  `./gradlew check`, `./gradlew build` or `./gradlew lint`.


## 0.7.2 *(2023-09-29)*

- For KMP libraries with an Android target only publish the `release` variant by default.
- Fix error caused by `ProcessGoogleResourcesTask`.
- Fix OSS publish plugin not including licenses.


## 0.7.0 *(2023-09-28)*

- Consolidated the Gradle plugins into one module and merged common with base plugins. As a result
  the `common` was removed from plugin ids. For example `com.freeletics.gradle.common.jvm` is now
  `com.freeletics.gradle.jvm`.
- Plugin ids that previously used a `-` now use a `.`, e.g. `com.freeletics.gradle.core-android` is now
  `com.freeletics.gradle.core.android`.
- `enableCompose()` in the `android` block was replaced with a top level `useCompose()` function that
  applies the Jetbrains Compose plugin for non Android projects.
- It's now possible to easily get Compose compiler reports by setting `fgp.compose.enableCompilerMetrics=true`
  and/or `fgp.compose.enableCompilerReports=true`.
- The `useRoom()` option will now automatically enable Room's Kotlin codegen.
- Paparazzi verify deltas are now automatically copied to the root projects `build/reports` directory.
- Support for Licensee 1.8.0.
- Support for Khonshu 0.17.0.
- Fix configuration cache compatibility of `checkDependencyRules`


## 0.6.1 *(2023-09-11)*

- Downgrade Ktlint because of [KT-60813](https://youtrack.jetbrains.com/issue/KT-60813).


## 0.6.0 *(2023-09-08)*

- Updated logic of how version code and name are calculated to only consider tags. Branch names
  and the commit count are not considered anymore.
- Updated experimental options to use Dagger, Anvil and Khonshu through KSP.
- Ktlint 1.0.0
- Updated dependencies.


## 0.5.0 *(2023-08-09)*

- Moved Android/JVM/Multiplatform specific DSL methods into sub-extensions.
- It's now possible to change the Android build tools version by setting `android.buildTools`
  in the version catalog.
- Add workaround to incremental build issue in Android's merge Java resources task.
- Removed `enableAndroidTests` from the DSL.
- Added flags to use Moshi, Anvil and Khoshu through KSP.
- Addded `consumerProguardFile` to Android DSL.
- Adapt `includeKhonshu` for the latest release.


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
- Multiplatform: `addCommonTargets` now has a parameter to skip adding `androidNative*` targets


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
