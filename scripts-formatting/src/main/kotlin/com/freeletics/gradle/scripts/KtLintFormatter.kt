package com.freeletics.gradle.scripts

import com.pinterest.ktlint.rule.engine.api.Code
import com.pinterest.ktlint.rule.engine.api.EditorConfigDefaults
import com.pinterest.ktlint.rule.engine.api.KtLintParseException
import com.pinterest.ktlint.rule.engine.api.KtLintRuleEngine
import com.pinterest.ktlint.rule.engine.api.LintError
import com.pinterest.ktlint.rule.engine.core.api.AutocorrectDecision
import com.pinterest.ktlint.rule.engine.core.api.RuleId
import com.pinterest.ktlint.ruleset.standard.StandardRuleSetProvider
import java.nio.file.Path
import kotlin.io.path.writeText
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.channelFlow

internal class KtLintFormatter(
    editorConfig: Path,
) {
    private val engine = KtLintRuleEngine(
        ruleProviders = StandardRuleSetProvider().getRuleProviders(),
        editorConfigDefaults = EditorConfigDefaults.load(editorConfig, emptySet()),
    )

    internal fun format(path: Path) = channelFlow {
        try {
            // need to use fromFile instead of fromPath because the latter drops the final new line
            val code = Code.fromFile(path.toFile())
            val formattedContent = engine.format(code) { error ->
                check(trySendBlocking(KtLintError(path, error, error.canBeAutoCorrected)).isSuccess)
                AutocorrectDecision.ALLOW_AUTOCORRECT
            }
            path.writeText(formattedContent)
        } catch (e: KtLintParseException) {
            val error = LintError(
                line = e.line,
                col = e.col,
                ruleId = RuleId("standard:file-parsing"),
                detail = e.message ?: "Failed to parse file",
                canBeAutoCorrected = false,
            )
            check(trySendBlocking(KtLintError(path, error, false)).isSuccess)
        }
    }
}
