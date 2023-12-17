# s3 설정
```
git clone
/djangoserver/songssam/views.py에서 s3의 버킷, 리전, access-key,secret-key을 spring서버와 동일한 값으로 설정한다.
```

# gunicorn 설정
```
gunicorn을 사용하여 데몬으로 django서버를 배포한다.
f0_extractor추출, AI 보컬곡 생성에 2~5분정도의 시간이 소요되기 때문에 timeout시간을 넉넉하게 잡는다.
아래는 프로젝트에서 사용한 gunicorn의 daemon service설정이다.
```
```
[Unit]
Description=Gunicorn daemon for Your App
After=network.target

[Service]
User=ubuntu
Group=www-data
WorkingDirectory=/home/ubuntu/songssam_ml
ExecStart=/home/ubuntu/venv/bin/gunicorn -b 0.0.0.0:8000 -w 4 -b 0.0.0.0:8001 mydjango.wsgi:application --timeout 500

[Install]
WantedBy=multi-user.target
```

# nginx 설정
```
loadbalancer에서 80번 포트로 오는 요청중 DL서비스에 대한 요청은 8000으로 넘기고
health-check에 대한 요청은 8001로 넘겨준다.
그리고 nginx에서 gunicorn으로 요청후 응답을 받기 까지 default timeout시간은 60초 이므로
timeout status code를 반환하고 세션을 nginx에서 끊는 것을 방지 하기위해 timeout시간을 500으로 설정한다.
```
