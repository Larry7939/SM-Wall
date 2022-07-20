package com.example.smproject.src.login.idsignin

import com.example.smproject.src.login.idsignin.models.*
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface IdSigninRetrofitInterface {
    @POST("/default/smwall")
    fun postSignin(@Body params:IdSigninRequest): Call<IdSigninResponse>
}