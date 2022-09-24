package com.example.smproject.src.main.getPostApi

import android.util.Log
import com.example.smproject.config.ApplicationClass
import com.example.smproject.config.ApplicationClass.Companion.getPostListRetrofitInterface
import com.example.smproject.src.main.getPostApi.models.GetPostListRequest
import com.example.smproject.src.main.getPostApi.models.GetPostListResonse
import com.example.smproject.src.main.getPostApi.models.GetPostListRetrofitInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class GetPostListService(val view:GetPostListView) {
    fun tryGetPostList(getPostListRequest: GetPostListRequest){



        getPostListRetrofitInterface.getPostList(getPostListRequest).enqueue(object:
            Callback<GetPostListResonse>{
            override fun onResponse(call: Call<GetPostListResonse>, response: Response<GetPostListResonse>)
            {
                Log.d("Success","-----게시물 목록 요청 통신성공-----")
                Log.d("code",response.code().toString())
                view.onGetPostListSuccess(response.body() as GetPostListResonse)
            }

            override fun onFailure(call: Call<GetPostListResonse>, t: Throwable)
            {
                view.onGetPostListFailure(t.message?:"통신오류")
                Log.d("Fail","-----통신실패-----")
            }


        }




        )
    }
}