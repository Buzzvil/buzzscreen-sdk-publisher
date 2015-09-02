# BuzzScreen SDK Advanced Integration
*Please make sure to read [BuzzScreen SDK for Android](README_EN.md) first.*
- [Lock screen customization](#lock-screen-customization): Customized lock screen sliding/swiping UI, clock UI, and extra lock screen widgets.
- [Process separation](#process-separation): Separating the lock screen process from main process in order to increase memory usage efficiency.
- [Distributing point accumulation request traffic](#distributing-point-accumulation-request-traffic): Distributing point accumulation request traffic over time instead of concentrated on the hour.

## Lock screen customization
Sample files are located in **buzzscreen-sample-custom**.

The lock screen consists of one activity. Just like a standard activity, you should create the layout and call a few essential functions inside the activity class. If you customize the lock screen, please use **buzzscreen-sdk-core** instead of **buzzscreen-sdk-full** in the integration process.

#### Layout
The layout basically consists of a clock, slider, and background gradation as below. You may add additional views if necessary.

[Layout Guideline](https://drive.google.com/file/d/0BxlsmkGYXVSyYUhDREkxYTl6STg/view?usp=sharing):

![Layout](layout.jpg)

- Clock: Add a view to layout, and process it upon `onTimeUpdated()` inside your activity.
- Slider: You must change all the image files that make up the slider.

    |Slider Attribute|Description|
    |--------|--------|
    |slider:left_icon|left icon of the slider|
    |slider:right_icon|right icon of the slider|
    |slider:pointer|center image of the slider|
    |slider:pointer_drag|center image of the slider during touch|
    |slider:radius|the distance between slider center and the center of left/right icons|

- Background: As readability of clock and slider may be affected by the color of campaign images, it is necessary to set up a background gradation under the UI.
- Additional views: Just like a standard view, you may add a view to the layout, and write a feature inside the activity.

#### Activity Class
Please create an activity inheriting `BaseLockerActivity` and pass it to `BuzzScreen.init()`. The slider and clock must be implemented inside Activity, while the others are optional.

##### Slider
Slider is an independent view from lock screen, so two more steps are required in order to connect it to the lock screen.

- Set up listeners for the slider through `Slider.setLeftOnSelectListener()` and `Slider.setRightOnSelectListener()`. Please call either the unlock function or the landing function depending on the sliding direction. 
- It is necessary to change the slide points in accordance with the campaign type. Please find `onCurrentCampaignUpdated()`, a function in the Activity called at the point of campaign change, and call `Slider.setLeftText()` and `Slider.setRightText()`.

##### Clock
If an `onTimeUpdate()` method is defined in your Activity, it will automatically be called by BuzzScreen every minute, upon which you should update your view.

##### Etc
- Previous/next page icons: Call `setPageIndicators()` and set up a view to display previous/next icons (ideally arrows) to indicate if there is more content that can be swiped up/down to.
- setOnTrackingListener: Impression and click events can be tracked through this listener.

## Process separation
Sample files are located in **buzzscreen-sample-multi-process**.

The BuzzScreen SDK is always running/using Android's service component in the foreground. Thus, in case the BuzzScreen service is running in the same process as your app, memory for BuzzScreen will be managed together your main process, which will result in inefficient memory usage. To prevent this, it's necessary to separate the process in which BuzzScreen is running from the main process.

- In `BuzzScreen.init()`, please set `useMultiProcess` to true.
- Please add our `MultipleProcessesReceiver` to your Android Manifest file.
```xml
<receiver
    android:name="com.buzzvil.buzzscreen.sdk.MultipleProcessesReceiver"
    android:process=":locker" />
```
- Please add a `android:process=":locker"` attribute to the existing BuzzScreen components in your Android Mainfest file. Components in which the attribute should be added to include `SimpleLockerActivity` (unless you're customizing the lock screen, in which case it should be added to the activity controlling the lock screen), `LandingHelperActivity`, `LockerService`, `ChangeAdReceiver`, and `DownloadAdReceiver`.

> **Warning**: After enabling process separation, the lock screen will run separately from your app's main process. Please be careful when developing a feature related to your app on the lock screen area.


## Distributing point accumulation request traffic
By default, when BuzzScreen makes point accumulation requests to a publisher's server, the traffic is concentrated every hour on the hour. In order to distribute the traffic, please call `BuzzScreen.init()` and then `BuzzScreen.set.BasePointsSpreadingFactor()` with an integer between 0 and 30:

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
        
        // Distribute the traffic over 10 minutes.
        BuzzScreen.setBasePointsSpreadingFactor(10);
    }
}
```

> Please keep in mind that by using this feature, you are potentially giving a different experience to each user.
