### Android configuration

- The `compileSdk` is set to the `android-compile` version catalog version
- The `minSdk` is set to the `android-min` version catalog version
- The `targetSdk` is set to the `android-target` version catalog version
- Optionally the `buildToolsVersion` is set to the `android-buildTools` version catalog version if it's present in the
  version catalog.
- If the `android-desugarjdklibs` library is defined the version catalog core library desugaring is be enabled
  automatically.
