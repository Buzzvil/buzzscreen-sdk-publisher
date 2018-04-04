# BuzzScreen Postback API Guide
API for notifying a publisher server when users earn rewards through BuzzScreen lock screen.

### 1. Request Direction
* BuzzScreen -> Publisher Server
 
### 2. HTTP Request method

* **POST**
 
### 3. HTTP Request URL

* Defined by publisher
 
### 4. HTTP Request Parameters

* Optional Parameters
    * `base_point`, `click_type` : If `action_type` is `a`(which means that the reward is earned through user's action - eg. CPA/CPI campaigns), these parameters are not included.
    * `data` : This is only included when parameter encryption is enabled.(please refer to 5. HTTP Request Parameter Encryption)

* Info

| Field | Type | Description |
|-------|------|-------------|
| transaction_id | String(64)  | Use this to prevent duplicate rewards payouts.<br>If there exists a record with the same transaction_id in your server, don't give out the rewards to prevent duplicated payment.<br>**The maximum length of transaction_id is 64, so make sure that your system supports it beforehand.** |
| user_id        | String  | User_id defined by publisher |
| campaign_id    | Long    | Campaign id that offered reward points |
| campaign_name  | String  | Campaign name that offered reward points |
| event_at       | Long    | The timestamp of the reward event.<br>timestamp value.<br>Mostly, it's the same as the time of API request. However, it could vary in case there was the second trial of request. |
| is_media       | Integer | <ul><li>0: Campaigns from Buzzvil</li><li>1: Campaigns from Publisher</li></ul> |
| extra          | String  | Json serialized strings of extra campaign data defined by publisher. It can be set in the dashboard when creating ad campaigns. It might take up to 10 mins to apply updated information in case these data were changed in the middle of ongoing campaign. eg) {"sub_type": "A", "source":"external"} |
| action_type | String | The type of action that the user has done in order to get the point.<br>New types can be added later.<ul><li>u: unlock</li><li>l: landing </li><li>a: action(For CPA type - execute, sign up...)</li></ul>|
| point | Integer | Total reward points to offer for users.<br>It is the sum of base_point and the original points of participated campaign.|
| *base_point*   | Integer | (Optional) Hourly free reward points to users.<br>If a user is given points within this hour, it's 0.<br>Otherwise, it's the value of hourly free reward points set by your account manager. |
| data | String  | (Optional) Use this parameter when the parameters are encrypted. Please refer to the below for more information. |
| revenue_type | String  | Type of ad <ul><li>cpi: cost per install (install only)</li><li>cpe: cost per engagement (install + open)</li><li>cpa: cost per action (action required by advertiser)</li><li>Set as **null** if it's content</li></ul> |
| ~~click_type~~ (Deprecated) | String  | (Optional) A separator of landing and swipe to unlock <ul><li>u: Swipe to unlock</li><li>l: Landing (Click) </li></ul> |

### 5. HTTP Request Parameter Encryption
Guide for decrypting 'data' parameter when HTTP Request Parameter Encryption is enabled. Please follow the process below in order.

1. JSON serialized parameters with UTF-8 encoding
2. AES(CBC mode, PKCS7 padding) encryption
3. base64 encoding

Encrypted data should be sent as 'data' parameter in HTTP POST request.
When receiving it, data parameter from HTTP POST request should be decrypted as below.

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
sgfHOC5Z66tLmlokmQEaXY39u+64gMWhLnxQAZ9ivYsTvF1isjVfaRx2BNhOADwPR6KB55/7F7iXBm5FKU8mHmHnlR3wSomVAlcjtx77KluoYoXi/jRCvaFLGIo7vcK1GVHxS557u/XTo53/AzdPZpk/aXkvFZvWPgS+GWj1TWle0mBJ0xOgfmb8LwMfi4rvfayTph3bZeryLuphorBzMoIhf+kQLyjfIyouWVoCh6UICeRBgzTS9SlgdUA6M1PVlCsQch0zKVeTJZEFEn8478QbpEEhgHDhXkzdo8tXgkw=
```

