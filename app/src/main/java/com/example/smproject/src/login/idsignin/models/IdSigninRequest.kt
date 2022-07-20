package com.example.smproject.src.login.idsignin.models

data class IdSigninRequest(
    val action: String,
    val id: String,
    val password: String
)