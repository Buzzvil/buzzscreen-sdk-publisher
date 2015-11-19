# 외부 캠페인(External Campaign)
버즈스크린은 기본적으로 버즈스크린 서버에서 할당한 캠페인에 대해서만 동작하지만, 여기서 설명하는 **외부 캠페인** 기능을 사용하면 버즈스크린 서버에서 할당한 캠페인이 아닌 **외부 캠페인**을 버즈스크린에서 보여줄 수 있다.

#### 특성
- 하나의 외부 캠페인만 존재 : 현재 추가한 외부 캠페인이 있다면, 외부 캠페인 추가를 수행하여도 새롭게 추가되는 것이 아니고 기존 외부 캠페인이 대체된다.
- 기본 포인트 지급에서 제외 : 외부 캠페인은 버즈스크린 서버에 존재하는 캠페인이 아니기 때문에 기본 포인트 지급에서 제외된다.
- 임프레션 제한(하루당) : 한 디바이스에서 외부 캠페인을 통해 발생시킬 수 있는 임프레션 수는 제한되어 있다.
- 높은 우선순위 : 다른 캠페인들보다 우선순위가 높기때문에 추가 즉시 잠금화면에서 바로 볼 수 있다.


## 외부 캠페인 연동
외부 캠페인 추가, 제거 및 트래킹(클릭, 임프레션) 연동 작업

### 생성
`Campaign.createExternal(String externalId, String campaignName, String imageURL, String linkURL, int landingPoints, int duration, int impressionCap)`

- externalId : 외부 캠페인 ID. `Campaign#getExternalId`를 통해 값을 얻을 수 있다.
- campaignName : 외부 캠페인 명칭.
- imageURL : 720*1230 크기의 이미지 링크. 이미지 용량이 큰 경우 데이터 사용량이 높아지기 때문에 100kb 이하의 이미지 링크 권장.
- linkURL : 랜딩 페이지 링크 URL.
- landingPoints : 랜딩시 추가 포인트 획득을 알려주는 값. 버즈스크린에서 포인트 적립처리가 이루어지지 않고 해당 값을 랜딩 포인트로 보여주기만 한다.
- duration : 외부 캠페인이 보여질 수 있는 시간(초단위). 300 인경우 300초 후에 해당 캠페인은 보여지지 않을 수 있는 상태가 된다.
    > 보여지지 않을 수 있는 상태 : 잠금화면에서 캠페인들이 업데이트되면 사라진다.
- impressionCap : 임프레션 제한. 이 값만큼 임프레션이 발생하면 자동으로 제거된다.
> 캠페인 정보에 대한 validation 체크가 따로 구현되어있지 않기때문에 위 파라미터들을 넣을 때 주의해야 한다.

### 추가
`BuzzScreen.getInstance().addExternalCampaign(Campaign campaign)`

- campaign : `Campaign.createExternal` 를 통해 생성한 캠페인 

#### 생성 및 추가 사용 예제

```Java
    String externalId = "test_id_1234";
    String campaignName = "test name";
    String imageURL = "https://d3aulf22blzf9p.cloudfront.net/android/1447239369_e0251254410d85fc.jpg";
    String linkURL = "http://www.naver.com";
    int landingPoints = 3;
    int duration = 300;
    int impressionCap = 2;
    Campaign campaign = Campaign.createExternal(externalId, campaignName, image, link, landingPoints, duration, impCap);
    BuzzScreen.getInstance().addExternalCampaign(campaign);
```

### 제거
`BuzzScreen.getInstance().removeExternalCampaign()`를 통해 현재 추가되어있는 외부 캠페인이 제거 된다.
> 만약 특정 시점후에 정확하게 외부 캠페인이 보여지지 않기를 원한다면 duration 보다는 이 함수를 호출해야 한다.

### 트래킹
OnTrackingListener([고급연동가이드](ADVANCED-USAGE.md) 트래킹 참고)를 이용하여 캠페인이 외부 캠페인인지(`Campaign#isExternal`) 확인하고,
외부 캠페인 아이디(`Campaign#getExternalId`)를 통해 어떤 캠페인에서 클릭 또는 임프레션이 발생했는지 알 수 있다.