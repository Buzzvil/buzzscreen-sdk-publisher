# BuzzScreen User Auth API Guide
> BuzzScreen SDK로 전달된 user_id가 실제로 매체사 앱에 가입되어 있는 user_id인지 확인하기 위한 API

### 1. 요청 방향
* BuzzScreen -> 매체사
 
### 2. HTTP Request method

* **GET**
 
### 3. HTTP Request URL

* 매체사에서 정의

### 4. HTTP Query Parameters

| 필드 | 타입 | 설명 |
|-----|----|-----|
| user_id | String  | 매체사에서 정의한 user_id ||

### 5. HTTP Response

#### Response body example
```json
{
  "user_id": "testuser1"
}
```

#### Response status code

| 코드 | 설명 |
|-----|-----|
| 200 | 해당 유저가 존재하는 경우 ||
| 404 | 해당 유저가 존재하지 않는 경우 ||
