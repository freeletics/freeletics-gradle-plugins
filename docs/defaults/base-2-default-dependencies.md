### Default dependencies

The plugins can dynamically add default dependencies to all modules. This can be enabled
by creating any of the following bundles:

```toml
[bundles]
# any dependency in this bundle is automatically added to all modules as implementation dependency
default-all = [ ... ]
# any dependency in this bundle is automatically added to all modules as compileOnly dependency
default-all-compile = [ ... ]
# any dependency in this bundle is automatically added to all Android modules as implementation dependency
default-android = [ ... ]
# any dependency in this bundle is automatically added to all Android modules as compileOnly dependency
default-android-compile = [ ... ]
# any dependency in this bundle is automatically added to all modules as testImplementation dependency
default-testing = [ ... ]
# any dependency in this bundle is automatically added to all modules as testCompileOnly dependency
default-testing-compile = [ ... ]
# any dependency in this bundle is automatically added to all modules as testRuntimeOnly dependency
default-testing-runtime = [ ... ]
# any dependency in this bundle is automatically added to all modules as lintChecks dependency
default-lint = [ ... ]
```
