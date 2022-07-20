package com.example.smproject.src.login.idsignin

import android.util.Log
import com.example.smproject.config.ApplicationClass
import com.example.smproject.src.login.idsignin.models.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class IdSigninService(val view:IdSigninView) {
    fun tryGetIdSignin(postSigninRequest: IdSigninRequest){
        val idSigninRetrofitInterface = ApplicationClass.sRetrofit.create(IdSigninRetrofitInterface::class.java)
        idSigninRetrofitInterface.postSignin(postSigninRequest).enqueue(object:
            Callback<IdSigninResponse> {
            override fun onResponse(call: Call<IdSigninResponse>, response: Response<IdSigninResponse>) {
                Log.d("바디","${response.isSuccessful}")
                view.onPostSigninSuccess(response.body() as IdSigninResponse)
                Log.d("Success","-----통신성공-----")
                Log.d("code",response.code().toString())
            }

            override fun onFailure(call: Call<IdSigninResponse>, t: Throwable) {
                view.onPostSigninFailure(t.message?:"통신 오류")
                Log.d("Fail","-----통신실패-----")
            }
        })
    }
}