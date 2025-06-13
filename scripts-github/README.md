# Github Scripts

This provides a `OptionGroup` helper called `githubOptions` which gives access
to common Github values like the repository name, job number or branch.

### Example usage

```kts
#!/usr/bin/env kotlin

@file:DependsOn("com.freeletics.gradle:scripts-github:<latest-version>")

import com.freeletics.gradle.scripts.GithubOptions
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.main

class MyCli : CliktCommand() {
    private val github by GithubOptions()

    override fun run() {
        send("Hello from job ${github.jobName}")
    }
}

MyCli().main(args)
```
