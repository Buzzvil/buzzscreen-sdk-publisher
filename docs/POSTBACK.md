# BuzzScreen Postback API Guide
> BuzzScreen SDK를 통해 유저가 포인트를 지급받은 경우 매체사에게 이 사실을 알리기 위한 API.

### 1. 요청 방향
* BuzzScreen -> 매체사
 
### 2. HTTP Request method

* **POST**
 
### 3. HTTP Request URL

* 매체사에서 정의

### 4. HTTP Request Parameters

* Optional Parameters
    * `base_point`, `click_type`은 `action_type`이 `a`인 경우(action 에 의해 발생한 포인트) 전달되지 않는다.
    * `data`는 암호화 관련 파라미터로 별도의 요청을 통해 사용할 수 있다.

* Info

| 필드 | 타입 | 설명 |
|-----|----|-----|
| transaction_id | String(64)  | 포인트 중복 적립을 막기 위한 **id**.<br>같은 **transaction_id**로 요청이 온 경우에는 반드시 포인트 중복 적립이 안되도록 처리해주어야 한다.<br>**최대 64자까지 전달 될 수 있으므로, 연동 시 확인이 필요하다.** |
| user_id | String | 매체사에서 정의한 user_id |
| campaign_id | Long | 포인트가 지급된 캠페인 id |
| campaign_name | String | 포인트가 지급된 캠페인 이름 |
| event_at | Long | 포인트 지급 시점.<br>**timestamp** 값이다.<br>대부분 API 호출시점과 동일하지만 API 호출이 재시도인 경우 다를 수 있다. |
| is_media | Integer | 0: 버즈빌측 캠페인<br>1: 매체사측 캠페인|
| extra | String | 매체사별 자체 정의한 캠페인 데이터의 json serilaize된 스트링. 라이브중에 캠페인 extra 정보가 바뀐 경우 실제 포인트 적립 api에서 바뀐 정보가 적용되는데에 최대 10분이 걸릴 수 있다. eg) {"sub_type": "A", "source":"external"} |
| action_type | String | 포인트를 지급 받기 위해 유저가 취한 액션 타입.<br>추후 새로운 타입이 추가될 수 있으므로 연동시 이를 고려해야 한다.<ul><li>u: 잠금해제</li><li>l: 랜딩 </li><li>a: 액션(액션형 광고의 경우. 해당 광고의 요구 액션을 완료했을 때)</li></ul>|
| point | Integer | 유저에게 지급할 포인트의 전체 합.<br>유저의 액션에 의해 캠페인에서 지급되는 포인트와 기본적립금인 base_point를 합친 값이다. |
| *base_point* | Integer | (Optional) 유저에게 정해진 interval 당 기본적으로 제공해주는 포인트.<br>최근에 기본 포인트를 적립받은 시점으로부터 정해진 **interval**이 지나지 않았으면 지급되지 않아야 하므로 0, 지났으면 기본 적립금 값으로 설정된다. |
| data | String  | (Optional) HTTP request parameter를 암호화 해서 전송하는 경우 이 파라미터를 사용한다. 자세한 내용은 5. 참조 |
| revenue_type | String | 광고 타입. cpi, cpe, cpa 등의 값이 올 수 있다. 컨텐츠인 경우 빈 값. |
| ~~click_type~~ (Deprecated) | String  | (Optional) ~~랜딩/잠금해제 구분자 <ul><li>u: 잠금해제</li><li>l: 랜딩 </li></ul>~~ ||

### 5. HTTP Request Parameter Encryption/Decryption

HTTP Request parameter를 암호화 하고 싶은 경우 사용하는 파라미터. BuzzScreen 에서는 아래와 같은 순서로 파라미터를 암호화한다.

1. JSON serialized parameters with UTF-8 encoding
2. AES(CBC mode, PKCS7 padding) encryption
3. base64 encoding

암호화된 데이터는 HTTP POST request 파라미터에 data 라는 이름으로 추가하여 전송한다.

수신측(매체사)에서는 HTTP POST request에서 data 파라미터를 가져와 아래와 같은 순서로 복호화 한다.

1. base64 decoding
2. AES decoding
3. JSON load
 
암호화 사용 시, 이메일(product@buzzvil.com)을 통해 암호화를 요청하면 BuzzScreen에서 매체사로 암호화에 사용할 **AES key**, **IV** 값을 지급한다.

- 예제 평문
```json
{
    "user_id": "testuserid76301", 
    "action_type": "u", 
    "extra": "{}", 
    "is_media": 0,
    "point": 2,
    "campaign_id": 3467,
    "event_at": 1442984268,
    "campaign_name": "test campaign",
    "base_point": 2,
    "transaction_id": 429482977
}
```
- 예제 암호문

    > 예제에서는 AES key, IV 값으로 모두 '12341234asdfasdf'를 사용하였다.

```
sgfHOC5Z66tLmlokmQEaXY39u+64gMWhLnxQAZ9ivYsTvF1isjVfaRx2BNhOADwPR6KB55/7F7iXBm5FKU8mHmHnlR3wSomVAlcjtx77KluoYoXi/jRCvaFLGIo7vcK1GVHxS557u/XTo53/AzdPZpk/aXkvFZvWPgS+GWj1TWle0mBJ0xOgfmb8LwMfi4rvfayTph3bZeryLuphorBzMoIhf+kQLyjfIyouWVoCh6UICeRBgzTS9SlgdUA6M1PVlCsQch0zKVeTJZEFEn8478QbpEEhgHDhXkzdo8tXgkw=
```
