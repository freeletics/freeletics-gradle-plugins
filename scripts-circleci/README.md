# CircleCI Scripts

This provides a `OptionGroup` helper called `CircleCiOptions` which gives access
to common CircleCI values like the repository name, job number or branch.

### Example usage

```kts
#!/usr/bin/env kotlin

@file:DependsOn("com.freeletics.gradle:scripts-circleci:<latest-version>")

import com.freeletics.gradle.scripts.CircleCiOptions
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.main

class MyCli : CliktCommand() {
    private val circleCi by CircleCiOptions()

    override fun run() {
        send("Hello from job ${circleCi.jobName}")
    }
}

MyCli().main(args)
```
