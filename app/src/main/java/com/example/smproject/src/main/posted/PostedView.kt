package com.example.smproject.src.main.posted

import com.example.smproject.src.main.posted.models.PostedResponse

interface PostedView {
    fun onPostedSuccess(response: PostedResponse)
    fun onPostedFailure(message:String)
}