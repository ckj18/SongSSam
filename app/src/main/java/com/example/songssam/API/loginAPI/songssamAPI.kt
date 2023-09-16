package com.example.songssam.API.loginAPI

import com.kakao.sdk.auth.model.OAuthToken
import retrofit2.Call
import retrofit2.http.*


interface songssamAPI {
    @Headers("accept: application/json",
        "content-type: application/json")
    @POST("/android/login")
    fun getKeywords(
        @Body authorizationCode: String
    ): Call<jwt>

    @Headers("accept: application/json",
        "content-type: application/json")
    @GET("/member/info")
    fun checkAccessToken(
        @Header("Authorization") Authorization: String,
    ):Call<user>
}