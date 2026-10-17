# =========================================================================
# AGRIM DIRECTORY - PRODUCTION R8 / PROGUARD OPTIMIZATION & KEEP RULES
# =========================================================================

# 1. Retrofit 2 & OkHttp3
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn retrofit2.**
-keepattributes Signature, InnerClasses, EnclosingMethod
-keepattributes *Annotation*
-keepclassmembers class * {
    @retrofit2.http.* <methods>;
}

# 2. Gson Serialization & Domain Models (Prevent Field Obfuscation)
-keepattributes *Annotation*, Signature
-keepclassmembers enum * { *; }
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}
-keep class com.namabiz.agrim.core.network.** { *; }
-keep class com.namabiz.agrim.core.model.** { *; }

# 3. Room Database
-keepclassmembers class * extends androidx.room.RoomDatabase {
    public abstract *;
}
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Dao interface * { *; }
-dontwarn androidx.room.paging.**

# 4. Google ML Kit Barcode Vision & CameraX
-keep class com.google.mlkit.vision.** { *; }
-keep class androidx.camera.core.** { *; }
-keep class androidx.camera.lifecycle.** { *; }
-dontwarn com.google.android.gms.**

# 5. Koin Dependency Injection
-keep class io.insertkoin.** { *; }
-keep class * extends org.koin.core.module.Module { *; }
-dontwarn org.koin.**

# 6. WorkManager
-keep class * extends androidx.work.Worker { *; }
-keep class * extends androidx.work.ListenableWorker { *; }

# 7. Kotlin Coroutines & Flow
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
