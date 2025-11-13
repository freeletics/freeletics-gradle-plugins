package com.freeletics.gradle.plugin

import com.freeletics.gradle.util.getDependency
import com.gradle.develocity.agent.gradle.test.DevelocityTestConfiguration
import java.time.Duration
import kotlin.apply
import kotlin.jvm.java
import kotlin.text.toInt
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.jvm.JvmTestSuite
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.TaskProvider
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.gradle.testing.base.TestingExtension

public abstract class AppiumPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.plugins.apply("jvm-test-suite")

        val localTestCases = project.rootProject.name == "shared-infrastructure"

        val unzipTestClasses = project.setupTestDependency(localTestCases)
        project.setSharedTestConfiguration(unzipTestClasses, localTestCases)
        project.addAndroidTestTasks()
        project.addIosTestTasks()
    }

    private fun Project.setupTestDependency(localTestCases: Boolean): TaskProvider<Copy> {
        val testCases = configurations.create("testCases")
        testCases.isTransitive = false

        dependencies.apply {
            if (localTestCases) {
                add(testCases.name, project(":testing:appium-tests"))
            } else {
                add(testCases.name, getDependency("appium-test-cases"))
            }
        }

        val testClassesDir = layout.buildDirectory.dir("testClasses")
        return tasks.register("unzipTests", Copy::class.java) {
            if (localTestCases) {
                it.dependsOn(tasks.getByPath(":testing:appium-tests:jvmJar"))
            }
            it.from(zipTree(testCases.singleFile))
            it.into(testClassesDir)
        }
    }

    @Suppress("UnstableApiUsage")
    private fun Project.setSharedTestConfiguration(unzipTestClasses: TaskProvider<Copy>, localTestCases: Boolean) {
        testing.suites.withType(JvmTestSuite::class.java).configureEach { suite ->
            suite.dependencies {
                if (localTestCases) {
                    it.runtimeOnly.add(it.project(":testing:appium-tests"))
                } else {
                    it.runtimeOnly.add(getDependency("appium-test-cases"))
                }
            }

            suite.targets.configureEach { target ->
                target.testTask.configure { test ->
                    if (test.name == "test") {
                        test.enabled = false
                        return@configure
                    }

                    test.useJUnitPlatform()

                    test.outputs.upToDateWhen { false }

                    test.timeout.set(Duration.ofMinutes(120))

                    test.maxParallelForks = System.getProperty("testParallelism", "8").toInt()

                    test.extensions.getByType(DevelocityTestConfiguration::class.java).testRetry.apply {
                        maxRetries.set(2)
                        maxFailures.set(null)
                        failOnPassedAfterRetry.set(false)
                    }

                    test.dependsOn(unzipTestClasses)
                    test.testClassesDirs = unzipTestClasses.get().outputs.files

                    test.testLogging {
                        it.events(
                            TestLogEvent.STARTED,
                            TestLogEvent.PASSED,
                            TestLogEvent.SKIPPED,
                            TestLogEvent.FAILED,
                        )

                        it.exceptionFormat = TestExceptionFormat.FULL
                        it.showStandardStreams = true
                    }
                }
            }
        }
    }

    @Suppress("UnstableApiUsage")
    private fun Project.addAndroidTestTasks() {
        testing.suites.register("androidReleaseLocalTest", JvmTestSuite::class.java) { suite ->
            suite.targets.configureEach { target ->
                target.testTask.configure {
                    it.useJUnitPlatform {
                        it.excludeTags("iOSOnly")
                    }

                    it.systemProperty("app.os", "ANDROID_LOCAL")
                    it.systemProperty("app.package", "com.freeletics.lite")
                    // provided by user
                    it.systemProperty("app.file", System.getProperty("app.file"))
                    // provided by user
                    it.systemProperty("device.name", System.getProperty("device.name"))
                    // provided by user
                    it.systemProperty("device.version", System.getProperty("device.version"))
                }
            }
        }

        testing.suites.register("androidDebugLocalTest", JvmTestSuite::class.java) { suite ->
            suite.targets.configureEach { target ->
                target.testTask.configure {
                    it.useJUnitPlatform {
                        it.excludeTags("iOSOnly")
                    }

                    it.systemProperty("app.os", "ANDROID_LOCAL")
                    it.systemProperty("app.package", "com.freeletics.debug")
                    // provided by user
                    it.systemProperty("app.file", System.getProperty("app.file"))
                    // provided by user
                    it.systemProperty("device.name", System.getProperty("device.name"))
                    // provided by user
                    it.systemProperty("device.version", System.getProperty("device.version"))
                }
            }
        }

        testing.suites.register("androidReleaseRemoteTest", JvmTestSuite::class.java) { suite ->
            suite.targets.configureEach { target ->
                target.testTask.configure {
                    it.useJUnitPlatform {
                        it.excludeTags("iOSOnly")
                    }

                    it.systemProperty("app.os", "ANDROID_REMOTE")
                    it.systemProperty("app.package", "com.freeletics.lite")
                    it.systemProperty("app.file", System.getProperty("app.file", "freeletics-release-build.apk"))
                    it.systemProperty("device.name", "") // not used for remote runs
                    it.systemProperty("device.version", "") // not used for remote runs
                }
            }
        }

        testing.suites.register("androidDebugRemoteTest", JvmTestSuite::class.java) { suite ->
            suite.targets.configureEach { target ->
                target.testTask.configure {
                    it.useJUnitPlatform {
                        it.excludeTags("iOSOnly")
                    }

                    it.systemProperty("app.os", "ANDROID_REMOTE")
                    it.systemProperty("app.package", "com.freeletics.debug")
                    // defaults to freeletics.apk but can be provided by user
                    it.systemProperty("app.file", System.getProperty("app.file", "freeletics.apk"))
                    it.systemProperty("device.name", "") // not used for remote runs
                    it.systemProperty("device.version", "") // not used for remote runs
                }
            }
        }
    }

    @Suppress("UnstableApiUsage")
    private fun Project.addIosTestTasks() {
        testing.suites.register("iosLocalTest", JvmTestSuite::class.java) { suite ->
            suite.targets.configureEach { target ->
                target.testTask.configure {
                    it.useJUnitPlatform {
                        it.excludeTags("AndroidOnly")
                    }

                    it.systemProperty("app.os", "IOS_LOCAL")
                    it.systemProperty("app.package", "") // not used for iOS
                    // provided by user
                    it.systemProperty("app.file", System.getProperty("app.file"))
                    // provided by user
                    it.systemProperty("device.name", System.getProperty("device.name"))
                    // provided by user
                    it.systemProperty("device.version", System.getProperty("device.version"))
                }
            }
        }

        testing.suites.register("iosRemoteTest", JvmTestSuite::class.java) { suite ->
            suite.targets.configureEach { target ->
                target.testTask.configure {
                    it.useJUnitPlatform {
                        it.excludeTags("AndroidOnly")
                    }

                    it.systemProperty("app.os", "IOS_REMOTE")
                    it.systemProperty("app.package", "") // not used for iOS
                    // defaults to freeletics.ipa but can be provided by user
                    it.systemProperty("app.file", System.getProperty("app.file", "freeletics.ipa"))
                    it.systemProperty("device.name", "") // not used for remote runs
                    it.systemProperty("device.version", "") // not used for remote runs
                }
            }
        }
    }

    @Suppress("UnstableApiUsage")
    private val Project.testing get() = project.extensions.getByType(TestingExtension::class.java)
}
