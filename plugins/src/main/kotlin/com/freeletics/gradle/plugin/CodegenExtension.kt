package com.freeletics.gradle.codegen

import com.freeletics.codegen.CodeGenerator
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.ListProperty

public interface CodegenExtension {
    /**
     * The `arguments` that are passed to [CodeGenerator].
     */
    public val arguments: ListProperty<String>

    /**
     * The directory which contains the source scripts.
     */
    public val sourceDirectory: DirectoryProperty
}
