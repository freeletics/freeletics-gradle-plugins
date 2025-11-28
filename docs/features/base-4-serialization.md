### kotlinx.serialization

To enable `kotlinx.serialziation` for the module call:

```kotlin
freeletics {
    useSerialization()
}
```

This expects the version catalog to have a `kotlinx-serialization` entry with the
`org.jetbrains.kotlinx:kotlinx-serialization-core` library.
