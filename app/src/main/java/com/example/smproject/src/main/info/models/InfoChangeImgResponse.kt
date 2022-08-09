package com.example.smproject.src.main.info.models

data class InfoChangeImgResponse(
    val data: Data2
)
data class Data2(
    val code: Int,
    val message: String,
    val result: Boolean
)