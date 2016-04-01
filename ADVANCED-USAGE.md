# 버즈스크린 SDK 연동 가이드 - 고급
["버즈스크린 SDK 연동 가이드 - 기본"](https://github.com/Buzzvil/buzzscreen-sdk-publisher#버즈스크린-sdk-연동-가이드---기본)을 먼저 진행한 후에 필요에 따라 본 문서 내용을 진행한다.
- [잠금화면 커스터마이징](https://github.com/Buzzvil/buzzscreen-sdk-publisher/blob/master/ADVANCED-USAGE.md#잠금화면-커스터마이징) : 시계 및 하단 슬라이더 UI 변경, 위젯 추가
- [프로세스 분리](https://github.com/Buzzvil/buzzscreen-sdk-publisher/blob/master/ADVANCED-USAGE.md#프로세스-분리) : 메모리의 효율적 사용을 위해 프로세스 분리 지원

## 잠금화면 커스터마이징
참고 샘플 : **sample-custom**

잠금화면은 하나의 액티비티로 구성되며, 일반적인 액티비티와 마찬가지로 레이아웃을 만들어주고, 액티비티 클래스 내에서 몇가지 필수 함수들을 호출 혹은 구현해주면 된다. 잠금화면을 커스터마이징 하는 경우는 **buzzscreen-sdk-full** 대신 **buzzscreen-sdk-core**를 사용하여 연동작업을 진행하면 된다.

#### 레이아웃
레이아웃에는 필수적으로 **시계, 슬라이더**가 포함되어 있어야 하며, 캠페인 이미지에 따라 뷰들의 가독성이 떨어지지 않게 하기 위해 배경 그라데이션을 추가하는 것이 좋다. 그 외에 커스텀화 된 기능을 위한 뷰(아래 그림의 카메라 shortcut 등)를 추가할 수 있다.
>참고:[레이아웃 가이드라인](https://drive.google.com/a/buzzvil.com/file/d/0B4bLqCqPIOIaZ1ZkS0tSczIya2M/view)

![Layout](layout.jpg)

- 시계 : 레이아웃에 뷰를 추가하고, 해당 뷰에 표시되는 값은 액티비티 내의 onTimeUpdated에서 처리한다.
- 슬라이더 : 슬라이더를 구성하는 모든 이미지들을 변경할 수 있다.

    |슬라이더 속성|설명|
    |--------|--------|
    |slider:sl_left_icon|슬라이더의 왼쪽 아이콘|
    |slider:sl_right_icon|슬라이더의 오른쪽 아이콘|
    |slider:sl_pointer|슬라이더의 가운데 이미지|
    |slider:sl_pointer_drag|슬라이더의 가운데 눌렀을 때 이미지|
    |slider:sl_radius|슬라이더 중심과 좌우 아이콘 중심까지의 반지름|
    |slider:sl_text_size|슬라이더의 포인트 텍스트의 크기(Default : 14sp)|
    
    슬라이더 속성 사용 예제
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

- 배경 그라데이션 : 현재 보여지는 캠페인 이미지에 따라 시계 및 슬라이더의 가독성이 떨어질 수 있으므로 해당 UI 뒤에 배경 그라데이션을 넣어준다.
- 뷰 추가 : 일반적인 뷰와 같이, 원하는 뷰를 레이아웃에 배치하고, 기능은 액티비티 내에서 구현한다.

#### 액티비티 클래스
`BaseLockerActivity`를 상속받아서 잠금화면 액티비티를 생성하고, 초기화 함수(BuzzScreen.init)의 3번째 파라미터에 생성한 액티비티 클래스를 지정해준다. 액티비티 내에서 구현해야하는 필수 요소는 **슬라이더, 시계**이며 그외는 선택에 따라 직접 구현하면 된다.

##### 슬라이더
슬라이더는 잠금화면과 독립적인 뷰이기 때문에 잠금화면과의 연동을 위해서는 크게 두가지 작업을 해야 한다.
- 좌/우 슬라이더 선택에 따른 리스너 등록 : `Slider.setLeftOnSelectListener` , `Slider.setRightOnSelectListener` 를 통해 좌/우 선택에 따른 리스너를 등록해야 한다. 좌/우 선택 이벤트 발생시에 잠금해제(unlock) 함수를 호출하거나 링크이동(landing) 함수를 호출한다.
- 좌/우 포인트 업데이트 : 캠페인 롤링 시 각각의 캠페인에 따라 화면에 표시되는 좌/우 포인트 변경이 필요하다. 캠페인 롤링 시 캠페인이 변할 때마다 `BaseLockerActivity` 내의 함수인 `onCurrentCampaignUpdated` 가 호출되므로 이를 오버라이딩하여 이 함수의 파라미터로 전달되는 campaign 정보를 이용해 `Slider.setLeftText`와 `Slider.setRightText`를 통해 포인트 정보를 업데이트 해야 한다.

##### 시계
레이아웃에서 배치한 뷰를 시간변화에 따라 업데이트 해준다. 시간이 분 단위로 업데이트 될때마다 `BaseLockerActivity` 내의 함수인 `onTimeUpdated`가 호출되므로 이를 오버라이딩하여 이 함수의 파라미터로 전달되는 시간 정보를 이용해 time, am/pm, date등의 정보를 업데이트 해야 한다.

> 주의 - 잠금화면 액티비티에서 `onCurrentCampaignUpdated` 와 `onTimeUpdated` 를 오버라이딩하여 구현하지 않으면 오류가 발생하므로 반드시 구현해야 한다. 구체적 사용 예시는 샘플 내의 **CustomLockerActivity.java** 참고.

##### 추가적으로 제공되는 기능
- 전/후 페이지 유무 표시 : 잠금화면 터치시에 전/후 페이지 유무를 표시할 수 있다. 이를 위해서는 `setPageIndicators` 를 호출하여 표시할 뷰를 지정해준다.

Method prototype(in BaseLockerActivity)
```Java
// previous : 이전 페이지가 존재할 경우 표시할 뷰
// next : 이후 페이지가 존재할 경우 표시할 뷰
protected void setPageIndicators(View previous, View next) { ... }
```

사용 예시
```Java
setPageIndicators(
	findViewById(R.id.locker_arrow_top),
	findViewById(R.id.locker_arrow_bottom)
);
```

- 임프레션 및 클릭 이벤트 트래킹 : `setOnTrackingListener` 를 이용하여 TrackingListener를 설정해 임프레션, 클릭시 원하는 기능을 구현할 수 있다.

사용 예시
```Java
setOnTrackingListener(new OnTrackingListener() {

	@Override
    public void onImpression(Campaign campaign) {
    	//Impression 시 원하는 기능 구현
	}
	
    @Override
    public void onClick(Campaign campaign) {
    	//Click 시 원하는 기능 구현
    }
    
});
```

## 프로세스 분리
참고 샘플 : **sample-multi-process**

버즈스크린을 동작시키는 서비스는 항상 실행중인 상태를 유지하고 있다. 이 때문에 버즈스크린 서비스가 매체사 앱(버즈스크린을 연동하려는 앱)과 같은 프로세스 내에서 동작하는 경우, 프로세스 단위 메모리 관리가 같이 되고, 이 때문에 메모리 사용량이 높게 측정 된다. 이를 막기 위해서는 버즈스크린 서비스가 실행되는 프로세스를 분리해야 한다.

#### 적용 방법
- 초기화 함수 수정 : BuzzScreen.init 함수에서 useMultiProcess를 true로 설정한다.
- Android Manifest 파일에 MultipleProcessesReceiver 추가
```Xml
<receiver
    android:name="com.buzzvil.buzzscreen.sdk.MultipleProcessesReceiver"
    android:process=":locker" />
```

- Android Mainfest 파일의 기존 버즈스크린 컴포넌트에 android:process=":locker" 속성 추가
>MultipleProcessesReceiver에서 사용하는 프로세스 속성과 동일

    속성 추가해야 할 컴포넌트 : SimpleLockerActivity(기본 잠금화면을 커스터마이징 하는경우 해당 액티비티에 적용), LandingHelperActivity, LockerService, ChangeAdReceiver, DownloadAdReceiver


> **프로세스 적용 분리시 주의사항** : 잠금화면이 매체사 앱과는 다른 프로세스에서 구동되기 때문에 커스터마이징한 잠금화면에서 매체사 앱과 연관된 작업을 진행할때에는 구현에 주의를 요한다.
