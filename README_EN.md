# BuzzScreen SDK for Android
- Buzzvil's library for integrating BuzzScreen with Android apps.
- Requires Android version 4.0.3 (API level 15) or newer.
- Below are the key values which need to be created in order to begin the integration:
	* `app_key` : Please find the `app_key` on your BuzzScreen dashboard. Please ask your account manager if the account details are not provided yet. ([Required in the `BuzzScreen.init` method](#1-initialization--call-init-and-launch))
	* `app_license` : Please ask your account manager. ([Required in the `AndroidManifest.xml` setting](#add-the-following-codes-to-androidmanifestxml))
	* `plist` : Please ask your account manager. ([Required in the `AndroidManifest.xml` setting](#add-the-following-codes-to-androidmanifestxml))
- You will be required to submit the integrated APK file to your account manager for review before going live.


## Basic guide (sample/basic)
Our simplest integration method – add BuzzScreen to your Android application in just a few easy steps.

The reference sample : **sample/basic**
> Please refer to the sample before applying to your code.

### 1. Add Libraries

#### Add the following codes to `build.gradle` in your app module.

```
repositories {
    maven { url "https://dl.bintray.com/buzzvil/buzzscreen/" }
    maven { url "http://dl.appnext.com/" }
}

...

dependencies {
    compile 'com.buzzvil:buzzscreen:1.+'
    compile 'com.google.android.gms:play-services-ads:8.4.0'
    compile 'com.google.android.gms:play-services-location:8.4.0'
}

```
> Please change the version number (8.4.0) in `com.google.android.gms:play-services-ads:8.4.0` and `com.google.android.gms:play-services-location:8.4.0` to match them with the version of Google Play Services in your app.
Otherwise errors such as `com.android.dex.DexException` may occur when compiling.

#### Add the following codes to `AndroidManifest.xml`.
```Xml
<manifest>
    <application>
        ...
        <!-- Configuration for BuzzScreen-->
        <meta-data
            android:name="app_license"
            android:value="<app_license>" />
        <meta-data
            android:name="com.buzzvil.locker.mediation.baidu.plist"
            android:value="<plist>" />
    </application>
</manifest>
```
> Please ask your account manager for `<app_license>` and `<plist>`.

### 2. Call Methods
In order to integrate BuzzScreen into Android apps, please follow the 3 steps below:
	**1) Initialization -> 2) Set User Profile -> 3) Control Lock Screen**


#### 1) Initialization : call init() and launch()
- `BuzzScreen.init()` : Add to onCreate in your application class, in order to call this method in advance of the others. Please see the required parameters below.
   - String appKey : `app_key`. Please find the `app_key` on your BuzzScreen dashboard. 
   - Context context : Use `this` for application context
   - Class<?> lockerActivityClass : Lock screen activity class. When no customization is applied, set as `SimpleLockerActivity.class` which is provided in the SDK. When lock screen is to be customized to suit unique needs of yours, set custom-built acitivy class. Please refer to 'Lock Screen Customization' on [BuzzScreen SDK Advanced Integration](docs/ADVANCED-USAGE_EN.md)
   - int imageResourceIdOnFail : An image file to be displayed when either network error occurs or there is no campaign available temporarily. Include the file in resource inside the app. Set resource id of the image.
 
     > **Note** : If it is the first time to create application class in your app, please do not forget to register application class in AndroidManifest.xml
     
```Java
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ...
        // app_key : `app_key` for SDK usage. Find it on your BuzzScreen dashboard.
        // SimpleLockerActivity.class : Lock screen activity class
        // R.drawable.image_on_fail : An image file to be displayed when either network error occurs or there is no campaign available temporarily
        BuzzScreen.init("app_key", this, SimpleLockerActivity.class, R.drawable.image_on_fail);
    }
}
```

- `BuzzScreen.getInstance().launch()` : Call this in your app's launch activity.


#### 2) Set User Profile
- `UserProfile` : Set user profile information in this Class. Calling `setUserId(String userId)` is required before offering a reward to the user. `userId` is a unique value by which publishers can identify each user, and is delivered in a postback when the BuzzScreen server fires a point accumulation request to the publisher's server. It is also possible for campaigns to target certain users by calling `setBirthYear`, `setGender`, and `setRegion`. 

- It is possible receive `UserProfile` by calling `BuzzScreen.getInstance().getUserProfile()`.

##### A list of methods in UserProfile Class
- `setUserId(String userId)` : Set User Id (**Required**)
- `setBirthYear(int birthYear)` : Set age by Year of Birth of the user in 4 digits (e.g, 1988)
- `setGender(String gender)` : Set gender by using predefined strings below: 
    - `UserProfile.USER_GENDER_MALE` : For male
    - `UserProfile.USER_GENDER_FEMALE` : For female
- `setRegion(String region)` : Set region by referring to [Region Format](docs/REGION-FORMAT.md)
    - **Note** : Region targeting is only available in Korea at this moment. More countries will be supported shortly. Please ask your account manager for further details.
    
> Keep in mind that you must set userId before calling BuzzScreen.getInstance().activate().


#### 3) Lock Screen Control
- `BuzzScreen.getInstance().activate()` : Activate BuzzScreen. 

    - `activate()` doesn't need to be called again unless `deactivate()` is called. BuzzScreen will be kept activated once activate() is called and lock screen cycle will be managed automatically since then. 

    - If un-removable ongoing notification is created at Notification area after calling this method, please refer to [Lock Screen Service Notification](docs/LOCKSCREEN-SERVICE-NOTIFICATION_EN.md). It helps hide it.
    
    - If you want to catch the even when the lock screen is ready for the first time after the `activate()` method has been called, implement the below interface and pass it as the parameter for the activate() method as `BuzzScreen.getInstance().activate(ActivateListener listener)`
          
        ```Java
        public interface ActivateListener {
            void onReady();// This will be called when the first lockscreen is ready to be shown.
        }
        ```   

- `BuzzScreen.getInstance().deactivate()` : Deactivate BuzzScreen.
    - `BuzzScreen.getInstance().logout()` : Call this when the user has loggeed-out. This method calls `deactivate()`, and removes all the UserProfile related info from the device.


### 3. Points Accumulation Request (Postback) - Server to Server Integration
- When a point accumulation activity occurs from a user, BuzzScreen does not give the user reward points directly. The BuzzScreen server will make a point accumulation request to the publisher's server and the publisher's server will process the request and provide points for the user.

- Regarding processing point accumulation requests, please refer to [BuzzScreen's API guidelines](docs/POSTBACK_EN.md) for more details.

> If you would like to send the user a push notification on point accumulation, it should be processed/sent from the publisher's server after receiving a point accumulation request from BuzzScreen.

#### Point accumulation request flow:
![Task Flow](docs/postback_flow.jpg)


### 4. Additional Features
- If you need any of these features, please refer to [BuzzScreen SDK Integration Guideline - Advanced](docs/ADVANCED-USAGE_EN.md):
    - Customized lock screen sliding/swiping UI, clock UI, or extra lock screen widgets.
    - Separating the lock screen process from main process in order to increase memory usage efficiency.

- To enable customized targeting features not provided in the SDK by default, please refer to ["BuzzScreen Custom Targeting"](docs/CUSTOM_TARGETING_EN.md).

- To customize lockscreen service notification, please refer to [Locksceen Service Notification Guideline](docs/LOCKSCREEN-SERVICE-NOTIFICATION_EN.md).
