# Slack Scripts

Provides `SlackMessageCli` that makes it easy to write clikt scripts that send Slack messages.

This also provides several `OptionGroup` helpers:
- `CircleCiOptions`
- `PlatformOptions`
- `ReleaseTrainOptions`

### Example usage

```kts
#!/usr/bin/env kotlin

@file:DependsOn("com.freeletics.gradle:scripts-slack:<latest-version>")

import com.freeletics.gradle.scripts.CircleCiOptions
import com.freeletics.gradle.scripts.SlackMessageCli
import com.github.ajalt.clikt.parameters.groups.provideDelegate
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.core.main

class MySlackCli : SlackMessageCli() {
    override val webHookUrl: String by option(
        "--web-hook-url",
        help = "The web hook url that is used to send messages",
    ).required()

    private val circleCi by CircleCiOptions()

    override fun run() {
        send("Hello from job ${circleCi.jobName}")
    }
}

MySlackCli().main(args)
```
