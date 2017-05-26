# BuzzScreen SDK Advanced Integration
*Please make sure to read [BuzzScreen SDK for Android](https://github.com/Buzzvil/buzzscreen-sdk-publisher/blob/master/README_EN.md) first.*
- [Lock screen customization](#lock-screen-customization): Customized lock screen slider UI, clock UI, and additional lock screen widgets.
- [Process separation](#process-separation): Separating the lock screen process from main process in order to increase memory  efficiency.


## Lock Screen Customization
Sample files are located in **sample/custom** of the SDK.
> Please refer to the sample before applying to your app.

The lock screen consists of one activity. Just like a standard activity, you should create the layout and call a few essential methods inside the activity class.


### Layout
The layout basically consists of **a clock, slider, and background gradation** as below. Among them, **required** are the clock and the slider. You may also add additional views such as camera, etc. if necessary.

[Layout Guideline](https://drive.google.com/file/d/0BxlsmkGYXVSyYUhDREkxYTl6STg/view?usp=sharing):

![Layout](layout.jpg)

- Clock: Add a view to layout, and update view with the currnet time in the `onTimeUpdated()` of your lock screen activity.
- Slider: You can customize all the image files that make up the slider.

    |Slider Attribute|Description|
    |--------|--------|
    |slider:sl_left_icon|left icon of the slider|
    |slider:sl_right_icon|right icon of the slider|
    |slider:sl_pointer|center image of the slider|
    |slider:sl_pointer_drag|center image of the slider during touch|
    |slider:sl_radius|the distance between slider center and the center of left/right icons|
    |slider:sl_text_size|the size of the reward point texts(Default : 14sp)|

    Slider Attribute example
    ```Xml
    <com.buzzvil.buzzscreen.sdk.widget.Slider
        android:id="@+id/locker_slider"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_alignParentBottom="true"
        android:layout_centerHorizontal="true"
        android:layout_marginBottom="8dp"
        slider:sl_left_icon="@drawable/locker_landing"
        slider:sl_pointer="@drawable/locker_slider_normal"
        slider:sl_pointer_drag="@drawable/locker_slider_drag"
        slider:sl_radius="128dp"
        slider:sl_right_icon="@drawable/locker_unlock"
        slider:sl_text_size="14sp" />
    ```

- Background gradation: As readability of clock and slider may be affected by the color of campaign images, it is highly recommended to set up a background gradation under the UI.
- Additional views: Just like a standard view, you may add a view to the layout, and implement the features inside the activity.


### Activity Class
Please create an activity that inherits `BaseLockerActivity` and pass it to `BuzzScreen.init()`. **The slider and clock** must be implemented inside the activity, while the others are optional.

#### Set `AndroidManifest.xml`
If the name of class inheriting `BaseLockerActivity` is `CustomLockerActivity`:
```Xml
<manifest>
    <application>
        ...
        <activity
            android:name=".CustomLockerActivity"
            android:excludeFromRecents="true"
            android:launchMode="singleTask"
            android:screenOrientation="portrait"
            android:taskAffinity="${applicationId}.Locker" />
    </application>
</manifest>

```
> `excludeFromRecents`, `launchMode`, `screenOrientation`, `taskAffinity` must be set as above. 


#### Slider
Slider is an independent view from lock screen, so two more steps are required in order to connect it to the lock screen.

- Set up listeners for the slider through `Slider.setLeftOnSelectListener()` and `Slider.setRightOnSelectListener()`. Please call either the unlock method or the landing method inside the callbacks accordingly.
- It is necessary to change the slide points in accordance with the campaign when the current campaign is updated. Please find `onCurrentCampaignUpdated()`, a function in the activity called at the point of campaign change, and call `Slider.setLeftText()` and `Slider.setRightText()` using the information of the current campaign.


#### Clock
The `onTimeUpdated` function in your `BaseLockerActivity` is called every minute. Override the fuction to update time-related information such as time, am/pm, date, etc, by using the parameters passed in the fuction.

> **Note** - It is required to override `onCurrentCampaignUpdated` and `onTimeUpdated` in your lock screen activity. Otherwise errors will occur. Please refer to **CustomLockerActivity.java** in the sample for more detailed implementation examples.


#### Additional Feature
##### Previous/Next Page Icons
Call `setPageIndicators()` and set up a view to display previous/next icons (ideally arrows) to indicate if there is more content that can be swiped up/down to.

Method prototype(in BaseLockerActivity)
```Java
// previous : View indicating that the previous page exists.
// next : View indicating that the next page exists.
protected void setPageIndicators(View previous, View next) {
    ...
}
```

Example

```Java
setPageIndicators(
    findViewById(R.id.locker_arrow_top),
    findViewById(R.id.locker_arrow_bottom)
);
```

##### Tracking Impression and Click Events
Impression and click events can be tracked through `setOnTrackingListener()`.

Example

```Java
setOnTrackingListener(new OnTrackingListener() {

    @Override
    public void onImpression(Campaign campaign) {
        // Necessary/desired features on impression
    }
    
    @Override
    public void onClick(Campaign campaign) {
        // Necessary/desired features on click
    }
    
});
```

##### Customizing Lock Screen Campaign Transitional Effect
You can customize campaign transitional effect by passing your own `ViewPager.PageTransformer` as the parameter using `setPageTransformer()` method.

- Refer to [Android developer's guide about PageTransformer](http://developer.android.com/intl/ko/reference/android/support/v4/view/ViewPager.PageTransformer.html#transformPage(android.view.View, float)) to get the details on how to customize.

Method prototype(in BaseLockerActivity)
```Java
protected void setPageTransformer(ViewPager.PageTransformer transformer) { 
    ... 
}
```

Example

```Java
setPageTransformer(new ViewPager.PageTransformer() {
    @Override
    public void transformPage(View page, float position) {
        if (Build.VERSION.SDK_INT >= 11) {
            int pageHeight = page.getHeight();
            if (0 <= position && position <= 1) {
                page.setTranslationY(pageHeight * -position + (position * pageHeight / 4));
            }
        }
    }
});
```

##### Informing Results of Point Accumultation Request
Using `OnPointListener` interface, you can inform user that the point accumulation request has been successful(`onSuccess`) or not(`onFail`). Implement the following interface, and pass it as the parameter in `setOnPointListener(OnPointListener listener)` method.

> setOnPointListener(OnPointListener pointListener) is defined in `BuzzScreen` class. So you have to use it as `BuzzScreen.getInstance().setOnPointListener(...)`. Therefore this method must be called after `BuzzScreen.init()`.

```Java
interface OnPointListener {
    void onSuccess(PointType type, int points);
    void onFail(PointType type);
}
```
- PointType : Depending on the user's action which results in the point accumulation event, the `PointType` is either UNLOCK(swiping right) or LANDING(swiping left).

- **Note**
	- The informed points are only for **completing instant reward campaigns**. Points that are earned after a course of actions such as app execution, sign up, and event participation(aka CPA types) will **not** be informed by this method.
	- If you want to handle the CPA types as well, you can use `onClick(Campaign campaign)` method in `OnTrackingListener`.(Please refer to [Tracking Impression and Click Events](#tracking-impression-and-click-events))

	> Call `campaign.getActionPoints()` to get action type's points(action_points) in `onClick` method's parameter, `campaign`, and check if it is bigger than 0, which means the campaign is CPA type. If so, you can inform users of instructions like - 'if you finish participating this campaign, you can get {action_points} points'.

Example
```Java
BuzzScreen.getInstance().setOnPointListener(new OnPointListener() {

    @Override
    public void onSuccess(PointType type, int points) {
        // Point accumulation request success message
        Toast.makeText(getApplicationContext(), points + " p request succeeded!", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onFail(PointType type) {
        // Point accumulation request fail message
        Toast.makeText(getApplicationContext(), "Fail to process points accumulation request due to the network issue..", Toast.LENGTH_LONG).show();
    }

});
```

## Process Separation
Sample files are located in **sample/multiProcess**.
> Please refer to the sample before applying to your app.

The BuzzScreen SDK is continuously running Android's service component in the foreground. Thus, in case the BuzzScreen service is running in the same process with your app, your app might experience insufficient memories in some cases. To prevent this, you can separate the Buzzscreen process from the main process.


### How To Apply
Add `compile 'com.buzzvil:buzzscreen-multi-process:1.+'` to `build.gradle`, instead of `compile 'com.buzzvil:buzzscreen:1.+'`.

```
dependencies {
    // compile 'com.buzzvil:buzzscreen:1.+'
    compile 'com.buzzvil:buzzscreen-multi-process:1.+'
}
```

#### Additional Work if Customized Lock Screen is Being Used
As customized lock screen activity should also work in the separated process, it is required to add `android:process=":locker"` to `AndroidManifest.xml` like the followings:

For example, if `CustomLockerActivity` is the customized lock screen activity you are using:
```Xml
<manifest>
    <application>
        ...
        <activity
            android:name=".CustomLockerActivity"
            android:excludeFromRecents="true"
            android:launchMode="singleTask"
            android:process=":locker"
            android:screenOrientation="portrait"
            android:taskAffinity="${applicationId}.Locker" />
    </application>
</manifest>
```

> **Notes** : Now that lock screen runs in a separated process from the original app, please be mindful when you provide features, which are **originally from the main app**, on the lock screen area.
