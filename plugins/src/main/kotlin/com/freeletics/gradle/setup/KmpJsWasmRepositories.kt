package com.freeletics.gradle.setup

import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsEnvSpec
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsPlugin
import org.jetbrains.kotlin.gradle.targets.js.yarn.YarnPlugin
import org.jetbrains.kotlin.gradle.targets.js.yarn.YarnRootEnvSpec
import org.jetbrains.kotlin.gradle.targets.wasm.binaryen.BinaryenEnvSpec
import org.jetbrains.kotlin.gradle.targets.wasm.binaryen.BinaryenPlugin
import org.jetbrains.kotlin.gradle.targets.wasm.nodejs.WasmNodeJsEnvSpec
import org.jetbrains.kotlin.gradle.targets.wasm.nodejs.WasmNodeJsPlugin
import org.jetbrains.kotlin.gradle.targets.wasm.yarn.WasmYarnPlugin
import org.jetbrains.kotlin.gradle.targets.wasm.yarn.WasmYarnRootEnvSpec

// TODO remove after https://youtrack.jetbrains.com/issue/KT-68533
internal fun Project.disableDefaultJsRepositories() {
    plugins.withType(NodeJsPlugin::class.java).configureEach {
        extensions.configure(NodeJsEnvSpec::class.java) {
            it.downloadBaseUrl.set(null)
        }
    }
    plugins.withType(YarnPlugin::class.java).configureEach {
        extensions.configure(YarnRootEnvSpec::class.java) {
            it.downloadBaseUrl.set(null)
        }
    }
    plugins.withType(WasmNodeJsPlugin::class.java).configureEach {
        extensions.configure(WasmNodeJsEnvSpec::class.java) {
            it.downloadBaseUrl.set(null)
        }
    }
    plugins.withType(WasmYarnPlugin::class.java).configureEach {
        extensions.configure(WasmYarnRootEnvSpec::class.java) {
            it.downloadBaseUrl.set(null)
        }
    }
    @OptIn(ExperimentalWasmDsl::class)
    plugins.withType(BinaryenPlugin::class.java).configureEach {
        extensions.configure(BinaryenEnvSpec::class.java) {
            it.downloadBaseUrl.set(null)
        }
    }
}
