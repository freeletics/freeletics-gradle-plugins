### Common configuration

The plugin applies the following common configuration:

- The Java and Kotlin toolchain are set to the `java-toolchain` version catalog version.
- Kotlin's progressive mode is enabled.
- Kotlin compiler warnings are treated as errors.
    - This can be disabled by setting `fgp.kotlin.warningsAsErrors=false`
    - It's possible to ignore deprecation warning by setting `fgp.kotlin.suppressDeprecationWarnings=true`
- Several Kotlin compiler flags are set to improve the default behavior.
- All produced archives are configured to be reproducible.
