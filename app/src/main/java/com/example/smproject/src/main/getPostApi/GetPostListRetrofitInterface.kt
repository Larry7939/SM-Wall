package com.example.smproject.src.main.getPostApi.models

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface GetPostListRetrofitInterface {
    @POST("/default/smwall")
    fun getPostList(@Body params:GetPostListRequest): Call<GetPostListResonse>
}