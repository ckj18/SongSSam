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
```[Unit]
Description=Gunicorn daemon for Your App
After=network.target

[Service]
User=ubuntu
Group=www-data
WorkingDirectory=/home/ubuntu/songssam_ml
ExecStart=/home/ubuntu/venv/bin/gunicorn -b 0.0.0.0:8000 -w 4 -b 0.0.0.0:8001 mydjango.wsgi:application --timeout 500

[Install]
WantedBy=multi-user.target```
