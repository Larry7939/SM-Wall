package com.example.smproject.ar.codelabs.hellogeospatial.helpers

import com.example.smproject.ar.codelabs.hellogeospatial.HelloGeoRenderer
import com.example.smproject.src.main.getPostApi.GetPostListView
import com.example.smproject.src.main.getPostApi.models.GetPostListResonse
import android.util.Log


class GetPostListArView(val renderer: HelloGeoRenderer) : GetPostListView{
    override fun onGetPostListSuccess(response: GetPostListResonse) {
        Log.d("여기까지", "")
        renderer.onGetPostListSuccess(response)
    }
    override fun onGetPostListFailure(message:String) {
        Log.d("여기까지 실패", "")
        renderer.onGetPostListFailure(message)
    }

}