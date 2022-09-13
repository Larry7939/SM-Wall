package com.example.smproject.src.main.posted

import com.example.smproject.src.main.posted.models.*
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface PostedRetrofitInterface {
    @POST("/default/smwall")
    fun postPosted(@Body params: PostedRequest): Call<PostedResponse>
}