package com.freeletics.gradle.util

import com.android.build.api.dsl.AndroidResources
import com.android.build.api.dsl.ApplicationAndroidResources
import com.android.build.api.dsl.ApplicationBuildFeatures
import com.android.build.api.dsl.BuildFeatures
import com.android.build.api.dsl.LibraryAndroidResources
import com.android.build.api.dsl.LibraryBuildFeatures

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

internal var AndroidResources.enable: Boolean
    get() = when (this) {
        is LibraryAndroidResources -> enable
        is ApplicationAndroidResources -> true
        else -> throw UnsupportedOperationException("")
    }
    set(value) = when (this) {
        is LibraryAndroidResources -> enable = value
        is ApplicationAndroidResources -> {}
        else -> throw UnsupportedOperationException("")
    }
