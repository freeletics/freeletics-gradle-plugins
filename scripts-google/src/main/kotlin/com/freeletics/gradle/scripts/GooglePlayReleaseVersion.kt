package com.freeletics.gradle.scripts

public sealed class GooglePlayReleaseVersion() {

    public abstract val name: String
    public abstract val code: Long

    public val namePrefix: String get() = name.split(".").run {
        check(size == 3) { "Invalid version $name" }
        "${get(0)}.${get(1)}"
    }

    public val codePrefix: Long get() = code / 1000

    public data class Simple(
        override val name: String,
        override val code: Long,
    ) : GooglePlayReleaseVersion() {
        override fun toString(): String {
            return "$name ($code)"
        }
    }

    public data class WithRollout(
        override val name: String,
        override val code: Long,
        val rolloutPercentage: Double
    ) : GooglePlayReleaseVersion() {
        public fun asSimple(): GooglePlayReleaseVersion = Simple(name, code)

        override fun toString(): String {
            return "$name ($code) @ ${rolloutPercentage}"
        }
   }
}
