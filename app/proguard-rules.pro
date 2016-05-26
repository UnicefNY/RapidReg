# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/bfeng/Android/android-sdk-macosx/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

-keep class com.squareup.okhttp.** { *; }
-keep class retrofit.** { *; }
-keep interface com.squareup.okhttp.** { *; }

-dontwarn com.squareup.okhttp.**
-dontwarn okio.**
-dontwarn retrofit2.**
-dontwarn rx.**
-dontwarn android.net.http.**
-dontwarn org.apache.http.**

-keepclasseswithmembers class * {
    @retrofit.http.* <methods>;
}

-keepattributes Exceptions
-keepattributes Signature

-dontnote android.net.http.*
-dontnote org.apache.http.*
-dontwarn org.w3c.dom.bootstrap.DOMImplementationRegistry

-keep class * extends com.raizlabs.android.dbflow.config.DatabaseHolder { *; }

# Remember keeping your model classes below
