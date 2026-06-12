# Dextera Launcher - Comprehensive R8 / ProGuard Rules
# Optimized for Kotlin + Jetpack Compose + Room + Moshi + Retrofit + Coil

# Default Android optimization rules (included via proguard-android-optimize.txt in build.gradle)
# This file adds project-specific keeps, warnings suppression, and optimizations.

# ============================================================
# General / Kotlin / Coroutines
# ============================================================
-keepattributes *Annotation*, Signature, InnerClasses, EnclosingMethod, SourceFile, LineNumberTable
-keep class kotlin.Metadata { *; }
-keepclassmembers class **.R$* { <fields>; }

# Coroutines
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation
-dontwarn kotlin.coroutines.jvm.internal.**
-dontwarn kotlinx.coroutines.**

# ============================================================
# Jetpack Compose (critical for runtime correctness)
# ============================================================
-keep class androidx.compose.** { *; }
-keep class androidx.compose.runtime.** { *; }
-keep class androidx.compose.ui.** { *; }
-keep class androidx.compose.material3.** { *; }
-keep class androidx.compose.foundation.** { *; }

# Compose compiler generated classes and lambdas
-keep class **$stable { *; }
-keepclassmembers class ** {
    @androidx.compose.runtime.Composable <methods>;
}
-keepclassmembers,allowobfuscation class * {
    @androidx.compose.ui.tooling.preview.Preview <methods>;
}

# (No need for fragile ComposableLambda wildcard keeps — broad compose keeps + AGP's proguard-android-optimize.txt are sufficient and safer.)

# ============================================================
# AndroidX / Lifecycle / ViewModel / DataStore
# ============================================================
-keep class androidx.lifecycle.** { *; }
-keep class androidx.lifecycle.viewmodel.compose.** { *; }
-keep class androidx.datastore.** { *; }
-keepclassmembers class * extends androidx.lifecycle.ViewModel {
    <init>(...);
}
-keepclassmembers class * extends androidx.lifecycle.AndroidViewModel {
    <init>(...);
}

# ============================================================
# Room (KSP generated code)
# ============================================================
-keep class androidx.room.** { *; }
-keep @androidx.room.* class * { *; }
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Dao class * { *; }
-keepclassmembers class * {
    @androidx.room.* <methods>;
}
# Keep generated database and DAOs
-keep class com.example.AppDatabase { *; }
-keep class com.example.UserDao { *; }
-keep class com.example.UserEntity { *; }
-keep class com.example.**Database_Impl { *; }

# ============================================================
# Moshi (codegen + runtime)
# ============================================================
-keep class com.squareup.moshi.** { *; }
-keep @com.squareup.moshi.JsonClass class * { *; }
-keep class **JsonAdapter { *; }
-keepclassmembers class * {
    @com.squareup.moshi.* <methods>;
}
# Your model classes used with Moshi (add more if you have @Json annotated ones)
-keep class com.example.model.** { *; }

# ============================================================
# Retrofit + OkHttp + Logging
# ============================================================
-keep class retrofit2.** { *; }
-keep interface * extends retrofit2.Call { *; }
-keepattributes Signature
-keepattributes Exceptions
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}
-keep class com.example.**Api { *; }   # if you add more Retrofit services later
-dontwarn retrofit2.**
-dontwarn okhttp3.**
-dontwarn okio.**

# ============================================================
# Coil (image loading)
# ============================================================
-keep class coil.** { *; }
-dontwarn coil.**

# ============================================================
# Launcher / System Integrations (very important)
# ============================================================
# Notification Listener
-keep class com.example.service.MyNotificationListenerService { *; }
-keep class com.example.service.MyNotificationListenerService$* { *; }

# Media
-keep class com.example.media.MediaController { *; }
-keep class com.example.model.MediaTrackInfo { *; }
-keep class com.example.model.AppNotification { *; }

# Widgets
-keep class com.example.widgets.** { *; }
-keep class com.example.model.WidgetData { *; }

# Main entry points and state
-keep class com.example.MainActivity { *; }
-keep class com.example.launcher.LauncherState { *; }
-keep class com.example.viewmodel.LauncherViewModel { *; }
-keep class com.example.UniversalSearchEngine { *; }

# All your models (prevents issues with Parcelable / serialization / reflection)
-keep class com.example.model.** { *; }

# AppInfo, ContactInfo etc. used heavily via PackageManager and lists
-keep class com.example.model.AppInfo { *; }
-keep class com.example.model.ContactInfo { *; }
-keep class com.example.model.ListEntry { *; }
-keep class com.example.model.LauncherUiState { *; }

# ============================================================
# AI / Gemini Image Generator (secrets + JSON)
# ============================================================
-keep class com.example.ui.ai.GeminiImageGenerator { *; }
-keep class com.example.ui.ai.** { *; }

# ============================================================
# Onboarding, Settings, Utils
# ============================================================
-keep class com.example.utils.** { *; }
-keep class com.example.ui.screens.OnboardingScreen { *; }
-keep class com.example.ui.settings.SettingsPanel { *; }

# ============================================================
# Reflection / PackageManager / Intent usage (launchers query a lot)
# ============================================================
-keepclassmembers class * {
    public <init>(...);
}
-keep class * implements android.os.Parcelable { *; }
-keepclassmembers class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

# Keep names for things looked up by string (package names, class names)
-keepnames class * { *; }   # conservative but safe for a launcher

# ============================================================
# R8 / Optimization tuning
# ============================================================
# Allow aggressive optimizations while protecting critical paths
-optimizations !code/simplification/arithmetic,!code/simplification/cast,!field/*,!class/merging/*
-optimizationpasses 5
-allowaccessmodification
-dontpreverify

# Full mode friendly rules (R8 is stricter)
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation

# ============================================================
# Warnings (suppress known noisy ones)
# ============================================================
-dontwarn androidx.**
-dontwarn com.google.**
-dontwarn org.jetbrains.kotlin.**
-dontwarn kotlin.**
-dontwarn javax.annotation.**
-dontwarn javax.inject.**
-dontwarn sun.misc.**
-dontwarn com.squareup.okhttp.**
-dontwarn okio.**

# If you see specific missing classes in future builds, add targeted -dontwarn here.

# ============================================================
# Keep everything in debug builds (optional safety)
# You can comment this if you want full shrinking even in some debug variants.
# -if conditionals not used here for simplicity.
