package com.example.songssam.Activitys

import android.app.Application
import com.example.songssam.DB.PreferenceUtil
import com.kakao.sdk.common.KakaoSdk

class GlobalApplication : Application() {

    companion object {
        lateinit var prefs: PreferenceUtil
    }
    override fun onCreate() {
        super.onCreate()
        // 다른 초기화 코드들

        prefs = PreferenceUtil(applicationContext)
        // Kakao SDK 초기화
        KakaoSdk.init(this, "8395ff7ed2359d6fbdc4cf50bc1ef7c0")
    }
}