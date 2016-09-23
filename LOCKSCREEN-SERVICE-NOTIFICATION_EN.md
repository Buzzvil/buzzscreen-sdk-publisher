# Lockscreen Service Notification
Buzzscreen uses [Foregound Service](http://developer.android.com/guide/components/services.html#Foreground) and in principle, foreground service should show ongoing notification. Because this notification is considered to be annoying for user, Buzzscreen SDK uses some tricks to hide it. But in some cases the tricks won't work. So we provide a support class to change notification configurations.

> Cases that the Notification is shown : 23 or higher compile sdk version, rare exceptions.

## Lockscreen Service Notification Configuration
**LockerServiceNotificationConfig** class enables to get or set service notification configurations. Below are configuration details that you can control. You MUST use this class after calling `BuzzScreen.init()`.
- **Title** : Title of the notification(String). Default is the app's name.
- **Text** : Content of the notification(String). Default is an empty string("").
- **SmallIcon** : Small icon of the notification(int). Default is the app's icon.
- **LargeIcon** : Large icon of the notification(int). Default is none.
- **ShowAlways** : Boolean value that decides whether or not to show the notification always(boolean). You can use it to test your notification setting. Defult is false.
- **Intent** : Intent that defines the action when notification is clicked(Intent). Default is to start the Launcher Activity.

### Example
```Java
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ...
        BuzzScreen.init("app_key", this, SimpleLockerActivity.class, R.drawable.image_on_fail, false);

        // Notification Configuration
        LockerServiceNotificationConfig config = BuzzScreen.getInstance().getLockerServiceNotificationConfig();
        config.setTitle("Sample Title");
        config.setText("Sample Text");
        config.setSmallIconResourceId(R.drawable.ic_noti_small);
        config.setLargeIconResourceId(R.drawable.ic_noti_large);
        // config.setShowAlways(true);
    }
}
```
#### Sample Notification
![Notification Sample](notification_sample.png)

## Utilization of Service Notification
If your app notifies users of some events using android notification, and at the same time the ongoing notification is also shown, then multiple notifications are shown in the bar. This could be harmful for user experience. Using the method below you can notify event notification on the place where your service notification is, and go back to the original one after user checks it.

> Caution : You must set `ShowAlways` to true to use this feature. If not, the method won't work.

- `BuzzScreen.getInstance.notifyOnServiceNotification(Notification notification)` : Pass your event notification object as the parameter. This notification will be shown on the place of service notification. To go back to the original service notification(which follows LockerServiceNotificationConfig), call this method again using `null` as the parameter.