##############################################
# App - ProGuard / R8 rules
##############################################

# Crashlytics: keep line numbers for readable stacktraces
-keepattributes SourceFile,LineNumberTable

# Kotlin metadata/annotations used by serialization, reflection, Compose tooling, etc.
-keepattributes *Annotation*,InnerClasses,EnclosingMethod

# Kotlinx Serialization (portable template)
-keep @kotlinx.serialization.Serializable class ** { *; }
-keepclassmembers class ** {
    public static ** Companion;
}

-keepclassmembers class **$Companion {
    public kotlinx.serialization.KSerializer serializer(...);
}

-keepclassmembers class **$$serializer { *; }

# Android components instantiated by the system (safer to keep names)
-keep class com.d4rk.lowbrightness.** extends android.app.Service
-keep class com.d4rk.lowbrightness.** extends android.content.BroadcastReceiver
-keep class com.d4rk.lowbrightness.** extends android.accessibilityservice.AccessibilityService
-keep class com.d4rk.lowbrightness.** extends androidx.work.ListenableWorker
-keep class com.d4rk.lowbrightness.** extends androidx.work.Worker

# Optional noise suppression (safe)
-dontwarn kotlinx.coroutines.**
-dontwarn io.ktor.**
