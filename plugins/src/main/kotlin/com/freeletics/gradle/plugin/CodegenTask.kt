package com.freeletics.gradle.codegen

import com.freeletics.codegen.CodeGenerator
import java.util.ServiceLoader
import javax.inject.Inject
import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Classpath
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import org.gradle.workers.WorkAction
import org.gradle.workers.WorkParameters
import org.gradle.workers.WorkQueue
import org.gradle.workers.WorkerExecutor

@CacheableTask
public abstract class CodegenTask : DefaultTask() {
    @get:Classpath
    internal abstract val generatorClasspath: ConfigurableFileCollection

    @get:Input
    public abstract val arguments: ListProperty<String>

    @get:InputDirectory
    @get:PathSensitive(PathSensitivity.NAME_ONLY)
    public abstract val sourceDirectory: DirectoryProperty

    @get:OutputDirectory
    public abstract val outputDirectory: DirectoryProperty

    @get:Inject
    public abstract val workerExecutor: WorkerExecutor

    @TaskAction
    public fun run() {
        val output = outputDirectory.asFile.get()
        if (output.exists()) {
            output.walkBottomUp().forEach {
                if (it != output) {
                    it.delete()
                }
            }
        }

        val workQueue: WorkQueue = workerExecutor.classLoaderIsolation {
            it.classpath.from(generatorClasspath)
        }

        workQueue.submit(GenerateCode::class.java) {
            it.arguments.set(arguments)
            it.sourceDirectory.set(sourceDirectory)
            it.outputDirectory.set(outputDirectory)
        }
    }

    public abstract class GenerateCode : WorkAction<GenerateCode.Parameters> {
        override fun execute() {
            val generator = ServiceLoader.load(CodeGenerator::class.java).single()
            generator.generate(
                parameters.arguments.get(),
                parameters.sourceDirectory.asFile.get(),
                parameters.outputDirectory.asFile.get(),
            )
        }

        public interface Parameters : WorkParameters {
            public val arguments: ListProperty<String>
            public val sourceDirectory: DirectoryProperty
            public val outputDirectory: DirectoryProperty
        }
    }
}
