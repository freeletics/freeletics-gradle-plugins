package com.freeletics.gradle.util

import com.android.build.api.dsl.AndroidResources
import com.android.build.api.dsl.ApplicationAndroidResources
import com.android.build.api.dsl.LibraryAndroidResources

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
