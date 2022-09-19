package com.example.smproject.src.main.post

import android.util.Log
import com.example.smproject.config.ApplicationClass
import com.example.smproject.src.main.post.models.*
import retrofit2.Callback
import retrofit2.Call
import retrofit2.Response

class PostPostingService(val view:PostFragmentPostingView) {
    fun tryPostPosting(postPostingRequest:PostPostingRequest){
        val postPostingRetrofitInterface = ApplicationClass.sRetrofit.create(PostRetrofitInterface::class.java)
        postPostingRetrofitInterface.postPosting(postPostingRequest).enqueue(object:
            Callback<PostPostingResponse>{
            override fun onResponse(call: Call<PostPostingResponse>, response: Response<PostPostingResponse>)
            {
                Log.d("바디","${response.isSuccessful()}")
                Log.d("Success","-----포스팅 통신성공-----")
                Log.d("code",response.code().toString())

                view.onPostPostingSuccess(response.body() as PostPostingResponse)
            }
            override fun onFailure(call: Call<PostPostingResponse>, t: Throwable)
            {
                view.onPostPostingFailure(t.message?:"통신 오류")
                Log.d("Fail","-----포스팅 통신실패-----")
            }
        }

        )

    }
}