package com.example.smproject.src.main.getPostApi.models

data class GetPostListResonse(
    val data: Data
)

data class Data(
    val code: Int,
    val list: ArrayList<Post>,
    val result: Boolean
)
data class Post(
    val id: Int,
    val locationObj: LocationObj,
    val withImage: Boolean,
    val userCreatedPost:Boolean,
    val isPrivate:Boolean
)
data class LocationObj(
    val lat: String,
    val lng: String
)