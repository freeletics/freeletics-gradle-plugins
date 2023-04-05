package com.freeletics.gradle.scripts

import com.pinterest.ktlint.core.KtLintRuleEngine
import com.pinterest.ktlint.core.LintError
import com.pinterest.ktlint.core.api.EditorConfigDefaults
import com.pinterest.ktlint.core.api.KtLintParseException
import com.pinterest.ktlint.ruleset.standard.StandardRuleSetProvider
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.channelFlow
import java.nio.file.Path
import kotlin.io.path.writeText

internal class KtLintFormatter(
    editorConfig: Path,
) {
    private val engine = KtLintRuleEngine(
        ruleProviders = StandardRuleSetProvider().getRuleProviders(),
        editorConfigDefaults = EditorConfigDefaults.load(editorConfig),
    )

    internal fun format(path: Path) = channelFlow {
        try {
            val formattedContent = engine.format(path) { error, corrected ->
                check(trySendBlocking(KtLintError(path, error, corrected)).isSuccess)
            }
            path.writeText(formattedContent)
        } catch (e: KtLintParseException) {
            val error = LintError(
                line = e.line,
                col = e.col,
                ruleId = "file-parsing",
                detail = e.message ?: "Failed to parse file",
                canBeAutoCorrected = false
            )
            check(trySendBlocking(KtLintError(path, error, false)).isSuccess)
        }
    }
}
