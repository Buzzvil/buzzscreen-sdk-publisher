# BuzzScreen Postback API Guide
API to inform a partner server (publisher server) of user earning rewards through BuzzScreen SDK.

### 1. Request Direction
* BuzzScreen -> Publisher Server
 
### 2. HTTP Request method

* **POST**
 
### 3. HTTP Request URL

* Defined by publisher
 
### 4. HTTP Request Parameters

* Optional Parameters
    * `base_point`, `click_type` : If `action_type` is `a`(which means that the point is derived from user's action), these parameters are not included.
    * `data` : It is included if and only if you request to encrypt the request parameter.(please refer to 5. HTTP Request Parameter Encryption)

* Info

| Field | Type | Description |
|-------|------|-------------|
| transaction_id | String(64)  | Used to prevent duplicated rewards offering.<br>If requested with the same transaction_id, don't give out the rewards to prevent duplicated payment.<br>**The maximum length of transaction_id is 64, so make sure that your system supports it beforehand.** |
| user_id        | String  | User_id defined by publisher |
| campaign_id    | Long    | Campaign id that offered reward points |
| campaign_name  | String  | Campaign name that offered reward points |
| event_at       | Long    | The time of offering reward points.<br>timestamp value.<br>Mostly, it's the same as the time of API request. However, it could vary in case there was the second trial of request. |
| is_media       | Integer | <ul><li>0: Campaigns from Buzzvil</li><li>1: Campaigns from Publisher</li></ul> |
| extra          | String  | Json serialized strings of extra campaign data defined by publisher. It might take up to 10 mins to apply updated information in case these data were changed in the middle of ongoing campaign. eg) {"sub_type": "A", "source":"external"} |
| action_type | String | The type of action that the user has done in order to get the point.<br>New types can be added later.<ul><li>u: unlock</li><li>l: landing </li><li>a: action(For CPA type - execute, sign up...)</li></ul>|
| point | Integer | Total reward points to offer for users.<br>It is the sum of base_point and the original points of participated campaign.|
| *base_point*   | Integer | (Optional) Parameters regarding hourly free reward points to users.<br>If a user is given points within this hour, it's 0.<br>Otherwise, it's the value of hourly free reward points. |
| data | String  | (Optional) Use this parameter when sending encrypted HTTP request parameter. Please refer to the below for more information. |
| ~~click_type~~ (Deprecated) | String  | (Optional) A separator of landing and swipe to unlock <ul><li>u: Swipe to unlock</li><li>l: Landing (Click) </li></ul> |

### 5. HTTP Request Parameter Encryption
Use this parameter when it is necessary to encrypt HTTP Request parameter. Please follow the process below in order.

1. JSON serialized parameters with UTF-8 encoding
2. AES(CBC mode, PCKS7 padding) encryption
3. base64 encoding

Encrypted data should be sent with 'data' parameter in HTTP POST request.
When receiving it, data parameter from HTTP POST request should be decoded as below.

1. base64 decoding
2. AES decoding
3. JSON load
 
Please request for AES key and IV value to your account manager.
 
- Example in plaintext
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

- Example in ciphertext
    
    > AES key and IV value in the example are both '12341234asdfasdf'.

```
Vblq5XX2g/M2fGs5GRbrLQGh6mwGXDI/frRb2Zn2syY0VAzG6ftcvDzaxSLLvgzYMmvhLTDKZATDX2F9U4AENfBZYQ/Ov+Y9QPfW9A39kaQi/XS3kea09+aI1pO0NkHqP8My8TuR//xhVYtoWovSIw42jbTzUhgJ8SePTC5ZwrLg7bOS7cy3gEgcHL9XzUOrxL8RqMo8fieSMv9hr2YkJJmNL2t0akyj/Hz/lXUvOqhrb9mmFuSlWLF/kS8af3fRKgjxNjGGIVDoVotPipFSbHbpExSp6wY0wsmjfcXGw6g=
```

