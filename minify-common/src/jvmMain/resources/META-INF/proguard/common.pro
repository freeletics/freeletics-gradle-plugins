# https://cs.android.com/android-studio/platform/tools/base/+/d732d3188323ed66e1248d06f49bd13de87d0684:build-system/gradle-core/src/main/resources/com/android/build/gradle/proguard-optimizations.txt
-allowaccessmodification

# https://cs.android.com/android-studio/platform/tools/base/+/710383a66f0637acc9a091f826d45ac7bb6d79cf:build-system/gradle-core/src/main/resources/com/android/build/gradle/proguard-common.txt;l=1-8
# Preserve some attributes that may be required for reflection.
-keepattributes AnnotationDefault,
                EnclosingMethod,
                InnerClasses,
                RuntimeVisibleAnnotations,
                RuntimeVisibleParameterAnnotations,
                RuntimeVisibleTypeAnnotations,
                Signature

# https://cs.android.com/android-studio/platform/tools/base/+/710383a66f0637acc9a091f826d45ac7bb6d79cf:build-system/gradle-core/src/main/resources/com/android/build/gradle/proguard-common.txt;l=17-20
# For native methods, see https://www.guardsquare.com/manual/configuration/examples#native
-keepclasseswithmembernames,includedescriptorclasses class * {
    native <methods>;
}

# https://cs.android.com/android-studio/platform/tools/base/+/710383a66f0637acc9a091f826d45ac7bb6d79cf:build-system/gradle-core/src/main/resources/com/android/build/gradle/proguard-common.txt;l=33-37
# For enumeration classes, see http://proguard.sourceforge.net/manual/examples.html#enumerations
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# https://cs.android.com/android-studio/platform/tools/base/+/710383a66f0637acc9a091f826d45ac7bb6d79cf:build-system/gradle-core/src/main/resources/com/android/build/gradle/proguard-common.txt;l=39-41
-keepclassmembers class * implements android.os.Parcelable {
    public static final ** CREATOR;
}
