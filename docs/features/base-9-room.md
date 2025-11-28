### Room

To enable [Room](https://developer.android.com/jetpack/androidx/releases/room) for the module call:

```kotlin
freeletics {
    useRoom()
}
```

This will automatically apply KSP to the project and will use Kotlin code generation.

Optionally a `schemaLocation` can be provided.

This expects the version catalog to have the following 2 entries:
- `androidx-room-runtime` pointing to `androidx.room:room-runtime`
- `androidx-room-compiler` pointing to `androidx.room:room-compiler`
