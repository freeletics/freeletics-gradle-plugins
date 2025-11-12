package com.freeletics.gradle.scripts

import com.pinterest.ktlint.rule.engine.api.Code
import com.pinterest.ktlint.rule.engine.api.EditorConfigDefaults
import com.pinterest.ktlint.rule.engine.api.KtLintParseException
import com.pinterest.ktlint.rule.engine.api.KtLintRuleEngine
import com.pinterest.ktlint.rule.engine.core.api.AutocorrectDecision
import com.pinterest.ktlint.ruleset.standard.StandardRuleSetProvider
import java.nio.file.Path
import kotlin.io.path.writeText
import org.ec4j.core.model.EditorConfig
import org.ec4j.core.model.Glob
import org.ec4j.core.model.Property
import org.ec4j.core.model.Section

internal class KtLintFormatter : Formatter {
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

    override fun format(path: Path): FormattingResult {
        val errors = mutableListOf<String>()

        // need to use fromFile instead of fromPath because the latter drops the final new line
        val code = Code.fromFile(path.toFile())
        val formatted = try {
            engine.format(code) { error ->
                if (!error.canBeAutoCorrected) {
                    errors += "$path:${error.line} ${error.ruleId} ${error.detail}"
                }
                AutocorrectDecision.ALLOW_AUTOCORRECT
            }
        } catch (e: KtLintParseException) {
            return FormattingResult.Error("$path: ${e.message ?: "Failed to parse file"}")
        }

        if (errors.isNotEmpty()) {
            return FormattingResult.Error(errors.joinToString(separator = "\n"))
        } else if (formatted != code.content) {
            path.writeText(formatted)
            return FormattingResult.Formatted
        } else {
            return FormattingResult.AlreadyFormatted
        }
    }
}
