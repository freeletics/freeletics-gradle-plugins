package com.freeletics.gradle.scripts

public data class GooglePlayReleaseVersion(
    val name: String,
    val code: Long,
) {
    val namePrefix: String get() = name.split(".").run {
        check(size == 3) { "Invalid version $name" }
        "${get(0)}.${get(1)}"
    }

    val codePrefix: Long get() = code / 1000

    override fun toString(): String {
        return "$name ($code)"
    }
}
