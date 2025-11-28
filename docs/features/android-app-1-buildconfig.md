### Build config fields

To add a custom build config field call:

```kotlin
freeletics {
    android {
        // create a BuildConfig field with the given value
        buildConfigField("type", "name", "value")
        // create a BuildConfig field with separate values for debug and release
        buildConfigField("type", "name", "debug value", "release value")
    }
}
```
