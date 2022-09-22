package com.example.smproject.ar.codelabs.hellogeospatial.helpers

import com.example.smproject.ar.codelabs.hellogeospatial.HelloGeoRenderer
//import com.example.smproject.src.main.getPostApi.GetPostListView
//import com.example.smproject.src.main.getPostApi.models.GetPostListResonse
import android.util.Log
import com.example.smproject.src.main.getArPostApi.GetArPostListView
import com.example.smproject.src.main.getArPostApi.models.GetArPostListResponse

class GetPostListArView(val renderer: HelloGeoRenderer) : GetArPostListView{
    override fun onGetArPostListSuccess(response: GetArPostListResponse) {
        Log.d("여기까지", "")
        renderer.onGetArPostListSuccess(response)
    }
    override fun onGetArPostListFailure(message:String) {
        Log.d("여기까지 실패", "")
        renderer.onGetArPostListFailure(message)
    }

}
//class GetPostListArView(val renderer: HelloGeoRenderer) : GetPostListView{
//    override fun onGetPostListSuccess(response: GetPostListResonse) {
//        Log.d("여기까지", "")
//        renderer.onGetPostListSuccess(response)
//    }
//    override fun onGetPostListFailure(message:String) {
//        Log.d("여기까지 실패", "")
//        renderer.onGetPostListFailure(message)
//    }
//
//}