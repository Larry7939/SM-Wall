package com.example.smproject.src.main.posted

import android.util.Log
import com.example.smproject.config.ApplicationClass
import com.example.smproject.config.ApplicationClass.Companion.postedRetrofitInterface
import com.example.smproject.src.login.idsignup.models.IdSignupResponse
import com.example.smproject.src.main.posted.models.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PostedService(val view: PostedView) {
    fun tryGetPosted(postedRequest: PostedRequest){

        postedRetrofitInterface.postPosted(postedRequest).enqueue(object:
            Callback<PostedResponse>{
            override fun onResponse(
                call: Call<PostedResponse>,
                response: Response<PostedResponse>
            ) {
                Log.d("바디","${response.isSuccessful}")
                view.onPostedSuccess(response.body() as PostedResponse)
                Log.d("Success","-----통신성공-----")
                Log.d("code",response.code().toString())
            }

            override fun onFailure(call: Call<PostedResponse>, t: Throwable) {
                view.onPostedFailure(t.message?:"통신 오류")
                Log.d("Fail","-----통신실패-----")
            }


        }
        )

    }
}