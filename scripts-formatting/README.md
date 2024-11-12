# Formatting Scripts

This can be added as a dependency to `.main.kts` script to share
code for scripts between repositories.

## ktlint

The published artifact contains a wrapper to run ktlint on the repository or on
given files. To use it create `ktlint.main.kts` and add the following content:

```kts
#!/usr/bin/env kotlin

@file:DependsOn("com.freeletics.gradle:scripts-formatting:<latest-version>")

import com.freeletics.gradle.scripts.KtLintCli
import com.github.ajalt.clikt.core.main

KtLintCli().main(args)
```

This can then be executed using `./kotlinw ktlint.main.kts --help`


## Kotlin wrapper

It is recommended to create an executable file called `kotlinw` in the root of the
repository with the following content. This will allow to run kts scripts with
`./kotlinw foo.main.kts` and automatically takes care of downloading and updating
the Kotlin compiler for the execution. For this to work the `libs.versions.toml`
version catalog needs to include a version called `kotlin`.

```shell
#!/usr/bin/env bash
set -e

ROOT_DIR="$(dirname $0)"
KOTLIN_VERSION="$(grep -m 1 '^kotlin \?=' $ROOT_DIR/gradle/libs.versions.toml | cut -d'"' -f2)"
INSTALLATION_DIR="${HOME}/.kotlinw"
BINARY_DIR="${INSTALLATION_DIR}/kotlinc/bin"

function internal_kotlinw() {
  if [ ! -f "${BINARY_DIR}/kotlin" ]; then
    internal_install_and_link_kotlin_compiler
  fi

  local current_version
  current_version="$("${BINARY_DIR}/kotlin" -version 2> /dev/null)"
  local expected_version_regex
  expected_version_regex="Kotlin version ${KOTLIN_VERSION}.*"
  if ! [[ "${current_version}" =~ ${expected_version_regex} ]]; then
    internal_install_and_link_kotlin_compiler
  fi

  # this works around an issue where the Kotlin compiler used by ktlint accesses code that JDK 12+ don't allow access to
  export JAVA_OPTS="--add-opens java.base/java.lang=ALL-UNNAMED"

  "${BINARY_DIR}/kotlin" "$@"
}

function internal_install_and_link_kotlin_compiler() {
  echo "Downloading Kotlin ${KOTLIN_VERSION}"
  rm -rf "${INSTALLATION_DIR}"
  local temp_file
  temp_file=$(mktemp /tmp/kotlin.zip.XXXXXX)
  curl -sLo "${temp_file}" "https://github.com/JetBrains/kotlin/releases/download/v${KOTLIN_VERSION}/kotlin-compiler-${KOTLIN_VERSION}.zip"
  unzip -q "${temp_file}" -d "${INSTALLATION_DIR}"
  rm -f "${temp_file}"
}

internal_kotlinw "$@"
```
