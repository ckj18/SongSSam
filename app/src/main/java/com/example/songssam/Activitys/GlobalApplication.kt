package com.example.songssam.Activitys

import android.app.Application
import com.kakao.sdk.common.KakaoSdk

class GlobalApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // 다른 초기화 코드들

        // Kakao SDK 초기화
        KakaoSdk.init(this, "8395ff7ed2359d6fbdc4cf50bc1ef7c0")
    }
}