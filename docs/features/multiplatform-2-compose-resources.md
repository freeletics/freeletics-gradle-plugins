### Compose resources

Enable [Compose resources](https://kotlinlang.org/docs/multiplatform/compose-multiplatform-resources.html) for the module call:

```kotlin
freeletics {
    multiplatform {
        useComposeResources()
    }
}
```

This will configure the `Res` class generation to always run and will configure the package name based on the module.
