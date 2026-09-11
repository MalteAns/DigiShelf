# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.

# Preserve line numbers for stack traces
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# ML Kit Barcode Scanning & Vision
-keep class com.google.mlkit.** { *; }
-keep interface com.google.mlkit.** { *; }
-keep class com.google.android.gms.internal.mlkit_vision_barcode.** { *; }
-keep class com.google.android.gms.internal.mlkit_vision_common.** { *; }
-keep class com.google.android.gms.vision.** { *; }

# Keep ML Kit ComponentRegistrars which are loaded reflectively via AndroidManifest metadata
-keep class * implements com.google.mlkit.common.sdkinternal.ComponentRegistrar {
    public <init>();
}
-keep class * extends com.google.mlkit.common.sdkinternal.MlKitComponentDiscoveryService {
    public <init>();
}

# Keep ML Kit native methods
-keepclasseswithmembernames class * {
    native <methods>;
}

# Google Play Services & Dynamite
-keep class com.google.android.gms.dynamite.** { *; }
-keep class com.google.android.gms.common.** { *; }

# CameraX
-keep class androidx.camera.core.** { *; }
-keep interface androidx.camera.core.** { *; }
-keep class androidx.camera.camera2.** { *; }
-keep interface androidx.camera.camera2.** { *; }
-keep class androidx.camera.lifecycle.** { *; }
-keep class androidx.camera.view.** { *; }
-dontwarn androidx.camera.**
