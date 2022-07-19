package com.example.smproject.src.login.idsignup.models

data class IdSignupResponse(
    val data: Data
)
data class Data(
    val result: Boolean,
    val code: Int,
    val message: String?
)