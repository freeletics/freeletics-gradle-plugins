package com.freeletics.gradle.monorepo.tasks

import com.freeletics.gradle.monorepo.util.ProjectType
import java.lang.Exception
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.component.ProjectComponentIdentifier
import org.gradle.api.artifacts.result.ResolvedComponentResult
import org.gradle.api.artifacts.result.ResolvedDependencyResult
import org.gradle.api.attributes.Attribute
import org.gradle.api.attributes.Category
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.TaskProvider

@CacheableTask
public abstract class CheckDependencyRulesTask : DefaultTask() {
    @get:Input
    public abstract val projectPath: Property<String>

    @get:Input
    public abstract val allowedProjectTypes: ListProperty<String>

    @get:Input
    public abstract val allowedDependencyProjectTypes: ListProperty<String>

    @get:Input
    public abstract val artifactIds: Property<ResolvedComponentResult>

    @get:OutputFile
    public abstract val outputFile: RegularFileProperty

    @TaskAction
    public fun check() {
        val projectPath = this.projectPath.get()
        val component = this.artifactIds.get()
        val errors = component.dependencies
            .asSequence()
            .filterIsInstance<ResolvedDependencyResult>()
            // AGP adds all runtime dependencies as constraints to the compile classpath, and these show
            // up in the resolution result. Filter them out.
            .filterNot { it.isConstraint }
            // For similar reasons as above
            .filterNot {
                it.selected.variants.any { variant ->
                    val category = variant.attributes.getAttribute(CATEGORY)
                    category == Category.REGULAR_PLATFORM || category == Category.ENFORCED_PLATFORM
                }
            }
            .filterNot { it.selected == component }
            .map { it.selected.id }
            .filterIsInstance<ProjectComponentIdentifier>()
            .flatMap {
                checkDependencyRules(
                    projectPath = projectPath,
                    dependencyPath = it.projectPath,
                    allowedProjectTypes = allowedProjectTypes.get().map(ProjectType::valueOf),
                    allowedDependencyProjectTypes = allowedDependencyProjectTypes.get().map(ProjectType::valueOf),
                )
            }
            .toList()

        outputFile.get().asFile.writeText(errors.joinToString(separator = "\n"))
        errors.forEach {
            System.err.println(it)
        }

        if (errors.isNotEmpty()) {
            throw Exception(
                "Found dependency violations. See logs or the following file for more infomation:\n" +
                    outputFile.get().asFile.absolutePath,
            )
        }
    }

    internal companion object {
        private val CATEGORY = Attribute.of("org.gradle.category", String::class.java)

        fun Project.registerCheckDependencyRulesTasks(
            allowedProjectTypes: List<ProjectType>,
            allowedDependencyProjectTypes: List<ProjectType>,
        ) {
            val checkDependencyRules = tasks.register("checkDependencyRules")
            
            tasks.named("check").configure {
                it.dependsOn(checkDependencyRules)
            }

            configurations.configureEach {
                if (it.name.contains("compileClasspath", ignoreCase = true)) {
                    val configurationCheck = registerCheckDependencyRulesTask(
                        it,
                        allowedProjectTypes,
                        allowedDependencyProjectTypes,
                    )
                    checkDependencyRules.configure { task ->
                        task.dependsOn(configurationCheck)
                    }
                }
            }
        }

        private fun Project.registerCheckDependencyRulesTask(
            configuration: Configuration,
            allowedProjectTypes: List<ProjectType>,
            allowedDependencyProjectTypes: List<ProjectType>,
        ): TaskProvider<CheckDependencyRulesTask> {
            return tasks.register("${configuration.name}CheckDependencyRules", CheckDependencyRulesTask::class.java) {
                it.projectPath.set(path)
                it.allowedProjectTypes.addAll(allowedProjectTypes.map(ProjectType::name))
                it.allowedDependencyProjectTypes.addAll(allowedDependencyProjectTypes.map(ProjectType::name))
                it.artifactIds.set(configuration.incoming.resolutionResult.rootComponent)
                it.outputFile.set(layout.buildDirectory.file("reports/dependency-rules/${configuration.name}.txt"))
            }
        }
    }
}
