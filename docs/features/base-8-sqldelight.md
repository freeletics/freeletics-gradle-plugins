### SQLDelight

To enable [SQLDelight](https://sqldelight.github.io/sqldelight) for the module call:

```kotlin
freeletics {
    useSqlDelight()
}
```

There are 2 optional parameters for this function:
- the `name` for the generated database which defaults to `database`
- a `dependency` if this module depends on another module's SQLDelight definitions

To change the used [SQL dialect](https://sqldelight.github.io/sqldelight/latest/multiplatform_sqlite/gradle/#dialect)
add the dialect of your choice to the version catalog with an entry called `sqldelight-dialect`.
