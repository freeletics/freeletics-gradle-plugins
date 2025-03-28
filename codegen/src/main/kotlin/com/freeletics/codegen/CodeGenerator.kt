package com.freeletics.codegen

import java.io.File

public interface CodeGenerator {
    public fun generate(arguments: List<String>, sourceDirectory: File, targetDirectory: File)
}
