# Activity 설명

### (가) SplashActivity 
Splash screen은 이미지나 로고, 현재 버전의 소프트웨어를 포함한 그래픽 요소를 보여주는 화면으로, 보통 게임이나 프로그램이 실행되고 있을 때 나오는 화면입니다.
해당 액티비티에서 Song SSam 앱의 로고를 1초간 보여주고 LoginActivity로 이동합니다.

![SplashActivity](https://github.com/chlwnsxo00/SongSSam/assets/31373739/80275b85-9012-4de5-bf9e-6d252277fb89)


### (나) LoginActivity
Kakao oAuth를 이용한 로그인 기능 구현 - kakaotalk을 이용한 login을 시도한 후 kakaotalk이 미설치시 kakao계정을 이용한 로그인을 시도

![KakaoLoginActivity](https://github.com/chlwnsxo00/SongSSam/assets/31373739/0809ad26-274d-4be7-9932-1d6fb358f4e8)


### (다) ChooseSongActivity
(노래 10개 정도 선택) → 노래 선호도, 대표노래, 장르, 음역대

멜론에서 top 100 chart를 크롤링 해 recyclerView를 통해 gridView처럼 시각화한 Activity

<img src="https://github.com/chlwnsxo00/SongSSam/assets/31373739/4330fd04-0880-4dff-b576-374abcd7d327.jpeg" width="200" />

선택한 곡의 개수를 상단에 표시

10개 이상 선택시 Toast 메시지를 show

선택하면 체크표시가 Visible = true

선택해제하면 체크표시가 Visivle = false

곡을 10개 선택 시 완료 버튼이 클릭 가능 및 다음 엑티비티로 이동

구현 과정에서 오픈 소스를 3가지 사용

https://github.com/fornewid/neumorphism  -> neumorphism

https://github.com/hdodenhof/CircleImageView  -> 이미지 원형으로 crop

https://github.com/bumptech/glide  -> 이미지 삽입

### (라) RecordingAcitivity

<img src="https://github.com/chlwnsxo00/SongSSam/assets/31373739/16bc93ff-57d8-4095-accb-ad721c992e2a.jpeg" width="200"/>

선택한 곡을 바탕으로 음역대와 장르, 가수에 맞춰 사용자가 부르고 싶어할 곡을 예측해 비슷한 곡을 부르게 하도록 하는 Activity

상단에 Perfect score을 구현할 예정
그리고 총 MR의 시간과 녹음 기능 및 노래 실행 기능을 구현할 예정

실시간 pitch dectection / pitch shifting 기능 (TarsosDSP를 이용) - https://dev-minji.tistory.com/119
