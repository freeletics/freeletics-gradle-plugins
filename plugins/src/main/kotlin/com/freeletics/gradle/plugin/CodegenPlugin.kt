package com.freeletics.gradle.codegen

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.attributes.java.TargetJvmEnvironment
import org.gradle.api.attributes.java.TargetJvmEnvironment.STANDARD_JVM
import org.gradle.api.attributes.java.TargetJvmEnvironment.TARGET_JVM_ENVIRONMENT_ATTRIBUTE
import org.gradle.api.tasks.TaskProvider
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension

public abstract class CodegenPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        // The DSL that is used from the build script to configure the
        // options of this plugin.
        val extension = project.extensions.create("codegen", CodegenExtension::class.java)

        // The code generator will be added to this as a dependency
        val configuration = project.configurations.create("codegen").apply {
            isTransitive = true

            attributes {
                it.attribute(
                    TARGET_JVM_ENVIRONMENT_ATTRIBUTE,
                    project.objects.named(TargetJvmEnvironment::class.java, STANDARD_JVM),
                )
            }
        }

        val generateTask = project.tasks.register("generateCode", CodegenTask::class.java) {
            it.generatorClasspath.setFrom(configuration)
            it.arguments.set(extension.arguments)
            it.sourceDirectory.set(extension.sourceDirectory)
            it.outputDirectory.set(project.layout.buildDirectory.dir("generated/sources"))
        }

        // This does 2 things:
        // - the output directory of the task is registered as source directory so that the
        //   generated code will be compiled and included in the built artifacts
        // - it automatically sets up  task dependencies so that generate is run before the
        //   compile task of the specified source set runs
        //
        // Depending on the module type based on which Kotlin plugin is applied a different source set is used.
        registerTaskWithSourceSet(project, generateTask, "org.jetbrains.kotlin.multiplatform", "commonMain")
        registerTaskWithSourceSet(project, generateTask, "org.jetbrains.kotlin.multiplatform", "jvmMain")
        registerTaskWithSourceSet(project, generateTask, "org.jetbrains.kotlin.multiplatform", "iosMain")
        registerTaskWithSourceSet(project, generateTask, "org.jetbrains.kotlin.jvm", "main")
    }

    private fun registerTaskWithSourceSet(
        project: Project,
        task: TaskProvider<CodegenTask>,
        plugin: String,
        sourceSet: String,
    ) {
        project.plugins.withId(plugin) {
            project.extensions.configure(KotlinProjectExtension::class.java) { extension ->
                extension.sourceSets.configureEach { kotlinSourceSet ->
                    if (kotlinSourceSet.name == sourceSet) {
                        kotlinSourceSet.kotlin.srcDir(task.flatMap { it.outputDirectory.dir("kotlin/$sourceSet") })
                    }
                }
            }
        }
    }
}
