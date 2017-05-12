# BuzzScreen SDK for Android
* 버즈스크린을 안드로이드 어플리케이션에 연동하기 위한 라이브러리
* 안드로이드 버전 지원 : Android 4.0.3(API Level 15) 이상
* 연동을 하기 위해 발급받아야 하는 키 값들
    * `app_key` : 버즈빌 파트너십 매니저에에게 발급받은 어드민에서 확인 가능([`BuzzScreen.init` 메소드에서 필요](#1-초기화--init-호출-및-launch-호출로-이루어진다))
    * `app_license` : 버즈빌 파트너십 매니저에게 문의([`AndroidManifest.xml` 설정에서 필요](#androidmanifestxml-에-다음-코드를-추가합니다))
    * `plist` : 버즈빌 파트너십 매니저에게 문의([`AndroidManifest.xml` 설정에서 필요](#androidmanifestxml-에-다음-코드를-추가합니다))

* 모든 연동작업 완료 후 연동한 앱 APK 파일을 버즈빌 파트너십 매니저에게 전달 후 승인 과정을 거쳐 라이브하게 됩니다.
> 1.4.0 이전 버전의 SDK를 연동한 퍼블리셔들은 [마이그레이션 가이드](docs/MIGRATION-TO-1.4.0.md)를 참고 바랍니다.

## 버즈스크린 SDK 연동 가이드 - 기본
가장 기본적인 연동 방법으로, 이 연동만으로도 버즈스크린을 안드로이드 어플리케이션에 탑재할 수 있다.

참고 샘플 : **sample/basic**
> 반드시 샘플을 확인하고 실제 자신의 코드에 적용바랍니다.

### 1. 설정

#### 모듈 내의 `build.gradle` 에 다음 코드를 추가합니다.

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
> `com.google.android.gms:play-services-ads:8.4.0` 와 `com.google.android.gms:play-services-location:8.4.0` 에서 사용하는 버전 8.4.0은 앱에서 사용하는 구글 플레이 서비스 버전과 동일하도록 수정바랍니다. 그렇지 않을 경우 컴파일시에 `com.android.dex.DexException` 등의 에러를 만날 수 있습니다.


#### `AndroidManifest.xml` 에 다음 코드를 추가합니다.
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
> `<app_license>` 및 `<plist>` 는 파트너십 매니저에게 문의 바랍니다.

### 2. 메소드 호출
버즈스크린을 안드로이드 앱에 연동하기 위해서는
    **1) 초기화 -> 2) 유저 정보 설정 -> 3) 잠금화면 제어 설정**
의 세 단계를 따라야 한다.

#### 1) 초기화 : init() 호출 및 launch() 호출로 이루어진다.
- `BuzzScreen.init()` : Application Class의 onCreate에 추가한다. 이로써 모든 다른 메소드보다 항상 먼저 호출되도록 할 수 있다. 파라미터는 다음과 같다.
   - String appKey : SDK 사용을 위한 앱키로, 버즈스크린 어드민에서 확인 가능하다.
    - Context context : Application context 를 `this` 로 입력한다.
    - Class<?> lockerActivityClass : 잠금화면 액티비티 클래스. 잠금화면 커스터마이징을 하지 않는 경우 SDK내에서 제공하는 `SimpleLockerActivity.class` 를 설정한다. 커스터마이징을 하는 경우 직접 구현한 잠금화면 액티비티 클래스를 설정한다. 자세한 사항은 [버즈스크린 SDK 연동 가이드-고급](docs/ADVANCED-USAGE.md) 내 '잠금화면 커스터마이징' 설명을 참조한다.
    - int imageResourceIdOnFail : 네트워크 에러 혹은 일시적으로 잠금화면에 보여줄 캠페인이 없을 경우 보여주게 되는 이미지를 앱 내 리소스에 포함시켜야 한다. 이 이미지의 리소스 아이디를 설정한다.

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
        BuzzScreen.init("app_key", this, SimpleLockerActivity.class, R.drawable.image_on_fail);
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
- `setRegion(String region)` : [지역 형식](docs/REGION-FORMAT.md)에 맞게 지역을 설정한다.

> BuzzScreen.getInstance().activate() 호출 전에 반드시 유저 정보 설정 필요

#### 3) 잠금화면 제어
- `BuzzScreen.getInstance().activate()` : 버즈스크린을 활성화한다. 이 함수가 호출된 이후부터 잠금화면에 버즈스크린이 나타난다.

    - activate()는 처음 잠금화면 활성화시 호출하면, 그 이후에 자동으로 잠금화면 사이클이 관리되기 때문에 deactivate()을 호출하기 전에 다시 호출할 필요는 없다.

    - 버즈스크린을 활성화한 후 Notification area 에 고정된 Notification이 생성된 경우 ["잠금화면 서비스 노티피케이션"](docs/LOCKSCREEN-SERVICE-NOTIFICATION.md)을 참고한다.
    
    - 활성화한 후 실제로 잠금화면이 처음 준비가 완료된 시점을 알고 싶을 때, 아래의 interface를 구현하여 activate() 메소드의 파라미터로 넘긴다. `BuzzScreen.getInstance().activate(ActivateListener listener)`의 형태이다.
          
        ```Java
        public interface ActivateListener {
            void onReady();// This will be called when the first lockscreen is ready to be shown.
        }
        ```   

- `BuzzScreen.getInstance().deactivate()` : 버즈스크린을 비활성화한다. 이 함수가 호출되면 더이상 잠금화면에서 버즈스크린이 나타나지 않는다.
    - `BuzzScreen.getInstance().logout()` : 유저가 로그아웃할 경우 이 함수를 호출한다. 이 함수는 deactivate() 를 호출하며, 버즈스크린에서 사용하는 유저 정보를 디바이스에서 삭제한다.

### 3. 포인트 적립 요청(포스트백)  - 서버 연동
- 버즈스크린은 포인트 적립이 발생했을 때 직접 유저들에게 포인트를 지급하는 것이 아니다. 버즈스크린 서버에서 매체사 서버로 포인트 적립 요청을 보낼 뿐이고, 실제 지급은 매체사 서버에서 처리한다.
- 포인트 적립 요청에 대한 처리 방법은 [포인트 적립 요청 연동 가이드](docs/POSTBACK.md)를 참고한다.

> 포인트 적립 알림 푸쉬를 유저에게 보내고 싶은 경우에는 포인트 적립 요청을 받고 매체사에서 직접 푸쉬를 전송한다.

#### 포인트 적립 요청 흐름
![Task Flow](docs/postback_flow.jpg)

### 4. 추가 기능
- 다음과 같은 기능이 필요할 때는 ["버즈스크린 SDK 연동 가이드-고급"](docs/ADVANCED-USAGE.md)을 참고한다.
    - 잠금화면 커스터마이징 : 시계 및 하단 슬라이더 UI 변경, 위젯 추가
    - 프로세스 분리 : 메모리의 효율적 사용을 위해 프로세스 분리 지원

- 유저에게 할당되는 광고의 타게팅 정보를 커스터마이징 하려고 할 때는 ["버즈스크린 커스텀 타게팅"](docs/CUSTOM_TARGETING.md)을 참고한다.

- Lockscreen Service Notification 설정을 위해서는 ["잠금화면 서비스 노티피케이션"](docs/LOCKSCREEN-SERVICE-NOTIFICATION.md)을 참고한다.
