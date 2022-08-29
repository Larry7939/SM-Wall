package com.example.smproject.src.main.ar

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.smproject.R
import com.example.smproject.config.BaseActivity
import com.example.smproject.databinding.ActivityArBinding
import com.example.smproject.src.main.MainActivity
import com.example.smproject.src.main.getPostApi.GetPostListService
import com.example.smproject.src.main.getPostApi.GetPostListView
import com.example.smproject.src.main.getPostApi.models.GetPostListRequest
import com.example.smproject.src.main.getPostApi.models.GetPostListResonse
import com.google.ar.core.Config
import com.google.ar.core.Session
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.ux.ArFragment
import com.naver.maps.geometry.LatLng

class ArActivity : BaseActivity<ActivityArBinding>(ActivityArBinding::inflate), GetPostListView {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Load model.glb from assets folder or http url
//        findFragmentById(R.id.arFragment) as ArFragment?)?.setOnTapPlaneGlbModel()
//        (findFragmentById() as ArFragment).setOnTapPlaneGlbModel("model.glb")
        val arFragment = (supportFragmentManager.findFragmentById(R.id.arFragment) as ArFragment)
        arFragment.setOnTapArPlaneListener{hitResult,_,_->
            val anchor = hitResult.createAnchor()
            val anchorNode = AnchorNode(anchor)
            anchorNode.parent = arFragment.arSceneView.scene
            
        }
//        var session = Session(this)
//        var config = Config(session)
//        session.configure(config)
//        session.close()
        //지도 버튼을 누르면 MainActivity로 이동
        binding.arMapBtn.setOnClickListener {
            binding.arMapBtn.visibility = View.GONE
            val intent = Intent(this,MainActivity::class.java)
            intent.apply { putExtra("","") }
            startActivity(intent)
        }
        //게시물 목록 API
        GetPostListService(this).tryGetPostList(GetPostListRequest("getPostList"))
    }
    override fun onGetPostListSuccess(response: GetPostListResonse) {
        Log.d("ArFragment GetPostList-code","${response.data.code}")
        for(i in response.data.list.iterator()){
            Log.d("${i.id}","${i.locationObj.lat}, ${i.locationObj.lng}")
        }
    }
    override fun onGetPostListFailure(message: String) {
        Log.d("게시물 목록 요청 실패",message)
    }
}