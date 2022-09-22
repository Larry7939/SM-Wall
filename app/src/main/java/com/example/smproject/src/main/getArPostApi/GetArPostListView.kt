package com.example.smproject.src.main.getArPostApi

import com.example.smproject.src.main.getArPostApi.models.GetArPostListResponse

interface GetArPostListView {
    fun onGetArPostListSuccess(response: GetArPostListResponse)
    fun onGetArPostListFailure(message:String)
}