### Khonshu

To enable [Khonshu](https://freeletics.github.io/khonshu) for the module call:

```kotlin
freeletics {
    useKhonshu()
}
```

Both Metro and KSP will be automatically enabled when using Khonshu.

This expects the version catalog to have the following 2 entries:
- `khonshu-codegen-runtime` pointing to `com.freeletics.khonshu:codegen-runtime`
- `khonshu-codegen-compiler` pointing to `com.freeletics.khonshu:codegen-compiler`
