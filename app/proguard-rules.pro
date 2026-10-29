# R8 / ProGuard rules for TalentStream

# Room
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**

# Domain models
-keepclassmembers class com.pawan.hirejetpack.domain.** { *; }

# Flutter add-to-app
-keep class io.flutter.** { *; }
-dontwarn io.flutter.**
