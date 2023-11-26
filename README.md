# SongSSam Model 
![image](https://github.com/ckj18/SongSSam/assets/48816329/39468924-66ce-4ffc-861f-e2ec23e26c79)

사용자의 목소리를 이용한 AI 커버곡 생성 서비스

## 소개

![image](https://github.com/ckj18/SongSSam/assets/48816329/e4ca96a8-dfc2-47aa-b794-99d9bc333e42)


SongSSam은 기존의 음성 합성 기술의 제한성을 인지하고 비전문 사용자가 이용하기 쉽지 않다는 문제를 해결하기 위해 이번 프로젝트를 진행했습니다.
AI를 활용한 음성 합성 기술이 고도로 진화함에 따라 AI 커버곡을 생성하는 것은 앞으로의 문화미디어 컨텐츠에 상업적 가치가 있다는 사실을 반증합니다. DDSP-SVC를 Baseline 모델로 사용하여 사용자가 목소리를 녹음하면, 서버에서 학습한 결과를 기반으로 사용자가 원하는 노래에 대해 본인의 목소리를 학습할 수 있도록 합니다. 

이 프로젝트는 안드로이드 애플리케이션에서 작동하며, 서버는 Spring과 Django로 운영합니다. 이에 대한 정보는 해당 폴더에서 구체적으로 확인할 수 있습니다.

모델을 사용하기 위해 다음 링크로부터 git clone을 하세요. DDSP-SVC github : https://github.com/yxlllc/DDSP-SVC/tree/master
보컬 분리를 사용하기 위해 다음 링크로부터 git clone을 하세요. Vocal-Remover github : https://github.com/tsurumeso/vocal-remover/tree/develop

## 참고
* [ddsp](https://github.com/magenta/ddsp)
* [pc-ddsp](https://github.com/yxlllc/pc-ddsp)
* [soft-vc](https://github.com/bshall/soft-vc)
* [DiffSinger (OpenVPI version)](https://github.com/openvpi/DiffSinger)
