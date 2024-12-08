# <img width="35" src="https://github.com/user-attachments/assets/8c576c5b-8695-4bb4-81d2-524f4a4bdc04"> MODAK


[<img width="220" src="https://github.com/user-attachments/assets/1e5a21a3-8bc8-40d2-b2d1-e0e75b3b2c0e">](https://apps.apple.com/kr/app/modak-%EB%AA%A8%EB%8B%A5/id6737128650)

![modakReplet](https://github.com/user-attachments/assets/29cafea7-99ef-4d11-b8d2-a6d6ed066406)

> 본 페이지는 서버 구현 내용을 다룹니다. 프로젝트 소개는 다음 페이지를 참고해주세요.

- https://github.com/MacroSoft-Team/modak-ios (팀 깃허브)
- https://github.com/DeveloperAcademy-POSTECH/2024-MacC-M18-MacroSoft (아카데미 공식 깃허브)

# 서버 아키텍쳐
![modak architecture](https://github.com/user-attachments/assets/a8457d51-75f0-4d68-9a9c-07aa19efb133)

## 회원 인증 방식

회원 인증 방식은 토큰 방식 (JWT) 를 사용했습니다.
선택 이유에 대한 자세한 내용은 [웹과 모바일 앱에 따라 다른 Token vs Session 방식 선택 기준](https://velog.io/@dgh06175/token-vs-session) 를 참고해주세요.

## 사진 트래픽 비용

이미지를 대량으로 다루는 서비스이므로 트래픽 비용 절감이 필수였습니다.
여러 기술을 도입한 결과, 쇼케이스 트래픽에도 0.1달러 미만을 지출했습니다. (AWS 프리티어 기준)
자세한 절감 과정은 [이미지 SNS 서버 비용 줄이기](https://velog.io/@dgh06175/ondemand-image-resizing)

## 👨‍👨‍👦‍👦 Team

|<img src="https://avatars.githubusercontent.com/u/99196087?v=4" width="150" height="150"/>|<img src="https://avatars.githubusercontent.com/u/149608045?v=4" width="150" height="150"/>|<img src="https://avatars.githubusercontent.com/u/66589666?v=4" width="150" height="150"/>|<img src="https://avatars.githubusercontent.com/u/77305722?v=4" width="150" height="150"/>|<img src="https://avatars.githubusercontent.com/u/83539914?v=4" width="150" height="150"/>|
|:-:|:-:|:-:|:-:|:-:|
|박준우<br/>[@Junu-Park](https://github.com/Junu-Park)|신수진<br/>[@Jinjinjinzin](https://github.com/Jinjinjinzin)|김지희<br/>[@jihee-daily](https://github.com/jihee-daily)|이상현<br/>[@dgh06175](https://github.com/dgh06175)|진윤겸<br/>[@Younkyum](https://github.com/Younkyum)|
|iOS 개발|디자인|iOS 개발|서버 개발|프로젝트 매니저|

> Apple Developer Academy @ POSTECH 3th - MacC Team 18 Macrosoft<br>
> 2024.09.02 ~ 2024.12.05
