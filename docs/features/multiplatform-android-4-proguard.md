### Consumer proguard files

To publish proguard files for the consumer of the module call:

```kotlin
freeletics {
    android {
        consumerProguardFiles("path/to/file") // supports multiple files being passed
    }
}
```
