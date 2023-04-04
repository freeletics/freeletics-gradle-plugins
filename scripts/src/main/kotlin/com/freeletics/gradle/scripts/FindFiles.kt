package com.freeletics.gradle.scripts

import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.filter
import java.nio.file.FileSystems
import java.nio.file.FileVisitResult
import java.nio.file.Path
import java.nio.file.PathMatcher
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.isHidden
import kotlin.io.path.name
import kotlin.io.path.visitFileTree

internal val kotlinMatcher = FileSystems.getDefault().getPathMatcher("glob:**/*.{kt,kts}")

@OptIn(ExperimentalPathApi::class)
internal fun filesToFormat(
    files: List<Path>?,
    rootDirectory: Path,
    matcher: PathMatcher,
): Flow<Path> {
    if (files != null) {
        return files.asFlow().filter { matcher.matches(it.fileName) }
    }

    return channelFlow {
        rootDirectory.visitFileTree {
            onPreVisitDirectory { it, _ ->
                val name = it.name
                if (it.isHidden() && name != ".") {
                    FileVisitResult.SKIP_SUBTREE
                } else if (name == "build" || name == "res") {
                    FileVisitResult.SKIP_SUBTREE
                } else {
                    FileVisitResult.CONTINUE
                }
            }

            onVisitFile { file, _ ->
                if (matcher.matches(file)) {
                    check(trySendBlocking(file).isSuccess)
                }
                FileVisitResult.CONTINUE
            }
        }
    }
}
