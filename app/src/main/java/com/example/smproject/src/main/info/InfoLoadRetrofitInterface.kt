package com.example.smproject.src.main.info


import com.example.smproject.src.main.info.models.*
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface InfoLoadRetrofitInterface {
    @POST("/default/smwall")
    fun postInfoLoad(@Body params:InfoLoadRequest): Call<InfoLoadResponse>
}