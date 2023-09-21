package com.freeletics.gradle.plugin

import com.freeletics.gradle.setup.defaultTestSetup
import com.freeletics.gradle.util.freeleticsExtension
import com.freeletics.gradle.util.java
import com.freeletics.gradle.util.javaTargetVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.api.tasks.testing.Test

public abstract class FreeleticsJvmBasePlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.plugins.apply("org.jetbrains.kotlin.jvm")
        target.plugins.apply(FreeleticsBasePlugin::class.java)

        target.freeleticsExtension.extensions.create("jvm", FreeleticsJvmExtension::class.java)

        target.java {
            sourceCompatibility = target.javaTargetVersion
            targetCompatibility = target.javaTargetVersion
        }

        target.tasks.withType(JavaCompile::class.java).configureEach {
            it.options.release.set(target.javaTargetVersion.majorVersion.toInt())
        }

        target.tasks.withType(Test::class.java).configureEach(Test::defaultTestSetup)
    }
}
