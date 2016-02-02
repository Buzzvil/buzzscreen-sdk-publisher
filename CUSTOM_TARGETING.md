# BuzzScreen Custom Targeting
- 유저에게 캠페인을 할당할 때, SDK에서 기본적으로 제공하는 타게팅 방법(나이, 성별, 지역) 외의 퍼블리셔에 의해 커스터마이징 된 방법으로 더 구체적인 타게팅을 할 수 있다.
- 캠페인 운영자가 admin에서 캠페인을 등록할 때 캠페인의 커스텀 타게팅 필드에 타게팅 정보를 설정하고, 연동 담당 개발자가 각 유저의 UserProfile 객체의 커스텀 타게팅 필드에 타게팅 정보를 설정함으로써 커스텀 타게팅이 이루어진다.
- 총 3개의 커스텀 타게팅 필드를 제공한다. 

## UserProfile 커스텀 타게팅 적용 방법
##### 연동 담당 개발자용

### 1. 타게팅 정보의 표현 형식
- UserProfile 객체에 설정하는 커스텀 타게팅 정보의 타입은 String 으로, 별도로 정해진 표현 형식의 제한은 없다. 퍼블리셔 측에서 자체적으로 캠페인과 UserProfile 간에 표현 형식이 일치하도록 정하면 된다.
- UserProfile에는 각각의 커스텀 타게팅 필드당 하나의 정보만 설정 가능하다.

### 2. 타게팅 정보 설정
- `BuzzScreen.getInstance().getUserProfile()` 을 통해 UserProfile을 가져온다.
- 가져온 UserProfile에  `setCustomTarget1()`, `setCustomTarget2()`, `setCustomTarget3()` 메소드를 통해 유저에게 해당되는 타게팅 정보를 설정해준다.

### 3. 타게팅 정보 설정 예제

```java
	UserProfile userProfile = BuzzScreen.getInstance().getUserProfile();
	
	userProfile.setUserId(...);
	userProfile.setBirthYear(...);
	...
	userProfile.setCustomTarget1("music");
    userProfile.setCustomTarget2("has_car");
    userProfile.setCustomTarget3("married");
```

## (참고사항) 캠페인 커스텀 타게팅 관련 내용
##### 캠페인 운영자용

### 캠페인 타게팅 정보 설정
admin에서 캠페인 등록 시 타게팅 정보를 설정하는 부분으로, 'Custom target 1, 2, 3' 이 커스텀 타게팅 정보를 입력하는 필드이다.
![Targeting_admin](targeting_admin.png)

### 멀티 타게팅
- 캠페인에서는 커스텀 타게팅 필드 하나에 여러 개의 정보를 설정하면 다양한 유저 그룹에 멀티 타게팅이 가능하다. 이 때의 정보들은 논리적으로 'or' 조건으로 작용한다.
	> UserProfile 의 CustomTarget1 에 설정 가능한 정보의 목록이 "sports", "music", "food", "travel" 이라고 하자. 유저는 이 중 하나를 자신의 UserProfile 정보로 가지게 된다. 만약 캠페인을 등록할 때 Custom target1 란에 ` sports, music, food ` 를 설정하면 "travel" 을 제외한 "sports" 혹은 "music" 혹은 "food"를 CustomTarget1 의 정보로 가진 유저 모두에게 해당 캠페인이 할당 가능하다.

## 주의사항
- 캠페인의 타게팅 정보와 UserProfile의 타게팅 정보는 캠페인 할당 과정에서 실제 하는 역할이 다르다. 캠페인의 타게팅 정보는 그 캠페인을 특정 유저에게 할당해도 괜찮은지를 결정하는 **할당 조건**이고, UserProfile의 타게팅 정보는 유저가 그 조건을 만족하는지 살펴보기 위한 **지표**이다. 유저가 서버로 캠페인 할당을 요청하면, 서버는 현재 확보하고 있는 모든 캠페인 각각의 타게팅 조건을 그 유저가 만족하는지 확인하고 할당 여부를 결정한다.
	> 캠페인에는 타게팅 정보가 있는데 UserProfile의 타게팅 정보가 누락되었을 때는 유저가 할당 조건을 만족하지 못한 것이므로 해당 캠페인이 할당되지 않는다.
	> 반면에 UserProfile에는 타게팅 정보가 있는데 캠페인의 타게팅 정보가 누락되었을 때는 해당 캠페인의 특별한 할당 조건이 없는 것으로, 유저의 타게팅 정보의 유무에 상관 없이 모든 유저에게 할당될 수 있으므로 해당 캠페인이 할당된다.

- 캠페인과 UserProfile끼리 정보를 비교할 때 커스텀 타게팅용 1, 2, 3번 필드는 **서로 독립적**이다. 즉, 캠페인의 'Custom target1'의 정보는 UserProfile의 'CustomTarget1'과만 비교하고 2, 3도 또한 그러하다. 따라서 각각의 필드에 설정 가능한 타게팅 정보의 목록은 겹치는 항목 없이 완전히 독립적으로 관리해야 한다.
	> 캠페인의 1번 필드에 "music" 을 설정했으나, 유저의 'CustomTarget2'에 "music"을 설정한 경우에는 타게팅에 실패하고 해당 캠페인은 그 유저에게 나가지 않는다.

- 다수의 타게팅 정보를 가진 캠페인의 경우, 각각의 타게팅 정보는 동등한 우선순위를 가지며 논리적으로 'and' 조건과 같은 역할을 하여 UserProfile이 **모든 조건을 동시에 만족**해야 그 캠페인이 유저에게 할당된다. 이는 기본 타게팅 정보(나이, 성별, 지역)와 커스텀 타게팅 정보 모두에 해당된다.
	> 다음과 같은 경우에는 캠페인이 할당되지 않는다.
	> eg1) 유저의 'CustomTarget1', 'CustomTarget3'이 각각 캠페인의 타게팅 조건을 만족하지만 'CustomTarget2'만 만족하지 않을 경우
	> eg2) 유저의 'CustomTarget1', 'CustomTarget2', 'CustomTarget3'이 모두 조건을 만족하지만 타게팅 성별이 일치하지 않을 경우.
	