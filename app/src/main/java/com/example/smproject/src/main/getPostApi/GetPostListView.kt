package com.example.smproject.src.main.getPostApi

import com.example.smproject.src.main.getPostApi.models.GetPostListResonse

interface GetPostListView {
    fun onGetPostListSuccess(response:GetPostListResonse)
    fun onGetPostListFailure(message:String)
}