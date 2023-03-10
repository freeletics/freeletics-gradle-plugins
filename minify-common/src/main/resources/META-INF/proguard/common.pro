# https://cs.android.com/android-studio/platform/tools/base/+/refs/tags/studio-2021.1.1:build-system/gradle-core/src/main/resources/com/android/build/gradle/proguard-optimizations.txt;l=8
-allowaccessmodification

# https://cs.android.com/android-studio/platform/tools/base/+/refs/tags/studio-2021.1.1:build-system/gradle-core/src/main/resources/com/android/build/gradle/proguard-common.txt;l=1
-dontusemixedcaseclassnames

# https://cs.android.com/android-studio/platform/tools/base/+/refs/tags/studio-2021.1.1:build-system/gradle-core/src/main/resources/com/android/build/gradle/proguard-common.txt;l=5-12
# Preserve some attributes that may be required for reflection.
-keepattributes AnnotationDefault,
                EnclosingMethod,
                InnerClasses,
                RuntimeVisibleAnnotations,
                RuntimeVisibleParameterAnnotations,
                RuntimeVisibleTypeAnnotations,
                Signature

# https://cs.android.com/android-studio/platform/tools/base/+/refs/tags/studio-2021.1.1:build-system/gradle-core/src/main/resources/com/android/build/gradle/proguard-common.txt;l=37-41
# For enumeration classes, see http://proguard.sourceforge.net/manual/examples.html#enumerations
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# https://cs.android.com/android-studio/platform/tools/base/+/refs/tags/studio-2021.1.1:build-system/gradle-core/src/main/resources/com/android/build/gradle/proguard-common.txt;l=43-45
-keepclassmembers class * implements android.os.Parcelable {
    public static final ** CREATOR;
}
