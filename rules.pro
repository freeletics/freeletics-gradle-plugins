# The Gradle API jar isn't added to the classpath, ignore the missing symbols
-ignorewarnings
# Allow to make some classes public so that we can repackage them without breaking package-private members
-allowaccessmodification

# Keep kotlin metadata so that the Kotlin compiler knows about top level functions and other things
-keep class kotlin.Metadata { *; }

# Keep Functions because they are used in the public API of Gradle/AGP/KGP
-keep class kotlin.jvm.functions.** { *; }

# We need to keep type arguments (Signature) for Gradle to be able to instantiate abstract models like `Property`
-keepattributes Signature,Exceptions,*Annotation*,InnerClasses,PermittedSubclasses,EnclosingMethod,Deprecated,SourceFile,LineNumberTable

# Keep your public API so that it's callable from scripts
-keep class com.freeletics.gradle.plugin.*Extension { *; }
-keep class com.freeletics.gradle.plugin.*Plugin { *; }
-keep class com.freeletics.gradle.tasks.** { *; }

# No need to obfuscate class names
-dontobfuscate
