package com.freeletics.gradle.util

import com.android.build.api.dsl.ApplicationBuildFeatures
import com.android.build.api.dsl.BuildFeatures
import com.android.build.api.dsl.LibraryBuildFeatures

@Suppress("UnstableApiUsage")
internal var BuildFeatures.dataBinding: Boolean?
    get() = when (this) {
        is ApplicationBuildFeatures -> dataBinding
        is LibraryBuildFeatures -> dataBinding
        else -> throw UnsupportedOperationException("")
    }
    set(value) = when (this) {
        is ApplicationBuildFeatures -> dataBinding = value
        is LibraryBuildFeatures -> dataBinding = value
        else -> throw UnsupportedOperationException("")
    }

@Suppress("UnstableApiUsage")
internal var BuildFeatures.androidResources: Boolean?
    get() = when (this) {
        is LibraryBuildFeatures -> androidResources
        is ApplicationBuildFeatures -> true
        else -> throw UnsupportedOperationException("")
    }
    set(value) = when (this) {
        is LibraryBuildFeatures -> androidResources = value
        is ApplicationBuildFeatures -> {}
        else -> throw UnsupportedOperationException("")
    }
