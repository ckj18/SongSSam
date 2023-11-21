package com.example.songssam.API.SongSSamAPI

import com.google.gson.annotations.SerializedName

data class chartjsonItems(
    @SerializedName("id")val songID : Long,
    @SerializedName("imgUrl")val coverImage : String,
    @SerializedName("title")val title : String,
    @SerializedName("artist")val artist : String,
    @SerializedName("status")var status : String,
    @SerializedName("originUrl")val originUrl : String?,
    @SerializedName("instUrl")val instUrl : String?
)

data class ChartJsonItem(
    @SerializedName("generatedUrl") val generatedUrl: String,
    @SerializedName("song") val song: chartjsonItems,
)

data class items(
    @SerializedName("id")val songID : Long,
    @SerializedName("imgUrl")val coverImage : String,
    @SerializedName("title")val title : String,
    @SerializedName("artist")val artist : String,
    var selected : Boolean = false
)
data class jwt(
    @SerializedName("response")val jwt : tokens
)

data class tokens(
    @SerializedName("accessToken")val accessToken : String,
    @SerializedName("refreshToken")val refreshToken : String
)

data class user(
    @SerializedName("HttpStatus")val status:Long,
    @SerializedName("response")val userinfo : userInfo
)

data class userInfo(
    @SerializedName("id")val id : Long,
    @SerializedName("email")val email : String,
    @SerializedName("nickname")val nickname : String,
    @SerializedName("profileUrl")val profile : String,
    @SerializedName("role")val role : String
)

data class Voice(
    @SerializedName("id")val id : Long,
    @SerializedName("name")val name : String
)
