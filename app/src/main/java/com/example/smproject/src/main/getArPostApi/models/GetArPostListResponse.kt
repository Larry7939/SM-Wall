package com.example.smproject.src.main.getArPostApi.models

data class GetArPostListResponse(
    val data: Data
)

data class Data(
    val code: Int,
    val list: ArrayList<Post>,
    val result: Boolean
)

data class Post(
    val id: Int,
    val locationObj: LocationObj
)

data class LocationObj(
    val lat: String,
    val lng: String
)
