package com.example.smproject.src.main.post

import com.example.smproject.src.main.post.models.PostPostingResponse

interface PostFragmentPostingView {
    fun onPostPostingSuccess(response: PostPostingResponse)
    fun onPostPostingFailure(message: String)
}