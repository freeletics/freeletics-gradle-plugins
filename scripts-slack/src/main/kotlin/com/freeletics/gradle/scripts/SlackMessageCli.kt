package com.freeletics.gradle.scripts

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.Context
import com.slack.api.Slack
import com.slack.api.model.kotlin_extension.block.dsl.LayoutBlockDsl
import com.slack.api.model.kotlin_extension.block.withBlocks
import com.slack.api.webhook.Payload

/**
 * Simple base class with some common setup to send Slack messages using web hooks.
 */
public abstract class SlackMessageCli(name: String? = null) : CliktCommand(name) {

    private val slack = Slack.getInstance()

    /**
     * The web hook url that the Slack message is sent to. It's recommended to implement this
     * using a clikt [com.github.ajalt.clikt.parameters.options.option].
     */
    protected abstract val webHookUrl: String

    /**
     * Sends the given message to [webHookUrl].
     */
    protected fun send(message: String) {
        val payload = Payload.builder()
            .text(message)
            .build()
        slack.send(webHookUrl, payload)
    }

    /**
     * Sends the given message to [webHookUrl]. The message is created using [builder], [fallbackMessage] is used
     * in case block based messages can't be displayed.
     */
    protected fun send(fallbackMessage: String, builder: LayoutBlockDsl.() -> Unit) {
        val payload = Payload.builder()
            .text(fallbackMessage)
            .blocks(withBlocks(builder))
            .build()
        slack.send(webHookUrl, payload)
    }

    override fun help(context: Context): String {
        return "CLI to send Slack messages"
    }
}

