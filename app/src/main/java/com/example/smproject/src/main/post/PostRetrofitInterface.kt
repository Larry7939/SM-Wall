package com.example.smproject.src.main.post

import com.example.smproject.src.main.post.models.*
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface PostRetrofitInterface {
    @POST("/default/smwall")
    fun postPosting(@Body params:PostPostingRequest): Call<PostPostingResponse>
}