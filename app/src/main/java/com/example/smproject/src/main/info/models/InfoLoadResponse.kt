package com.example.smproject.src.main.info.models

data class InfoLoadResponse(
    val data: Data
)

data class Info(
    val id: String,
    val image: Any,
    val nickname: String
)

data class Data(
    val code: Int,
    val info: Info,
    val result: Boolean
)