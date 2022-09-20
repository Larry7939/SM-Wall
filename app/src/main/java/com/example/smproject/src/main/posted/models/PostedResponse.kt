package com.example.smproject.src.main.posted.models

data class PostedResponse(
    val data: Data
)
data class Data(
    val code: Int,
    val info: Info,
    val result: Boolean
)

data class Info(
    val content: String,
    val hashtag: String,
    val id: Int,
    val imageUrlList: ArrayList<String>?,
    val isPrivate: Boolean,
    val locationObj: LocationObj,
    val userObj: UserObj
)
data class LocationObj(
    val lat: String,
    val lng: String
)
data class UserObj(
    val id: String?,
    val nickname: String?="사용자 닉네임",
    val imageUrl:String?
)