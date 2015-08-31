# BuzzScreen SDK for Android
- Buzzvil's library for integrating BuzzScreen with Android apps.
- Requires Android version 2.3 (API level 9) or newer.
- Please find the `app_key` on your BuzzScreen dashboard before beginning the SDK integration or running sample applications.
- The Google Play Services SDK must be configured. Please refer to Google's [Setting Up Google Play Services](https://developers.google.com/android/guides/setup) guide for more details.

## Included files
- **buzzscreen-sdk-core**: Core SDK for integrating BuzzScreen. It doesn't include BuzzScreen's default `SimpleLockerActivity` lock screen, meaning you must implement a custom lock screen yourself. (Please refer to [BuzzScreen SDK Integration Guideline - Advanced](ADVANCED-USAGE_EN.md) for more details.)
- **buzzscreen-sdk-full**: Full SDK including BuzzScreen's default `SimpleLockerActivity` lock screen which supports BuzzScreen's basic features. Please use this SDK if you don't need customized features.
- **buzzscreen-sample-basic**: Basic BuzzScreen integration example that uses **buzzscreen-sdk-full**.
- **buzzscreen-sample-custom**: Custom BuzzScreen integration example that uses **buzzscreen-sdk-core** and a customized lock screen. Please compare `SimpleLockerActivity` from **buzzscreen-sdk-full** with `CustomLockerActivity` from **buzzscreen-sdk-core** for further understanding.
- **buzzscreen-sample-multi-process**: BuzzScreen integration sample that separates the lock screen process from the main process in order to increase memory usage efficiency. Please refer to [BuzzScreen SDK Integration Guideline - Advanced](ADVANCED-USAGE_EN.md) for more details.
- **google-play-services_lib**: Google Play Services library. Please refer to Google's [Setting Up Google Play Services](https://developers.google.com/android/guides/setup) guide for more details.

## Basic guide (buzzscreen-sample-basic)
Our simplest integration method – add BuzzScreen to your Android application in just a few easy steps.

### 1. Setup
Download and unzip the [BuzzScreen SDK](https://github.com/Buzzvil/buzzscreen-sdk-publisher/archive/master.zip) and include **buzzscreen-sdk-full** from the unzipped folder in your Android application.

Add permissions, activities, services, and receivers to your Android Manifest as below.

```xml
<!-- Permissions for BuzzScreen -->
<manifest>
    ...
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.READ_PHONE_STATE" />
    <uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
    
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

### 2. Lock Screen Control
- `BuzzScreen.getInstance().launch()`: Call this in your app's launch activity.
- `BuzzScreen.getInstance().activate()`: Shows BuzzScreen on the lock screen.
- `BuzzScreen.getInstance().deactivate()`: Hides BuzzScreen from the lock screen.
- `UserProfile`: Calling `setUserId(String userId)` is required before offering a reward to the user. `userId` is a unique value by which publishers can identify each user, and is delivered in a postback when the BuzzScreen server makes a point accumulation request to the publisher's server. It is also possible for campaigns to target certain users by calling `setBirthYear()`,  `setGender()`, and `setRegion()`. ([Region formatting](REGION-FORMAT.md) will be added later according to your region. Please contact your account manager.)

> Keep in mind that you must set `userId` before calling `BuzzScreen.getInstance().activate()`, and `UserProfile` data including `userId` can be updated later at any time.

### 3. Points Accumulation Request (Postback) - Server to Server Integration
When a point accumulation activity occurs from a user, BuzzScreen does not give the user reward points directly. The BuzzScreen server will make a point accumulation request to the publisher's server and the publisher's server will process the request and provide points for the user.

Regarding processing point accumulation requests, please refer to [BuzzScreen's API guidelines](https://buzzvilian.atlassian.net/wiki/pages/viewpage.action?pageId=4718597) for more details.

> If you would like to send the user a push notification on point accumulation, it should be processed/sent from the publisher's server after receiving a point accumulation request from BuzzScreen.

Point accumulation request flow:
![Task Flow](https://github.com/Buzzvil/buzzscreen-sdk-publisher/blob/master/postback_flow.jpg)

### 4. Additional Features
If you need any of these features, please refer to [BuzzScreen SDK Integration Guideline - Advanced](ADVANCED-USAGE_EN.md):
- Customized lock screen sliding/swiping UI, clock UI, or extra lock screen widgets.
- Separating the lock screen process from main process in order to increase memory usage efficiency.
- Distributing point accumulation request traffic over time instead of concentrated on the hour.
