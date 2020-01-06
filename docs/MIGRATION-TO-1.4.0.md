# 1.4.0 이전 버전에서 1.4.0 이상으로 업그레이드시 변경 사항
연동 단순화를 위해 라이브러리 설정 방식이 수정되었습니다. 아래 내용에서 변경된 설정 방식을 확인할 수 있습니다.

## 1. 라이브러리 추가 방식 변경
기존에는 aar 파일을 직접 추가 하였으나 maven repository 를 사용하도록 변경함에 따라 다음 변경이 필요합니다.
### 1-1. 라이브러리 파일 제거(다음 3개의 파일 중 갖고 있는 파일만)
* `buzzscreen-sdk-core-<version>.aar`
* `buzzscreen-sdk-full-<version>.aar`
* `AudienceNetwork-<version>.jar`
### 1-2. `build.gradle` 내의 `dependencies` 설정 제거
버즈스크린 SDK 연동때문에 추가한 다음 두 줄을 제거합니다.
```
dependencies {
    compile 'com.google.android.gms:play-services-ads:9.4.0'
    compile 'com.nostra13.universalimageloader:universal-image-loader:1.9.4'
}
```
### 1-3. `build.gradle` 에 다음 코드 추가
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

> 프로세스 분리를 사용하고 있는 퍼블리셔는 `compile 'com.buzzvil:buzzscreen:1.+'` 대신 `compile 'com.buzzvil:buzzscreen-multi-process:1.+'` 를 추가합니다.

## 2. Manifest 파일 변경
수동으로 추가했던 컴포넌트는 이제 SDK를 통해 자동으로 관리되기 때문에 더 이상 필요없고, 두 개의 설정값이 추가되었습니다.
### 2-1. 컴포넌트 제거
기존에 버즈스크린 때문에 수동으로 추가했던 다음 컴포넌트들을 제거합니다.
* `SimpleLockerActivity`
* `LandingHelperActivity`
* `LandingOverlayActivity`
* `LockerService`
* `BootReceiver`
* `UpdateReceiver`
> 단, 커스터마이징한 액티비티를 사용하는 경우 해당 액티비티 컴포넌트는 남겨둡니다. 그래도 속성 정보가 제대로 입력되었는지 한번더 확인 바랍니다.
ex) `BaseLockerActivity`를 상속받은 클래스 이름이 `CustomLockerActivity` 인 경우
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
> 프로세스를 분리해서 사용하는 경우 위 액티비티에 `android:process=":locker"` 속성이 필요합니다.

## 3. [새 연동가이드](../README.md) 읽고 문제가 없는지 확인 바랍니다.

마이그레이션 작업이 문제있거나 궁금한점이 있으면 언제든지 문의주시기 바랍니다. 빠르게 대응하도록 하겠습니다. 감사합니다.





