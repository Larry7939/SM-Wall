package com.example.smproject.src.main.ar
//
//
//import android.content.Intent
//import android.os.Bundle
//import android.util.Log
//import android.view.View
//import com.example.smproject.R
//import com.example.smproject.config.BaseActivity
//import com.example.smproject.databinding.ActivityArBinding
//import com.example.smproject.src.main.MainActivity
//import com.example.smproject.src.main.getPostApi.GetPostListService
//import com.example.smproject.src.main.getPostApi.GetPostListView
//import com.example.smproject.src.main.getPostApi.models.GetPostListRequest
//import com.example.smproject.src.main.getPostApi.models.GetPostListResonse
////import com.google.ar.sceneform.ux.ArFragment
//
//class ArActivity : BaseActivity<ActivityArBinding>(ActivityArBinding::inflate), GetPostListView {
//    var lat:Double =0.0
//    var lng:Double = 0.0
//    //Ar기능 관련 변수들
////    private lateinit var arFragment:ArFragment
////    private lateinit var placesService: PlacesService
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        // Load model.glb from assets folder or http url
////        findFragmentById(R.id.arFragment) as ArFragment?)?.setOnTapPlaneGlbModel()
////        (findFragmentById() as ArFragment).setOnTapPlaneGlbModel("model.glb")
//        //ar카메라 실행 코드. 이 코드가 없으면 arActivity로의 전환이 불가능
////        arFragment = (supportFragmentManager.findFragmentById(R.id.arFragment) as ArFragment)
//
////        placesService = PlacesService.create()

//
//        //지도 버튼을 누르면 MainActivity로 이동
//        binding.arMapBtn.setOnClickListener {
//            binding.arMapBtn.visibility = View.GONE
//            val intent = Intent(this,MainActivity::class.java)
//            intent.apply { putExtra("","") }
//            startActivity(intent)
//        }
//        //게시물 목록 API
//        GetPostListService(this).tryGetPostList(GetPostListRequest("getPostList"))
//    }
//    //일단은 게시물 목록 로그만 찍는 역할
//    override fun onGetPostListSuccess(response: GetPostListResonse) {
//        Log.d("ArFragment GetPostList-code","${response.data.code}")
//        for(i in response.data.list.iterator()){
//            Log.d("${i.id}","${i.locationObj.lat}, ${i.locationObj.lng}")
//        }
//    }
//    override fun onGetPostListFailure(message: String) {
//        Log.d("게시물 목록 요청 실패",message)
//    }
//
//
//
//
//
//}