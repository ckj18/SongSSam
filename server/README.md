# 필요한 사전 설정
```
* yaml파일 설정
* keystore의 비밀번호와 암호 방식, 경로를 설정
* jwt에 사용할 secret-key를 설정, 만료시간, 재발급 시간 설정
* oauth2에 등록한 client-id 설정, 로그인한 사용자가 이동할 redirect-url경로 설정
* s3 버킷 경로, access-key,secret-key, region설정
* DL서버 경로 설정(이번 프로젝트에서는 load balancer의 경로로 설정)
* mysql 설정
* 수용가능한 용량 servlet multipart max-request-size, max-file-size설정
```
# 빌드 방법
```
git clone
certbot을 통해 발급받은 keystore.p12 파일을 src/main/resource 경로에 옮김
./gradlew build통해 .jar 압축파일 생성
cd ./build/libs
nohup java -jar {생성된파일이름}.jar & 통해 배포
```

* 추천 알고리즘은 아래 레포지토리의 README를 참고하여 설정
* 노래 데이터의 전처리 및 음역대 추출, AI보컬곡 생성은 아래 레포지토리의 README를 참고하여 설정
  * DL 서버: https://github.com/ckj18/SongSSam/blob/master/djangoserver/readme.md
