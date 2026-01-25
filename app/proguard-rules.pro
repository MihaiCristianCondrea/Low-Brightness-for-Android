##############################################
# App - ProGuard / R8 rules
##############################################

# Crashlytics: keep line numbers for readable stacktraces
-keepattributes SourceFile,LineNumberTable

# Kotlinx Serialization
-keepattributes *Annotation*,InnerClasses,EnclosingMethod

-keep @kotlinx.serialization.Serializable class com.d4rk.android.apps.** { *; }

-keepclassmembers class com.d4rk.android.apps.** {
    public static ** Companion;
}

-keepclassmembers class **$Companion {
    public kotlinx.serialization.KSerializer serializer(...);
}

-keepclassmembers class **$$serializer { *; }

# Optional noise suppression (safe)
-dontwarn kotlinx.coroutines.**
-dontwarn io.ktor.**
