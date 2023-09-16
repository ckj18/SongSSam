package com.example.songssam.Activitys


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.songssam.API.loginAPI.jwt
import com.example.songssam.API.loginAPI.songssamAPI
import com.example.songssam.API.loginAPI.user
import com.example.songssam.Activitys.GlobalApplication.Companion.prefs
import com.example.songssam.R
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class LoginActivity : AppCompatActivity() {

    val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
        if (error != null) {
            Log.e("login", "카카오계정으로 로그인 실패", error)
        } else if (token != null) {
            startActivity(Intent(this@LoginActivity, ChooseSongActivity::class.java))
        }
    }
    private val kakaoLoginButton: ImageView by lazy {
        findViewById(R.id.kakaoLoginButton)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        checkKakaoAccessToken()
        initKakaoLoginButton()
    }

    private fun checkKakaoAccessToken() {
        val accesstokenInfo = prefs.getString("accessToken","")
        if(accesstokenInfo.equals("").not()){
            Log.d("check", accesstokenInfo)
            val retrofit = Retrofit.Builder()
                .baseUrl("http://songssam.site:8080")
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
                        this@LoginActivity,
                        MainActivity::class.java
                    ))
                }

                override fun onFailure(call: Call<user>, t: Throwable) {
                    Log.d("retrofit", "첫 사용자이거나 token 유효성 만료")
                    // 네트워크 오류 등 호출 실패 시 처리
                }
            })
        }
    }

    private fun initKakaoLoginButton() {
        kakaoLoginButton.setOnClickListener {
            if (UserApiClient.instance.isKakaoTalkLoginAvailable(this@LoginActivity)) {
                UserApiClient.instance.loginWithKakaoTalk(this@LoginActivity) { token, error ->
                    if (error != null) {
                        Log.e("login", "카카오톡으로 로그인 실패", error)
                        // 사용자가 카카오톡 설치 후 디바이스 권한 요청 화면에서 로그인을 취소한 경우,
                        // 의도적인 로그인 취소로 보고 카카오계정으로 로그인 시도 없이 로그인 취소로 처리 (예: 뒤로 가기)
                        if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                            return@loginWithKakaoTalk
                        }
                        // 카카오톡에 연결된 카카오계정이 없는 경우, 카카오계정으로 로그인 시도
                        UserApiClient.instance.loginWithKakaoAccount(
                            this@LoginActivity,
                            callback = callback
                        )
                    } else if (token != null) {
                        Log.i("login", "카카오톡으로 로그인 성공 ${token.accessToken}")
                        val retrofit = Retrofit.Builder()
                            .baseUrl("http://songssam.site:8080")
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
                        val call = apiService.getKeywords(token.accessToken)
                        call.enqueue(object : Callback<jwt> {
                            override fun onResponse(
                                call: Call<jwt>,
                                response: Response<jwt>
                            ) {
                                if (response.isSuccessful.not()) {
                                    Toast.makeText(
                                        this@LoginActivity,
                                        "서버가 닫혀있습니다!",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    Log.d("login", "연결 실패")
                                    return
                                }
                                Log.d("login", "로그인 연결 성공")
                                var refreshToken = response.body()?.jwt?.refreshToken
                                var accessToken = response.body()?.jwt?.accessToken
                                Log.d(
                                    "login",
                                    "refreshToken : " + refreshToken + "\n" + "accessToken : " + accessToken
                                )

                                accessToken?.let { it1 -> prefs.setString("accessToken", it1) }
                                refreshToken?.let { it1 -> prefs.setString("refreshToken", it1) }

                                checkKakaoAccessToken()
                            }

                            override fun onFailure(call: Call<jwt>, t: Throwable) {
                                Log.d("retrofit", t.stackTraceToString())
                                Toast.makeText(
                                    this@LoginActivity,
                                    "네트워크 오류와 같은 이유로 오류 발생!",
                                    Toast.LENGTH_LONG
                                ).show()
                                // 네트워크 오류 등 호출 실패 시 처리
                            }
                        })
                    }
                }
            } else {
                UserApiClient.instance.loginWithKakaoAccount(
                    this@LoginActivity,
                    callback = callback
                )
            }
        }
    }
}