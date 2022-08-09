package com.example.smproject.src.main.info

import com.example.smproject.src.main.info.models.InfoChangeImgResponse

interface InfoFragmentChangeImgView {
    fun onPostInfoChangeImgSuccess(response:InfoChangeImgResponse)
    fun onPostInfoChangeImgFailure(message:String)
}