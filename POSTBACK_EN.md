# BuzzScreen Postback API Guide
API to inform a partner server (publisher server) of user earning rewards through BuzzScreen SDK.

### 1. Request Direction
BuzzScreen -> Publisher Server
 
### 2. HTTP Request method
POST
 
### 3. HTTP Request URL
Defined by publisher
 
### 4. HTTP Request Parameters
| Field          | Type    | Description                                                                                                                                                                                                                      |
|----------------|---------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| transaction_id | string  | Used to prevent duplicated rewards offering. If requested with the same transaction_id, don't give out the rewards to prevent duplicated payment.                                                                                           |
| user_id        | string  | User_id defined by publisher                                                                                                                                                                                    |
| point          | integer | Reward points to offer for users                                                                                                                                                                                                      |
| base_point     | integer | Parameters regarding hourly free reward points to users. If a user is given points within this hour, it's 0. Or, it's the value of hourly free reward points.                                                                                           |
| campaign_id    | long    | Campaign id that offered reward points                                                                                                                                                                                                  |
| campaign_name  | string  | Campaign name that offered reward points                                                                                                                                                                         |
| event_at       | long    | The time of offering reward points. timestamp value. Mostly, it's the same as the time of API request. 
However, it could vary in case there was the second trial of request.                                                                                                |
| is_media       | integer | <ul><li>0: Campaigns from Buzzvil</li><li>1: Campaigns from Publisher</li></ul>                                                                                                                                                                                 |
| click_type     | string  | A separator of landing and swipe to unlock <ul><li>u: Swipe to unlock</li><li>l: Landing (Click) </li></ul>                                                                                                                                                                                   |
| extra          | string  | Json serialized strings of extra campaign data defined by publisher
It might take up to 10 mins to apply updated information in case these data were changed in the middle of ongoing campaign
eg) {"sub_type": "A", "source":"external"} |
| data           | string  | (Optional) Use this parameter when sending encrypted HTTP request parameter. Please refer to the below for more information.                                                                                                                 |                                                                                                         |
 
### 6. HTTP Request Parameter Encryption
Use this parameter when it is necessary to encrypt HTTP Request parameter. Please follow the process below in order.
```
JSON serialized parameters with UTF-8 encoding  -> AES(CBC mode, PCKS7 padding) encryption -> base64 encoding
```

Encrypted data should be sent with 'data' parameter in HTTP POST request.
When receiving it, data parameter from HTTP POST request should be decoded as below.
```
base64 decoding -> AES decoding -> JSON load
```
 
Please request for AES key and IV value to your account manager.
 
- Example in plaintext
```
{"user_id": "testuserid76301", "click_type": "u", "extra": "{}", "is_media": 0, "point": 2, "campaign_id": 3467, "event_at": 1442984268, "campaign_name": "test campaign", "base_point": 2, "transaction_id": 429482977}
```
- Example in ciphertext
```
Vblq5XX2g/M2fGs5GRbrLQGh6mwGXDI/frRb2Zn2syY0VAzG6ftcvDzaxSLLvgzYMmvhLTDKZATDX2F9U4AENfBZYQ/Ov+Y9QPfW9A39kaQi/XS3kea09+aI1pO0NkHqP8My8TuR//xhVYtoWovSIw42jbTzUhgJ8SePTC5ZwrLg7bOS7cy3gEgcHL9XzUOrxL8RqMo8fieSMv9hr2YkJJmNL2t0akyj/Hz/lXUvOqhrb9mmFuSlWLF/kS8af3fRKgjxNjGGIVDoVotPipFSbHbpExSp6wY0wsmjfcXGw6g=
AES key and IV value in the example are both '12341234asdfasdf'.
```
