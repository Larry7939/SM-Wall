package com.example.smproject.src.main.getPostApi.models

data class GetPostListRequest(
    val action: String,
    val days: Int?,
    val keyword:String?,
    val withImage:Int?,
    val userCreatedPost:Int?,
    val isPrivate:Int?
)