# BuzzScreen Postback API Guide
BuzzScreen SDK를 통해 유저가 포인트를 지급받은 경우 매체사에게 이 사실을 알리기 위한 API.
### 1. 요청 방향
BuzzScreen -> 매체사
 
### 2. HTTP Request method
POST
 
### 3. HTTP Request URL
매체사에서 정의
 
### 4. HTTP Request Parameters
| 필드           | 타입    | 설명                                                                                                                                                                                                                        |
|----------------|---------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| transaction_id | string  | 포인트 중복 적립을 막기위해서 사용한다. 같은 transaction_id로 요청이 온 적이 있는 경우에는 중복으로 지급하지 않도록 처리해준다.                                                                                             |
| user_id        | string  | 매체사에서 정의한 user_id                                                                                                                                                                                                   |
| point          | integer | 유저에게 지급할 포인트                                                                                                                                                                                                      |
| base_point     | integer | 유저에게 지급할 포인트 중 시간당 기본 포인트. 해당 시간안에 포인트를 적립 받은 적이 있으면 0, 아니면 시간당 기본적립금 값이 된다.                                                                                           |
| campaign_id    | long    | 포인트가 지급된 캠페인 id                                                                                                                                                                                                   |
| campaign_name  | string  | 포인트가 지급된 캠페인 이름                                                                                                                                                                                                 |
| event_at       | long    | 포인트 지급 시점. timestamp값이다. 대부분 API 호출시점과 동일하지만 API 호출이 재시도인 경우 다를 수 있다.                                                                                                                  |
| is_media       | integer | <ul><li>0: 버즈빌측 캠페인</li><li>1: 미디어측 캠페인</li></ul>                                                                                                                                                                                 |
| click_type     | string  | 랜딩/잠금해제 구분자 <ul><li>u: 잠금해제</li><li>l: 랜딩 </li></ul>                                                                                                                                                                                   |
| extra          | string  | 매체사별 자체 정의한 캠페인 데이터의 json serilaize된 스트링. 라이브중에 캠페인 extra 정보가 바뀐 경우 실제 포인트 적립 api에서 바뀐 정보가 적용되는데에 최대 10분이 걸릴 수 있다. eg) {"sub_type": "A", "source":"external"} |
| data           | string  | (Optional) HTTP request parameter를 암호화 해서 전송하는 경우 이 파라미터를 사용한다. 자세한 내용은 6. 참조                                                                                                                 |                                                                                                         |
 
### 6. HTTP Request Parameter Encryption
HTTP Request parameter를 암호화 하고 싶은 경우 사용하는 파라미터. 아래와 같은 순서로 파라미터를 암호화 한다.
```
JSON serialized parameters with UTF-8 encoding  -> AES(CBC mode, PCKS7 padding) encryption -> base64 encoding
```

암호화된 데이터는 HTTP POST request 파라미터에 data 라는 이름으로 추가하여 전송한다.
수신측에서는 HTTP POST request에서 data 파라미터를 가져와 아래와 같은 순서로 복호화 한다.
```
base64 decoding -> AES decoding -> JSON load
```
 
암호화에 사용할 AES key, IV 값은 따로 요청해야 한다.
 
- 예제 평문
```
{"user_id": "testuserid76301", "click_type": "u", "extra": "{}", "is_media": 0, "point": 2, "campaign_id": 3467, "event_at": 1442984268, "campaign_name": "test campaign", "base_point": 2, "transaction_id": 429482977}
```
- 예제 암호문
```
Vblq5XX2g/M2fGs5GRbrLQGh6mwGXDI/frRb2Zn2syY0VAzG6ftcvDzaxSLLvgzYMmvhLTDKZATDX2F9U4AENfBZYQ/Ov+Y9QPfW9A39kaQi/XS3kea09+aI1pO0NkHqP8My8TuR//xhVYtoWovSIw42jbTzUhgJ8SePTC5ZwrLg7bOS7cy3gEgcHL9XzUOrxL8RqMo8fieSMv9hr2YkJJmNL2t0akyj/Hz/lXUvOqhrb9mmFuSlWLF/kS8af3fRKgjxNjGGIVDoVotPipFSbHbpExSp6wY0wsmjfcXGw6g=
예제에서는 AES key, IV 값으로 모두 '12341234asdfasdf'를 사용하였다.
```
