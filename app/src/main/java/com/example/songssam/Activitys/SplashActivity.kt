package com.example.songssam.Activitys

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.songssam.API.SongSSamAPI.songssamAPI
import com.example.songssam.API.SongSSamAPI.user
import com.example.songssam.Activitys.GlobalApplication.Companion.prefs
import com.example.songssam.R
import com.kakao.sdk.common.util.Utility
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


class SplashActivity : AppCompatActivity() {
    val splashTime:Long = 1000

    override fun onCreate(savedInstanceState: Bundle?) {
        val keyHash = Utility.getKeyHash(this)
        Log.d("Hash", keyHash)

        super.onCreate(savedInstanceState)
        // 상단 액션바 숨기기
        supportActionBar?.hide()
        setContentView(R.layout.activity_splash)
        checkKakaoAccessToken()
    }

    private fun checkKakaoAccessToken() {
        val accesstokenInfo = prefs.getString("accessToken","")
        if(accesstokenInfo.equals("").not()){
            Log.d("check", accesstokenInfo)
            val retrofit = Retrofit.Builder()
                .baseUrl("https://songssam.site:8443")
                .addConverterFactory(GsonConverterFactory.create())
                .client(
                    OkHttpClient.Builder()
                        .readTimeout(
                            30,
                            TimeUnit.SECONDS
                        ) // Adjust the timeout as needed
                        .connectTimeout(30, TimeUnit.SECONDS)
                        .build()
                )
                .build()
            val apiService = retrofit.create(songssamAPI::class.java)
            val call = apiService.checkAccessToken("Bearer $accesstokenInfo")
            call.enqueue(object : Callback<user> {
                override fun onResponse(
                    call: Call<user>,
                    response: Response<user>
                ) {
                    if (response.isSuccessful.not()) {
                        Log.d("check", "member/info 실패")
                        Handler().postDelayed({
                            // This method will be executed once the timer is over
                            // Start your app main activity
                            startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
                            // close this activity
                            finish()
                        }, splashTime)
                        return
                    }
                    Log.d("check", "member/info 성공")
                    val nickname = response.body()?.userinfo!!.nickname
                    val id = response.body()?.userinfo!!.id
                    val email = response.body()?.userinfo!!.email
                    val role = response.body()?.userinfo!!.role
                    val profile = response.body()?.userinfo!!.profile
                    Log.d("check", "nickname : "+nickname + "\nid : " +  id + "\nemail : "+ email+ "\nrole : " + role +"\nprofile : "+ profile)

                    prefs.setString("nickname",nickname)
                    prefs.setString("id",id.toString())
                    prefs.setString("email",email)
                    prefs.setString("role",role)
                    prefs.setString("profile",profile)

                    startActivity(Intent(
                        this@SplashActivity,
                        MainActivity::class.java
                    ))
                }

                override fun onFailure(call: Call<user>, t: Throwable) {
                    Log.d("check", "첫 사용자이거나 token 유효성 만료")
                    // 네트워크 오류 등 호출 실패 시 처리
                    Handler().postDelayed({
                        // This method will be executed once the timer is over
                        // Start your app main activity
                        startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
                        // close this activity
                        finish()
                    }, splashTime)
                }
            })
        }else{
            startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
            finish()
        }
    }

}