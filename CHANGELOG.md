Change Log
==========

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
