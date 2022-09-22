package com.example.smproject.src.main.getArPostApi

import android.util.Log
import com.example.smproject.config.ApplicationClass
import com.example.smproject.src.main.getArPostApi.models.GetArPostListRequest
import com.example.smproject.src.main.getArPostApi.models.GetArPostListResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GetArPostListService(val view:GetArPostListView) {
    fun tryGetArPostList(getArPostListRequest: GetArPostListRequest){
        val getArPostListRetrofitInterface = ApplicationClass.sRetrofit.create(GetArPostListRetrofitInterface::class.java)
        getArPostListRetrofitInterface.getArPostList(getArPostListRequest).enqueue(object:
            Callback<GetArPostListResponse>{
            override fun onResponse(call: Call<GetArPostListResponse>, response: Response<GetArPostListResponse>)
            {
                Log.d("Success","-----AR 게시물 목록 요청 통신성공-----")
                Log.d("code",response.code().toString())
                view.onGetArPostListSuccess(response.body() as GetArPostListResponse)
            }

            override fun onFailure(call: Call<GetArPostListResponse>, t: Throwable)
            {
                view.onGetArPostListFailure(t.message?:"통신오류")
                Log.d("Fail","-----통신실패-----")
            }
            })
    }
}