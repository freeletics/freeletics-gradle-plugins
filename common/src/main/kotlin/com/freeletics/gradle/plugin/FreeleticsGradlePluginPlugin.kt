package com.freeletics.gradle.plugin

import com.autonomousapps.tasks.CodeSourceExploderTask
import com.freeletics.gradle.setup.setupGr8
import com.freeletics.gradle.tasks.GenerateVersion
import com.freeletics.gradle.util.getDependency
import com.freeletics.gradle.util.java
import com.freeletics.gradle.util.stringProperty
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.jvm.tasks.Jar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

abstract class FreeleticsGradlePluginPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.plugins.apply("java-gradle-plugin")
        target.plugins.apply("org.jetbrains.kotlin.jvm")
        target.plugins.apply(FreeleticsJvmBasePlugin::class.java)
        target.plugins.apply("com.gradleup.gr8")
        target.plugins.apply("com.autonomousapps.dependency-analysis")
        target.plugins.apply("com.autonomousapps.plugin-best-practices-plugin")

        target.extensions.create("freeletics", FreeleticsJvmExtension::class.java)

        target.generateVersionTask()
        target.setupGr8()

        target.dependencies.add("compileOnly", target.getDependency("gradle-api"))
        target.dependencies.add("shade", target.getDependency("kotlin-stdlib"))
    }

    private fun Project.generateVersionTask() {
        val directory = layout.buildDirectory.dir("generated/pluginVersion")

        java {
            sourceSets.findByName("main")!!.java.srcDir(directory)
        }

        val generateVersion = tasks.register("generatePluginVersion", GenerateVersion::class.java) { task ->
            val packageName = stringProperty("GROUP").map { group ->
                val artifactId = property("POM_ARTIFACT_ID")!!.toString()
                "$group.$artifactId".replace("-", "")
            }
            task.packageName.set(packageName)
            task.version.set(stringProperty("VERSION_NAME"))
            val file = packageName.map { "${it.replace(".", "/")}/Version.kt" }
            task.outputFile.set(directory.flatMap { dir -> dir.file(file) })
        }

        tasks.withType(KotlinCompile::class.java).configureEach {
            it.dependsOn(generateVersion)
        }

        tasks.withType(Jar::class.java).configureEach {
            if (it.name == "sourcesJar") {
                it.dependsOn(generateVersion)
            }
        }

        tasks.withType(CodeSourceExploderTask::class.java).configureEach {
            it.dependsOn(generateVersion)
        }
    }
}
