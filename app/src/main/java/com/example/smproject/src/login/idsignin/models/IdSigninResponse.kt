package com.example.smproject.src.login.idsignin.models


data class IdSigninResponse(
    val data: Data
)

data class Data(
    val accessToken: String,
    val code: Int,
    val result: Boolean
)
