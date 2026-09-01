# ProGuard rules for OldAni
-keepattributes *Annotation*
-keep class com.oldani.model.** { *; }
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn com.bumptech.glide.**