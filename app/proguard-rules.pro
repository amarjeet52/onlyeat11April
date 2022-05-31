# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /home/cbl80/Android/Sdk/tools/proguard/proguard-android.txt
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

##-----------------Event bus----------

#-keepattributes *Annotation*
#-keepclassmembers class ** {
#    @org.greenrobot.eventbus.Subscribe <methods>;
#}
#-keep enum org.greenrobot.eventbus.ThreadMode { *; }

##-----------------------------------

##-----------------lottie image----------

#-dontwarn com.airbnb.lottie.**
#-keep class com.airbnb.lottie.** {*;}

##--------------------------------



##------------------Conketta payment

#-keep class com.conekta.** { *; }
##--------------------------------


##-----------------Razor Pay-------------
#-keepclassmembers class * {
#    @android.webkit.JavascriptInterface <methods>;
#}

#-keepattributes JavascriptInterface
#-keepattributes *Annotation*

#-dontwarn com.razorpay.**
#-keep class com.razorpay.** {*;}

#-optimizations !method/inlining/*

#-keepclasseswithmembers class * {
#  public void onPayment*(...);
#}

##--------------------------------------------


##----------Square Pay-----------------

#-keep class sqip.** { *; }

##----------------------------------

##---------New Relic------------

#-keep class com.newrelic.** { *; }
#-dontwarn com.newrelic.**
#-keepattributes Exceptions, Signature, InnerClasses, LineNumberTable

#------------------------------

##--------------------Glide----------

#-keep public class * implements com.bumptech.glide.module.GlideModule
#-keep public class * extends com.bumptech.glide.module.AppGlideModule
#-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
#  **[] $VALUES;
#  public *;
#}

# for DexGuard only
#-keepresourcexmlelements manifest/application/meta-datavalue=GlideModule

##-------------------------------------


#-keepnames class com.path.to.your.ParcelableArg
#-keepnames class com.path.to.your.SerializableArg
#-keepnames class com.path.to.your.EnumArg

# did not work for minify=true
#-keepclassmembers class io.grpc.okhttp.OkHttpChannelBuilder {
#  io.grpc.okhttp.OkHttpChannelBuilder forTarget(java.lang.String);
#  io.grpc.okhttp.OkHttpChannelBuilder scheduledExecutorService(java.util.concurrent.ScheduledExecutorService);
#  io.grpc.okhttp.OkHttpChannelBuilder sslSocketFactory(javax.net.ssl.SSLSocketFactory);
#  io.grpc.okhttp.OkHttpChannelBuilder transportExecutor(java.util.concurrent.Executor);
#}