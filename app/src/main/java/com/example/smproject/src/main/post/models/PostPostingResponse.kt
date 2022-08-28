package com.example.smproject.src.main.post.models

data class PostPostingResponse(
    val data: Data
)
data class Data(
    val code: Int,
    val result: Boolean
)