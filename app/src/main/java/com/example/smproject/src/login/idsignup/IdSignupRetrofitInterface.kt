package com.example.smproject.src.login.idsignup

import com.example.smproject.src.login.idsignup.models.*
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface IdSignupRetrofitInterface {
    @POST("/default/smwall")
    fun postSignup(@Body params:IdSignupRequest): Call<IdSignupResponse>
}