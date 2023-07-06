# 모각소 & 파란학기제 프로젝트 ( Sam SSong )

음성 Diffusion 모델인 DDSP-SVC를 활용하여 서비스 이용자의 목소리를 바탕으로 커버곡을 생성하고 이용자가 본인의 노래 녹음 파일을 올리면 유사도를 검증하여 노래를 코칭해주는 멘토링 서비스

## 1. 각 Activiry 설명

### (가) SplashActivity 
Splash screen은 이미지나 로고, 현재 버전의 소프트웨어를 포함한 그래픽 요소를 보여주는 화면으로, 보통 게임이나 프로그램이 실행되고 있을 때 나오는 화면입니다.
해당 액티비티에서 Song SSam 앱의 로고를 1초간 보여주고 LoginActivity로 이동합니다.

![SplashActivity](https://github.com/chlwnsxo00/SongSSam/assets/31373739/80275b85-9012-4de5-bf9e-6d252277fb89)


### (나) LoginActivity
Kakao oAuth를 이용한 로그인 기능 구현 - kakaotalk을 이용한 login을 시도한 후 kakaotalk이 미설치시 kakao계정을 이용한 로그인을 시도

![KakaoLoginActivity](https://github.com/chlwnsxo00/SongSSam/assets/31373739/0809ad26-274d-4be7-9932-1d6fb358f4e8)


### (다) AnalysisingUserActivity
(노래 10개 정도 선택) → 노래 선호도, 대표노래, 장르, 음역대

멜론에서 top 100 chart를 크롤링 해 gridview를 통해 시각화하는 Activity

![ChooseSongActivity](https://github.com/chlwnsxo00/SongSSam/assets/31373739/e486bb79-4bff-48e1-a77d-381a4b94b9dc)


여러 노래 목록을 보여주고 사용자가 노래를 10개 정도 선택할 수 있도록 함.

선택한 노래를 알고리즘으로 분석해 사용자의 노래 선호도, 대표노래, 장르를 추출함.

음역대의 경우 선택한 노래를 통해 높은 음역대를 좋아하는지, 낮은 음역대를 좋아하는지를 파악.

-> 잘 부르고 싶은 원하는 노래가 있는지를 설문을 통해 알아내고 있다면 해당 노래의 음역대나 장르 등에 맞게 샘플 데이터를 수집하는 것이 더욱 좋은 결과를 얻을 수 있을 것 같음.

https://velog.io/@mstar228/%EC%B6%94%EC%B2%9C%EC%95%8C%EA%B3%A0%EB%A6%AC%EC%A6%98-%EA%B0%9C%EC%9A%94
