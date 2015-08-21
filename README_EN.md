# BuzzScreen SDK for Android
- Library for integrating BuzzScreen with Android application
- Required Android version: From Android 2.3(API Level 9)
- Please check out your app_key on BuzzScreen Admin before you start the SDK integration or running sample applications attached in this document. 
- Google Play services API setting required: Please check out [ Google Play services library setting ](https://developers.google.com/android/guides/setup) for further details.


## Folder instructions
- **buzzscreen-sdk-core** : Essential SDK for integrating BuzzScreen. It doesn't include default lock screen feature provided by BuzzScreen (SimpleLockerActivity), meaning you may write a new code to customize lock screen. (Please refer to [ BuzzScreen SDK Integration Guideline - Advanced ](https://github.com/Buzzvil/buzzscreen-sdk-publisher/blob/master/ADVANCED-USAGE_EN.md) for more details)
- **buzzscreen-sdk-full** : Full spec SDK buzzscreen-sdk-core which includes default locker feature (SimpleLockerActivity). SimpleLockerActivity is the default lock screen feature which has the basic functions provided by BuzzScreen. Please use this SDK if you don't need customized functions.
- **buzzscreen-sample-basic** : BuzzScreen integration sample in the most basic format, integrating buzzscreen-sdk-full.
- **buzzscreen-sample-custom** : BuzzScreen integration sample with customized lock screen, integrating buzzscreen-sdk-core. Please compare SimpleLockerActivity from buzzscreen-sdk-full with CustomLockerActivity from buzzscreen-sdk-core for further understanding.
- **buzzscreen-sample-multi-process** : BuzzScreen integration aample that separates lock screen process from main process in order to increase the efficiency in memory usage. Please refer to [ BuzzScreen SDK Integration Guideline - Advanced ](https://github.com/Buzzvil/buzzscreen-sdk-publisher/blob/master/ADVANCED-USAGE_EN.md)
- **google-play-services_lib** : Google Play services library. Please refer to [ Google Play services library setting ](https://developers.google.com/android/guides/setup)


## BuzzScreen SDK Integration Guideline - Basic
The most basic integration method: Only with this simple integration process, you may add BuzzScreen to your Android application.

Reference Sample : **buzzscreen-sample-basic**

### 1. Setting
- After [downloading SDK](https://github.com/Buzzvil/buzzscreen-sdk-publisher/archive/master.zip), unzip it.
- Include buzzscreen-sdk-full from the unzipped folder into your developing Android application.
- Android Manifest : Please add permissions, activities, services, and receivers as below.

```Xml
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
        <!-- Setting for Google Play Services -->
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

- Application Class : Please add Initialization function (BuzzScreen.init) to onCreate

```Java
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ...
        // app_key : unique key value for publisher. Please check it on the BuzzScreen admin page.
        // SimpleLockerActivity.class : lock screen activity class
        // R.drawable.image_on_fail : a back fill image to be shown when network error occurs or there is no campaign available.
        // useMultiProcess : if lock screen is separated from main process, it's true. if not, it's false.
        BuzzScreen.init("app_key", this, SimpleLockerActivity.class, R.drawable.image_on_fail, false);
    }
}
```

### 2. Lock Screen Control
- BuzzScreen.getInstance().launch() : Please add it into the activity which is first called at the point of app launch
- BuzzScreen.getInstance().activate() : Activating BuzzScreen. BuzzScreen will be shown on lock screen after this function is called.
- BuzzScreen.getInstance().deactivate() : De-activating BuzzScreen. BuzzScreen will be no longer shown on lock screen after this function is called.
- UserProfile : Setting user profile. In order to offer reward for user, it's required to call UserProfile.setUserId(String userId). userId is a unique value (id) by which publisher can identify each user. UserId is delivered in postback when BuzzScreen server makes a point accumulation request to publisher's server. Also, it is possible for campaign to target certain users by calling setBirthYear,  setGender, setRegion (please refer to: [ Region Format ](https://github.com/Buzzvil/buzzscreen-sdk-publisher/blob/master/REGION-FORMAT.md)). Region format will be added later according to your region. Please ask your account manager.

> Keep in mind that you must set userId before calling BuzzScreen.getInstance().activate() and UserProfile data including userId could be changed later at anytime.

### 3. Points Accumulation Request (Postback) - Server to Server Integration
- When a point accumulation activity occurs from user, BuzzScreen does not give user reward points directly. BuzzScreen server will make a point accumulation request to publisher's server and the publisher's server shall process this request. (provide points for user)
- Regarding how to process point accumulation request, please refer to [ BuzzScreen API Guideline ](https://buzzvilian.atlassian.net/wiki/pages/viewpage.action?pageId=4718597)

> If you would like to send user a push notification on point accumulation, it shall be processed/sent from publisher's server after receiving point accumulation request from BuzzScreen.

####Point Accumulation Request Flow
![Task Flow](https://github.com/Buzzvil/buzzscreen-sdk-publisher/blob/master/postback_flow.jpg)

### 4. Additional Features
If you need features below, please refer to [ BuzzScreen SDK Integration Guideline - Advanced ](https://github.com/Buzzvil/buzzscreen-sdk-publisher/blob/master/ADVANCED-USAGE_EN.md)
- Lock screen customization : Changing watch UI and lock screen slider UI (swiping UI), and adding widgets
- Process separation : Separating lock screen process from main process in order to increase efficiency in memory usage
- Distributing point accumulation request traffic, which is concentrated on the hour
