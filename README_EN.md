# BuzzScreen SDK for Android
- Buzzvil's library for integrating BuzzScreen with Android apps.
- Requires Android version 3.0 (API level 11) or newer.
- Please find the `app_key` on your BuzzScreen dashboard before beginning the SDK integration or running sample applications.
- The Google Play Services SDK must be configured. Please refer to Google's [Setting Up Google Play Services](https://developers.google.com/android/guides/setup) guide for more details.

	> If you are using android studio, just add `compile 'com.google.android.gms:play-services-ads:7.5.0'` to **build.gradle > dependencies**.


## Included files
- **aars/** : Android Library files for Android Studio
    - **buzzscreen-sdk-core.aar** : Core SDK for integrating BuzzScreen. It doesn't include BuzzScreen's default `SimpleLockerActivity` lock screen, meaning you must implement a custom lock screen yourself. (Please refer to [BuzzScreen SDK Integration Guideline - Advanced](ADVANCED-USAGE_EN.md) for more details.)
    - **buzzscreen-sdk-full.aar** : Full SDK including BuzzScreen's default `SimpleLockerActivity` lock screen which supports BuzzScreen's basic features. Please use this SDK if you don't need customized features.
- **sample/** : BuzzScreen sample module that enables to build the sample app. It has productFlavors called basic, custom, and multiProcess that represent all versions of integration.
    - **basic** : Basic BuzzScreen integration example that uses **buzzscreen-sdk-full**.
    - **custom** : Custom BuzzScreen integration example that uses **buzzscreen-sdk-core** and a customized lock screen. Please compare `SimpleLockerActivity` from **buzzscreen-sdk-full** with `CustomLockerActivity` from **buzzscreen-sdk-core** for further understanding.
    - **multiProcess** : BuzzScreen integration sample that separates the lock screen process from the main process in order to increase memory usage efficiency. Please refer to [BuzzScreen SDK Integration Guideline - Advanced](ADVANCED-USAGE_EN.md) for more details.

## Basic guide (sample-basic)
Our simplest integration method – add BuzzScreen to your Android application in just a few easy steps.

Download and unzip the [BuzzScreen SDK](https://github.com/Buzzvil/buzzscreen-sdk-publisher/archive/master.zip) and include **aars/buzzscreen-sdk-full** from the unzipped folder in your Android application.

### 1. Add Libraries

#### For Android Studio
- Go to [BuzzScreen SDK Releases](https://github.com/Buzzvil/buzzscreen-sdk-publisher/releases) and download the latest release of buzzscreen-sdk-full_*VERSION*.aar.
- How to add aar as library
    1. Put the downloaded aar file to libs/ directory of your app module.
    2. Add to build.gradle : 
    ```
    dependencies {
        compile(name:'buzzscreen-sdk-full_VERSION', ext:'aar')
    }

    repositories{
        flatDir{
            dirs 'libs'
        }
    }
    ```

- Add dependencies of **google play service library** and **universal image loader library** by adding following lines to 'dependencies' in build.gradle.

```
dependencies {
    compile 'com.google.android.gms:play-services-ads:9.4.0'
    compile 'com.nostra13.universalimageloader:universal-image-loader:1.9.4'
}
```

##### Facebook Audience Network library 
Download [AudienceNetwork.jar](libs/AudienceNetwork.jar) and add it as library. This is for using [Facebook Audience Network](https://developers.facebook.com/docs/audience-network) so if you are already using it, please don't add it again.

### 2. Setup

Add permissions, activities, services, and receivers to your Android Manifest as below.

> Since version 1.2.0, ChangeAdReceiver and DownloadAdReceiver are deprecated.

```xml
<manifest>
    ...
    <!-- Permissions for BuzzScreen -->
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />

    <application>
        ...
        <!-- Google Play Services -->
        <meta-data android:name="com.google.android.gms.version"
            android:value="@integer/google_play_services_version" />

        <!-- Activities for BuzzScreen -->
        <activity
            android:name="com.buzzvil.buzzscreen.sdk.SimpleLockerActivity"
            android:excludeFromRecents="true"
            android:launchMode="singleTask"
            android:screenOrientation="portrait"
            android:taskAffinity="<MY_PACKAGE_NAME>.Locker" />
        <activity
            android:name="com.buzzvil.buzzscreen.sdk.LandingHelperActivity"
            android:excludeFromRecents="true"
            android:noHistory="true"
            android:screenOrientation="portrait"
            android:taskAffinity="<MY_PACKAGE_NAME>.Locker" />

        <!-- Service for BuzzScreen -->
        <service android:name="com.buzzvil.buzzscreen.sdk.LockerService" />

        <!-- Receivers for BuzzScreen -->
        <receiver
            android:name="com.buzzvil.buzzscreen.sdk.BootReceiver"
            android:exported="false" >
            <intent-filter>
                <action android:name="android.intent.action.BOOT_COMPLETED" />
            </intent-filter>
        </receiver>
        <receiver
            android:name="com.buzzvil.buzzscreen.sdk.UpdateReceiver"
            android:exported="false" >
            <intent-filter>
                <action android:name="android.intent.action.PACKAGE_REPLACED" />
                <data android:scheme="package" />
            </intent-filter>
        </receiver>

        <!--Deprecated. No need to add these receivers -->
        <receiver android:name="com.buzzvil.buzzscreen.sdk.ChangeAdReceiver" />
        <receiver android:name="com.buzzvil.buzzscreen.sdk.DownloadAdReceiver" />
    </application>
</manifest>
```

Add `BuzzScreen.init()` to onCreate in your Application class.

```java
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ...
        // app_key: Unique key value for publisher. Please find it on your BuzzScreen dashboard.
        // SimpleLockerActivity.class: Lock screen activity class.
        // R.drawable.image_on_fail: A back fill image to be shown when a network error occurs or there is no campaign available.
        // useMultiProcess: true if the lock screen is separated from the main process, otherwise false.
        BuzzScreen.init("app_key", this, SimpleLockerActivity.class, R.drawable.image_on_fail, false);
    }
}
```

##### ProGuard Configuration
To prevent ProGuard from stripping away required classes, add the following lines in your ProGuard configuration file.
```
-keep class com.buzzvil.buzzscreen.sdk.** {*;}
-keep interface com.buzzvil.buzzscreen.sdk.** {*;}

-keep class com.google.android.gms.common.GooglePlayServicesUtil {*;}
-keep class com.google.android.gms.ads.identifier.AdvertisingIdClient {*;}
-keep class com.google.android.gms.ads.identifier.AdvertisingIdClient$Info {*;}

-dontwarn com.facebook.ads.internal.**
-dontwarn com.google.android.gms.ads.**
```

### 3. Lock Screen Control
- `BuzzScreen.getInstance().launch()`: Call this in your app's launch activity.
- `BuzzScreen.getInstance().activate()`: Shows BuzzScreen on the lock screen.
- `BuzzScreen.getInstance().deactivate()`: Hides BuzzScreen from the lock screen.
- `UserProfile`: Calling `setUserId(String userId)` is required before offering a reward to the user. `userId` is a unique value by which publishers can identify each user, and is delivered in a postback when the BuzzScreen server makes a point accumulation request to the publisher's server. It is also possible for campaigns to target certain users by calling `setBirthYear()` and `setGender()`.

> Keep in mind that you must set `userId` before calling `BuzzScreen.getInstance().activate()`, and `UserProfile` data including `userId` can be updated later at any time.

### 4. Points Accumulation Request (Postback) - Server to Server Integration
When a point accumulation activity occurs from a user, BuzzScreen does not give the user reward points directly. The BuzzScreen server will make a point accumulation request to the publisher's server and the publisher's server will process the request and provide points for the user.

Regarding processing point accumulation requests, please refer to [BuzzScreen's API guidelines](POSTBACK_EN.md) for more details.

> If you would like to send the user a push notification on point accumulation, it should be processed/sent from the publisher's server after receiving a point accumulation request from BuzzScreen.

Point accumulation request flow:
![Task Flow](postback_flow.jpg)

### 5. Additional Features
- If you need any of these features, please refer to [BuzzScreen SDK Integration Guideline - Advanced](ADVANCED-USAGE_EN.md):
    - Customized lock screen sliding/swiping UI, clock UI, or extra lock screen widgets.
    - Separating the lock screen process from main process in order to increase memory usage efficiency.

- To customize ongoing service notification, please refer to [Locksceen Service Notification Guideline](LOCKSCREEN-SERVICE-NOTIFICATION_EN.md).
