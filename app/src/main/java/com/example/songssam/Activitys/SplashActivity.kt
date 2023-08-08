package com.example.songssam.Activitys

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.songssam.R
import com.kakao.sdk.common.util.Utility


class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val keyHash = Utility.getKeyHash(this)
        Log.d("Hash", keyHash)
        val splashTime:Long = 1000
        super.onCreate(savedInstanceState)
        // 상단 액션바 숨기기
        supportActionBar?.hide()
        setContentView(R.layout.activity_splash)
        Handler().postDelayed({
            // This method will be executed once the timer is over
            // Start your app main activity
            startActivity(Intent(this, RecordingActivity::class.java))
            // close this activity
            finish()
        }, splashTime)
    }
}