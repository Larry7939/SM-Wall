package com.example.smproject.src.login.idsignup

import android.util.Log
import com.example.smproject.config.ApplicationClass
import com.example.smproject.src.login.idsignup.models.IdSignupRequest
import com.example.smproject.src.login.idsignup.models.IdSignupResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class IdSignupService(val view:IdSignupView) {
    fun tryPostIdSignup(postSignupRequest: IdSignupRequest){
        val idSignupRetrofitInterface = ApplicationClass.sRetrofit.create(IdSignupRetrofitInterface::class.java)
        idSignupRetrofitInterface.postSignup(postSignupRequest).enqueue(object:
            Callback<IdSignupResponse>{
            override fun onResponse(call: Call<IdSignupResponse>, response: Response<IdSignupResponse>) {
                Log.d("바디","${response.isSuccessful}")
                view.onPostSignUpSuccess(response.body() as IdSignupResponse)
                Log.d("Success","-----통신성공-----")
                Log.d("code",response.code().toString())
            }

            override fun onFailure(call: Call<IdSignupResponse>, t: Throwable) {
                view.onPostSignUpFailure(t.message?:"통신 오류")
                Log.d("Fail","-----통신실패-----")
            }
        })
    }
}