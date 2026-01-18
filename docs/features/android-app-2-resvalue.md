### Res values

To add a custom res value call:

```kotlin
freeletics {
    android {
        // create a res value with the given value
        resValue("type", "name", "value")
        // create a res value with separate values for debug and release
        resValue("type", "name", "debug value", "release value")
    }
}
```
