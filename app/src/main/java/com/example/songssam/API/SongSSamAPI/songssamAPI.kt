package com.example.songssam.API.SongSSamAPI

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*
import java.io.File


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
    @POST("/member/upload")
    fun uploadSong(
        @Header("Authorization") Authorization: String,
        @Body favoriteSongs: File,
        @Query("songId") songId: Int
    ):Call<Void>
    @GET("/song/chartjson")
    fun chartJson(
    ):Call<List<chartjsonItems>>
    @GET("/song/uploaded_list")
    fun getUploadedList(
    ):Call<List<chartjsonItems>>
    @GET("/song/completed_list")
    fun getCompletedList(
    ):Call<List<chartjsonItems>>
    @Headers("accept: application/json",
        "content-type: application/json")
    @GET("/song/search")
    fun recordableSearch(
        @Query("target") target: String,
        @Query("mode") mode: Int
    ):Call<List<chartjsonItems>>
    @Multipart
    @POST("/song/upload")
    fun uploadSongToRecord(
        @Part ("songId") songId: RequestBody,
        @Part file: MultipartBody.Part?
    ):Call<Void>
    @POST("/song/preprocess")
    fun processingSong(
        @Query("songId") songId: Long
    ):Call<Void>
}