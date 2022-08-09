package com.example.smproject.src.main.info

import android.util.Log
import com.example.smproject.config.ApplicationClass
import com.example.smproject.src.main.info.models.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class InfoLoadService(val view:InfoFragmentLoadView) {
    fun tryPostInfoLoad(infoLoadRequest: InfoLoadRequest){
        val infoLoadRetrofitInterface = ApplicationClass.sRetrofit.create(InfoLoadRetrofitInterface::class.java)
        infoLoadRetrofitInterface.postInfoLoad(infoLoadRequest).enqueue(object:
            Callback<InfoLoadResponse>{
            override fun onResponse(call: Call<InfoLoadResponse>, response: Response<InfoLoadResponse>) {
                Log.d("바디","${response.isSuccessful}")
                view.onPostInfoLoadSuccess(response.body() as InfoLoadResponse)
                Log.d("Success","-----통신성공-----")
                Log.d("code",response.code().toString())
            }

            override fun onFailure(call: Call<InfoLoadResponse>, t: Throwable) {
                view.onPostInfoLoadFailure(t.message?:"통신 오류")
                Log.d("Fail","-----통신실패-----")
            }
            })
    }
}