package com.example.smproject.src.login.idsignup.models

data class IdSignupRequest(
    val action: String,
    val cellphone: String,
    val id: String,
    val nickname: String,
    val password: String
)