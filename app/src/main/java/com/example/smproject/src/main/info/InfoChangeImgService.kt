package com.example.smproject.src.main.info

import android.util.Log
import com.example.smproject.config.ApplicationClass
import com.example.smproject.src.main.info.models.InfoChangeImgRequest
import com.example.smproject.src.main.info.models.InfoChangeImgResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class InfoChangeImgService(val view:InfoFragmentChangeImgView) {
    fun tryPostInfoChangeImg(infoChangeImgRequest:InfoChangeImgRequest){
        val infoChangeImgRetrofitInterface = ApplicationClass.sRetrofit.create(InfoChangeImgRetrofitInterface::class.java)
        infoChangeImgRetrofitInterface.postInfoChangeImg(infoChangeImgRequest).enqueue(object:
            Callback<InfoChangeImgResponse>{
            override fun onResponse(call: Call<InfoChangeImgResponse>, response: Response<InfoChangeImgResponse>){
                Log.d("바디","${response.isSuccessful}")
                view.onPostInfoChangeImgSuccess(response.body() as InfoChangeImgResponse)
                Log.d("Success","-----통신성공-----")
                Log.d("code",response.code().toString())
            }
            override fun onFailure(call: Call<InfoChangeImgResponse>, t: Throwable) {
                view.onPostInfoChangeImgFailure(t.message?:"통신 오류")
                Log.d("Fail","-----통신실패-----")
            }
        })
    }
}