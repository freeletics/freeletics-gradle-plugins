## Internal publishing

Publishing to an internal repo can be enabled by calling the following function:

```kotlin
freeletics {
    enableInternalPublishing()
}
```

A `fgp.internalArtifacts.url` gradle property needs to be defined with the URL of the remote repository. The username
and password for this repository are expected to be set through `internalArtifactsUsername` and `internalArtifactsPassword`.
