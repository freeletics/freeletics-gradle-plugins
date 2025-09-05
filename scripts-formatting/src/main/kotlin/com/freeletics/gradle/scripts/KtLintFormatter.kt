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
import java.nio.file.Paths
import kotlin.io.path.writeText
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.channelFlow
import org.ec4j.core.model.EditorConfig
import org.ec4j.core.model.Glob
import org.ec4j.core.model.Property
import org.ec4j.core.model.Section

internal class KtLintFormatter() {
    private val editorConfigDefaults = EditorConfigDefaults(
        EditorConfig
            .builder()
            .section(
                Section
                    .builder()
                    .glob(Glob("*.{kt,kts}"))
                    .properties(
                        // allow test methods with back ticked names to be longer than the line limit
                        Property
                            .builder()
                            .name("ktlint_ignore_back_ticked_identifier")
                            .value("true"),
                        // allow composable functions to be capitalized
                        Property
                            .builder()
                            .name("ktlint_function_naming_ignore_when_annotated_with")
                            .value("Composable"),
                        // remove unused imports
                        Property
                            .builder()
                            .name("ktlint_standard_no-unused-imports")
                            .value("enabled"),
                        // see https://github.com/pinterest/ktlint/issues/2138
                        Property
                            .builder()
                            .name("ktlint_standard_annotation")
                            .value("disabled"),
                    ),
            )
            .build(),
    )

    private val engine = KtLintRuleEngine(
        ruleProviders = StandardRuleSetProvider().getRuleProviders(),
        editorConfigDefaults = editorConfigDefaults,
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
