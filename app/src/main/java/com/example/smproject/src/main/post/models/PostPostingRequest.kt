package com.example.smproject.src.main.post.models

data class PostPostingRequest(
    val action: String,
    val content: String,
    val imageList: ArrayList<String>,
    val hashtag: String,
    val isPrivate: Int,
    val lat: String,
    val lng: String
)