package com.example.songssam.API.SongSSamAPI

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

    @POST("/member/user_list")
    fun updateFavoriteSong(
        @Header("Authorization") Authorization: String,
        @Body favoriteSongs: List<Long>
    ):Call<Void>

    @Headers("accept: application/json",
        "content-type: application/json")
    @GET("/song/search")
    fun search(
        @Query("target") target: String,
        @Query("mode") mode: Int
    ):Call<List<items>>
}