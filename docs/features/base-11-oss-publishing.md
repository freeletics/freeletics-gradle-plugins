## OSS publishing

Publishing to Maven Central using the [gradle-maven-publish-plugin](https://vanniktech.github.io/gradle-maven-publish-plugin).

```kotlin
freeletics {
    enableOssPublishing()
}
```

Enabling this will also apply [Dokka](https://github.com/kotlin/dokka), enable ABI validation and enable explicit API
mode.

For the configuration of the POM, only the following Gradle properties need to be defined:
- `GROUP`
- `VERSION_NAME`
- `POM_REPO_NAME`
- `POM_ARTIFACT_ID`
- `POM_NAME`
- `POM_DESCRIPTION`
