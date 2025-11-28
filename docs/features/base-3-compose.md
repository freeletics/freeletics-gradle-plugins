### Compose

To enable Jetpack Compose for the module call:

```kotlin
freeletics {
    useCompose()
}
```

It's possible to enable compose compiler reports and metrics by setting the following 2 properties. Afterwards
the outputs can be found in the build folder.
```properties
fgp.compose.enableCompilerMetrics=true
fgp.compose.enableCompilerReports=true`
```
