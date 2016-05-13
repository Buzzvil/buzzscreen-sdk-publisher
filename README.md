# BuzzScreen SDK for Android
- 버즈스크린을 안드로이드 어플리케이션에 연동하기 위한 라이브러리
- 안드로이드 버전 지원 : Android 2.3(API Level 9) 이상
- SDK 연동 및 샘플 어플리케이션 실행을 위해서는 app_key(버즈스크린 어드민에서 확인 가능) 필요

## 폴더 및 파일 설명
- **aars/** : 안드로이드 스튜디오에서 사용가능한 안드로이드 라이브러리들이 들어있다.
    - **buzzscreen-sdk-core.aar** : 버즈스크린의 필수적인 요소들만으로 구성된 SDK이다. 기본적으로 제공하는 잠금화면이 아닌 직접 커스터마이징(참고:연동 가이드 - 고급)하는 경우 사용한다.
    - **buzzscreen-sdk-full.aar** : buzzscreen-sdk-core에 기본 잠금화면(SimpleLockerActivity)을 포함한 SDK이다. SimpleLockerActivity는 가장 간단한 형태의 잠금화면으로 잠금화면 커스터마이징이 필요하지 않은 경우 이 SDK를 사용한다.
- **eclipse/** : 이클립스에서 사용가능한 안드로이드 라이브러리들이 들어있다.
    - **buzzscreen-sdk-core-eclipse.zip** : buzzscreen-sdk-core.aar 과 역할이 동일한 이클립스용 라이브러리이다.
    - **buzzscreen-sdk-full-eclipse.zip** : buzzscreen-sdk-full.aar 과 역할이 동일한 이클립스용 라이브러리이다.
- **sample/** : 버즈스크린 연동 샘플 모듈이 들어있다. basic, custom, multiProcess 라는 이름의 productFlavors 를 이용하여 모든 연동 버젼에 대한 샘플 앱을 각각 빌드할 수 있다.
    - **basic** : 가장 간단한 형태의 연동 샘플이다. buzzscreen-sdk-full을 사용하여 최소한의 코드로 버즈스크린을 연동하는 것을 보여준다.
    - **custom** : 잠금화면을 커스터마이징 하는 샘플이다. buzzscreen-sdk-core를 연동하여 커스터마이징을 어떻게 하는지 알 수 있다. buzzscreen-sdk-full에 포함되어있는 SimpleLockerActivity와 이 샘플에서 제공하는 CustomLockerActivity와의 비교를 통해 쉽게 이해할 수 있도록 구성했다.
    - **multiProcess** : 메모리 사용의 효율성을 위해 잠금화면 프로세스를 분리하는 샘플이다. 연동가이드 고급에서 다루는 프로세스 분리를 참고한다.

## 버즈스크린 SDK 연동 가이드 - 기본
가장 기본적인 연동 방법으로, 이 연동만으로도 버즈스크린을 안드로이드 어플리케이션에 탑재할 수 있다.

참고 샘플 : **sample/basic**

### 1. 라이브러리 추가
개발 환경에 따라 설정 방법이 달라진다.

#### Android studio
- [버즈스크린 SDK 릴리즈 목록](https://github.com/Buzzvil/buzzscreen-sdk-publisher/releases) 중 최신 릴리즈 버젼의 `buzzscreen-sdk-full.aar` 을 개발중인 안드로이드 어플리케이션 내에 포함한다.
    1. 다운받은 aar 파일을 개발중인 모듈의 libs/ 폴더에 넣는다.
    2. build.gradle에 다음의 내용을 추가한다.
    ```
    dependencies {
        compile(name:'buzzscreen-sdk-full', ext:'aar')
    }
    repositories{
        flatDir{
            dirs 'libs'
        }
    }
    ```

- **구글 플레이 서비스 라이브러리**와 **universal image loader 라이브러리**를 추가한다. 모듈 내 build.gradle에 다음과 같이 dependencies 를 추가하면 된다.

```
dependencies {
    compile 'com.google.android.gms:play-services-ads:7.5.0'
    compile 'com.nostra13.universalimageloader:universal-image-loader:1.9.4'
}
```
##### Facebook Audience Network library 
[AudienceNetwork.jar](libs/AudienceNetwork.jar) 를 다운받아 라이브러리로 추가해준다. 이 라이브러리는 [Facebook Audience Network](https://developers.facebook.com/docs/audience-network) 사용을 위한 것으로 이미 사용하던 경우에는 추가하지 않아도 된다.

#### Eclipse
- [버즈스크린 SDK 릴리즈 목록](https://github.com/Buzzvil/buzzscreen-sdk-publisher/releases) 중 최신 릴리즈 버젼의 `buzzscreen-sdk-full-eclipse.zip` 의 압축을 풀어 개발중인 안드로이드 어플리케이션 내에 라이브러리로 포함한다.
    1. File -> import -> Android -> Existing Android code into Workspace를 선택하여 다운받은 디렉토리를 root directory로 갖는 새로운 프로젝트를 생성한다.
    2. 1번을 통해 생성된 프로젝트의 속성 중 'is Library' 를 체크해 라이브러리로 등록한다.
    3. 개발중인 어플리케이션 프로젝트 속성에서 위에서 등록한 라이브러리 프로젝트에 대한 dependency를 추가한다.

- **구글 플레이 서비스 라이브러리** 설정 필요. [구글 플레이 서비스 라이브러리 설정 방법](https://developers.google.com/android/guides/setup)을 참고하여 직접 추가하면 된다.
- **universal image loader 라이브러리**를 추가한다. [universal image loader README](https://github.com/nostra13/Android-Universal-Image-Loader)를 참고하여 직접 추가하면 된다.

- [AudienceNetwork.jar](libs/AudienceNetwork.jar) 를 다운받아 라이브러리로 추가해준다. 이 라이브러리는 [Facebook Audience Network](https://developers.facebook.com/docs/audience-network) 사용을 위한 것으로 이미 사용하던 경우에는 추가하지 않아도 된다.

### 2. 설정
- Android Manifest에 아래와 같이 권한, 액티비티, 서비스, 리시버들을 추가한다.

```Xml
<manifest>
    ...
    <!-- Permissions for BuzzScreen -->
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />

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

- Proguard 설정(선택사항) : Proguard 사용시에 다음 라인들을 Proguard 설정에 추가한다.

```
-keep class com.buzzvil.buzzscreen.sdk.** {*;}
-keep interface com.buzzvil.buzzscreen.sdk.** {*;}

-keep class com.google.android.gms.common.GooglePlayServicesUtil {*;}
-keep class com.google.android.gms.ads.identifier.AdvertisingIdClient {*;}
-keep class com.google.android.gms.ads.identifier.AdvertisingIdClient$Info {*;}
```

### 3. 메소드 호출
버즈스크린을 연동하기 위해서는 전체적으로
    **1) 초기화 -> 2) 유저 정보 설정 -> 3) 잠금화면 제어 설정**
의 세 단계를 따라야 한다.

#### 1) 초기화
- `BuzzScreen.init()` : Application Class의 onCreate에 추가한다. 이로써 모든 다른 메소드보다 항상 먼저 호출되도록 할 수 있다. 파라미터는 다음과 같다.
    - String appKey : SDK 사용을 위한 앱키로, 어드민에서 확인 가능하다.
    - Context context : Application context 를 `this` 로 입력한다.
    - Class<?> lockerActivityClass : 잠금화면 액티비티 클래스. 잠금화면 커스터마이징을 하지 않는 경우 SDK내에서 제공하는 `SimpleLockerActivity.class` 를 설정한다. 커스터마이징을 하는 경우 직접 구현한 잠금화면 액티비티 클래스를 설정한다. 자세한 사항은 [버즈스크린 SDK 연동 가이드-고급](ADVANCED-USAGE.md) 내 '잠금화면 커스터마이징' 설명을 참조한다.
    - int imageResourceIdOnFail : 네트워크 에러 혹은 일시적으로 잠금화면에 보여줄 캠페인이 없을 경우 보여주게 되는 이미지를 앱 내 리소스에 포함시켜야 한다. 이 이미지의 리소스 아이디를 설정한다.
    - boolean useMultiProcess : 잠금화면 서비스를 분리된 프로세스에서 실행하는 경우 true, 사용하지 않으면 false 로 설정한다. 자세한 사항은 [버즈스크린 SDK 연동 가이드-고급](ADVANCED-USAGE.md) 내 '프로세스 분리' 설명을 참조한다.
    
     > **주의** : 기존에 사용하던 Application Class가 없이 버즈스크린 연동을 위해 처음으로 Application Class를 생성할 경우 반드시 AndroidManifest.xml 에 해당 Application Class를 등록해야 한다.

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

- `BuzzScreen.getInstance().launch()` : 앱 실행시 처음 실행되는 액티비티에 추가해 준다.

#### 2) 유저 정보 설정
- `UserProfile` : 유저 정보를 설정할 수 있는 클래스이다. 유저에게 적립금을 지급하기 위해서는 필수적으로 userId 를 설정해야 한다. userId는 매체사에서 유저를 구분할 수 있는 아이디로 적립금 지급 요청(SDK가 아닌 서버연동)시에 전달된다. 추가적으로 나이, 성별, 지역정보 등을 설정하여 해당 유저에게 맞는 캠페인 타게팅이 가능하게 할 수 있다.
- `BuzzScreen.getInstance().getUserProfile()` 을 통해 UserProfile 객체를 얻을 수 있다.

##### UserProfile 클래스 내의 메소드 목록
- `setUserId(String userId)` : 유저 아이디 설정(**필수 항목**)
- `setBirthYear(int birthYear)` : 유저의 출생 년도를 4자리의 숫자로 입력하여 나이를 설정한다.
- `setGender(String gender)` : 성별을 설정한다. 다음과 같은 미리 정의된 String을 통해 형식에 맞춰 성별을 적용해야 한다.
    - `UserProfile.USER_GENDER_MALE` : 남성인 경우
    - `UserProfile.USER_GENDER_FEMALE` : 여성인 경우
- `setRegion(String region)` : [지역 형식](https://github.com/Buzzvil/buzzscreen-sdk-publisher/blob/master/REGION-FORMAT.md)에 맞게 지역을 설정한다.

> BuzzScreen.getInstance().activate() 호출 전에 반드시 userId 설정이 필요하며, 이후에 userId를 포함한 UserProfile 정보는 수시로 변경가능하다.

#### 3) 잠금화면 제어 설정
- BuzzScreen.getInstance().activate() : 버즈스크린을 활성화한다. 이 함수가 호출된 이후부터 잠금화면에 버즈스크린이 나타난다.
- BuzzScreen.getInstance().deactivate() : 버즈스크린을 비활성화한다. 이 함수가 호출되면 더이상 잠금화면에서 버즈스크린이 나타나지 않는다.
    
> **주의** : 유저가 로그아웃할 경우 잠금화면에서 버즈스크린이 더이상 나타나지 않게 하기 위해 로그아웃 로직에 반드시 명시적으로 `BuzzScreen.getInstance().deactivate()` 를 호출해야 한다.

### 4. 포인트 적립 요청(포스트백)  - 서버 연동
- 버즈스크린은 포인트 적립이 발생했을 때 직접 유저들에게 포인트를 지급하는 것이 아니다. 버즈스크린 서버에서 매체사 서버로 포인트 적립 요청을 보낼 뿐이고, 실제 지급은 매체사 서버에서 처리한다.
- 포인트 적립 요청에 대한 처리 방법은 [포인트 적립 요청 연동 가이드](POSTBACK.md)를 참고한다.

> 포인트 적립 알림 푸쉬를 유저에게 보내고 싶은 경우에는 포인트 적립 요청을 받고 매체사에서 직접 푸쉬를 전송한다.

#### 포인트 적립 요청 흐름
![Task Flow](postback_flow.jpg)

### 5. 추가 기능
다음과 같은 기능이 필요할 때는 ["버즈스크린 SDK 연동 가이드-고급"](ADVANCED-USAGE.md)을 참고한다.
- 잠금화면 커스터마이징 : 시계 및 하단 슬라이더 UI 변경, 위젯 추가
- 프로세스 분리 : 메모리의 효율적 사용을 위해 프로세스 분리 지원

유저에게 할당되는 광고의 타게팅 정보를 커스터마이징 하려고 할 때는 ["버즈스크린 커스텀 타게팅"](CUSTOM_TARGETING.md)을 참고한다.
