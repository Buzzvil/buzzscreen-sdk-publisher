# 지역 형식
지역 타게팅된 캠페인을 할당받기 위해서는 UserProfile.setRegion(String region)을 통해 유저의 지역 정보를 입력해야 한다. setRegion에 전달되는 region은 다음의 형식을 따라야 한다.

## 한국
- 형식 : **시/도 + 공백 + 시/군/구**
- [사용가능한 시/도 및 시/군/구 정보](https://docs.google.com/spreadsheets/d/1YlacZes23iaDjf-VMc5ZwtNL_-AwZ4dcpMeVrnxpkNA/edit#gid=0)
- ex : "서울특별시 관악구", "제주특별자치도 제주시"