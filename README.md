# BuzzScreen SDK for Android
- 버즈스크린을 안드로이드 어플리케이션에 연동하기 위한 라이브러리
- 안드로이드 버전 지원 : Android 2.3(API Level 9) 이상
- SDK 연동 및 샘플 어플리케이션 실행을 위해서는 app_key(버즈스크린 어드민에서 확인 가능) 필요
- 구글 플레이 서비스 라이브러리 설정 필요. [구글 플레이 서비스 라이브러리 설정 방법](https://developers.google.com/android/guides/setup)을 참고하여 직접 추가하면 된다.

    > 안드로이드 스튜디오와 이클립스 설정이 다릅니다. 안드로이드 스튜디오인 경우는 **build.gradle > dependencies**에 **compile 'com.google.android.gms:play-services-ads:7.5.0'**만 추가하면 됩니다. 다운받은 소스에서 샘플을 참고하세요.

## 폴더 및 파일 설명
- **aars/** : 안드로이드 스튜디오에서 사용가능한 안드로이드 라이브러리들이 들어있다.
- **aars/buzzscreen-sdk-core.aar** : 버즈스크린의 필수적인 요소들만으로 구성된 SDK이다. 기본적으로 제공하는 잠금화면이 아닌 직접 커스터마이징(참고:연동 가이드 - 고급)하는 경우 사용한다.(이클립스인 경우는 **eclipse-projects/buzzscreen-sdk-core**)
- **aars/buzzscreen-sdk-full.aar** : buzzscreen-sdk-core에 기본 잠금화면(SimpleLockerActivity)을 포함한 SDK이다. SimpleLockerActivity는 가장 간단한 형태의 잠금화면으로 잠금화면 커스터마이징이 필요하지 않은 경우 이 SDK를 사용한다.(이클립스인 경우는 **eclipse-projects/buzzscreen-sdk-full**)
- **sample-basic** : 가장 간단한 형태의 연동 샘플이다. buzzscreen-sdk-full을 사용하여 최소한의 코드로 버즈스크린을 연동하는 것을 보여준다.
- **sample-custom** : 잠금화면을 커스터마이징 하는 샘플이다. buzzscreen-sdk-core를 연동하여 커스터마이징을 어떻게 하는지 알 수 있다. buzzscreen-sdk-full에 포함되어있는 SimpleLockerActivity와 이 샘플에서 제공하는 CustomLockerActivity와의 비교를 통해 쉽게 이해할 수 있도록 구성했다.
- **sample-multi-process** : 메모리 사용의 효율성을 위해 잠금화면 프로세스를 분리하는 샘플이다. 연동가이드 고급에서 다루는 프로세스 분리를 참고한다.

## 버즈스크린 SDK 연동 가이드 - 기본
가장 기본적인 연동 방법으로, 이 연동만으로도 버즈스크린을 안드로이드 어플리케이션에 탑재할 수 있다.

참고 샘플 : **sample-basic**

### 1. 설정
[SDK 다운로드](https://github.com/Buzzvil/buzzscreen-sdk-publisher/archive/master.zip) 후 압축 해제하고, 압축 해제한 폴더 내의 aars/buzzscreen-sdk-full을 개발중인 안드로이드 어플리케이션 내에 포함(이클립스인 경우는 eclipse-projects/buzzscreen-sdk-full)

> 안드로이드 스튜디오인 경우 build.gradle에 다음 두 라이브러리를 추가합니다. (이클립스인 경우는 SDK 프로젝트내의 libs 에 이미 포함되어 있음)
    ```
    dependencies {
        compile 'com.loopj.android:android-async-http:1.4.8'
        compile 'com.nostra13.universalimageloader:universal-image-loader:1.9.4'
    }
    ```

Android Manifest에 아래와 같이 권한, 액티비티, 서비스, 리시버들을 추가합니다.

```Xml
<manifest>
    ...
    <!-- Permissions for BuzzScreen -->
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

초기화 함수(BuzzScreen.init)를 Application Class의 onCreate에 추가합니다.

```Java
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ...
        // app_key : SDK 사용을 위한 앱키로, 어드민에서 확인 가능
        // SimpleLockerActivity.class : 잠금화면 액티비티 클래스
        // R.drawable.image_on_fail : 네트워크 에러 혹은 일시적으로 잠금화면에 보여줄 캠페인이 없을 경우 보여주게 되는 이미지.
        // useMultiProcess : 잠금화면 서비스를 분리된 프로세스에서 실행하는 경우 true, 사용하지 않으면 false
        BuzzScreen.init("app_key", this, SimpleLockerActivity.class, R.drawable.image_on_fail, false);
    }
}
```

##### Proguard 설정
Proguard 사용시에 다음 라인들을 Proguard 설정에 추가합니다.

```
-keep class com.buzzvil.buzzscreen.sdk.** { *; }
-keep interface com.buzzvil.buzzscreen.sdk.** { *; }
```

### 2. 잠금화면 제어
- BuzzScreen.getInstance().launch() : 앱 실행시 처음 실행되는 액티비티에 추가해 준다.
- BuzzScreen.getInstance().activate() : 버즈스크린을 활성화한다. 이 함수가 호출된 이후부터 잠금화면에 버즈스크린이 나타난다.
- BuzzScreen.getInstance().deactivate() : 버즈스크린을 비활성화한다. 이 함수가 호출되면 더이상 잠금화면에서 버즈스크린이 나타나지 않는다.
- UserProfile : 유저 정보를 설정한다. 유저에게 적립금을 지급하기 위해서는 반드시 UserProfile.setUserId(String userId)를 호출해야 한다. userId는 매체사에서 유저를 구분할 수 있는 아이디로 적립금 지급 요청(SDK가 아닌 서버연동)시에 전달된다. 그 외의 setBirthYear,  setGender, setRegion(참고:[지역 형식](https://github.com/Buzzvil/buzzscreen-sdk-publisher/blob/master/REGION-FORMAT.md)) 호출을 통해 해당 유저에게 맞는 캠페인 타게팅이 가능하다.

> BuzzScreen.getInstance().activate() 호출 전에 반드시 userId 설정이 필요하며, 이후에 userId를 포함한 UserProfile 정보는 수시로 변경가능하다.

### 3. 포인트 적립 요청(포스트백)  - 서버 연동
- 버즈스크린은 포인트 적립이 발생했을 때 직접 유저들에게 포인트를 지급하는 것이 아니다. 버즈스크린 서버에서 매체사 서버로 포인트 적립 요청을 보낼 뿐이고, 실제 지급은 매체사 서버에서 처리한다.
- 포인트 적립 요청에 대한 처리 방법은 [포인트 적립 요청 연동 가이드](https://buzzvilian.atlassian.net/wiki/display/PUB/BuzzScreen+API+Guide)를 참고한다.

> 포인트 적립 알림 푸쉬를 유저에게 보내고 싶은 경우에는 포인트 적립 요청을 받고 매체사에서 직접 푸쉬를 전송한다.

####포인트 적립 요청 흐름
![Task Flow](https://github.com/Buzzvil/buzzscreen-sdk-publisher/blob/master/postback_flow.jpg)

### 4. 추가 기능
다음과 같은 기능이 필요할 때는 ["버즈스크린 SDK 연동 가이드-고급"](https://github.com/Buzzvil/buzzscreen-sdk-publisher/blob/master/ADVANCED-USAGE.md)을 참고한다.
- 잠금화면 커스터마이징 : 시계 및 하단 슬라이더 UI 변경, 위젯 추가
- 프로세스 분리 : 메모리의 효율적 사용을 위해 프로세스 분리 지원
- 포인트 적립 요청 트래픽 분산 : 매시 정각에 집중되는 포인트 적립 요청 트래픽 분산
