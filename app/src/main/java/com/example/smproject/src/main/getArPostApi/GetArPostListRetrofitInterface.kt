package com.example.smproject.src.main.getArPostApi

import com.example.smproject.src.main.getArPostApi.models.GetArPostListRequest
import com.example.smproject.src.main.getArPostApi.models.GetArPostListResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface GetArPostListRetrofitInterface {
    @POST("/default/smwall")
    fun getArPostList(@Body params: GetArPostListRequest): Call<GetArPostListResponse>

}