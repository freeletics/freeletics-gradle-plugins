### Validations

- `enableStricterValidation` is set to true to make Gradle validate cacheable tasks more strictly.
- Automatically enables Android Lint for the module and applies the `androidx.lint:lint-gradle` lint checks.
    - Expects a version catalog entry called `androidx-lint-gradle` pointing to the lint checks to exist.
