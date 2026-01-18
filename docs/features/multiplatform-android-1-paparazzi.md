### Paparazzi

To enable [Paparazzi](https://github.com/cashapp/paparazzi) for the module call:

```kotlin
freeletics {
    android {
        usePaparazzi()
    }
}
```

This also configures that running Gradle's `check` or `build` tasks will automatically include `verifyPaparazzi`.
