package com.example.smproject.src.main.ar

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import com.example.smproject.R
import com.example.smproject.config.BaseActivity
import com.example.smproject.databinding.ActivityArBinding
import com.example.smproject.src.main.MainActivity
import com.example.smproject.src.main.ar.models.Place
import com.example.smproject.src.main.getPostApi.GetPostListService
import com.example.smproject.src.main.getPostApi.GetPostListView
import com.example.smproject.src.main.getPostApi.models.GetPostListRequest
import com.example.smproject.src.main.getPostApi.models.GetPostListResonse
import com.example.smproject.util.CurrentLocation
import com.google.android.gms.maps.model.MarkerOptions
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.ux.ArFragment


class ArActivity : BaseActivity<ActivityArBinding>(ActivityArBinding::inflate), GetPostListView {
    private val TAG = "ArActivity" //로그 찍을 때 쓰려고 만든 태그
    private var places: List<Place>? = null //models에 보면 Place data class생성했음.
    var lat:Double =0.0
    var lng:Double = 0.0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //OpenGL ES 지원기기인지 체크
        if(!isSupportedDevice()){
            return
        }

        // Load model.glb from assets folder or http url
//        findFragmentById(R.id.arFragment) as ArFragment?)?.setOnTapPlaneGlbModel()
//        (findFragmentById() as ArFragment).setOnTapPlaneGlbModel("model.glb")
        val arFragment = (supportFragmentManager.findFragmentById(R.id.arFragment) as ArFragment)
        arFragment.setOnTapArPlaneListener{hitResult,_,_->
            val anchor = hitResult.createAnchor()
            val anchorNode = AnchorNode(anchor)
            anchorNode.setParent(arFragment.arSceneView.scene)


        }

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
    private fun isSupportedDevice(): Boolean {
        val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val openGlVersionString = activityManager.deviceConfigurationInfo.glEsVersion
        if (openGlVersionString.toDouble() < 3.0) {
            Toast.makeText(this, "Sceneform requires OpenGL ES 3.0 or later", Toast.LENGTH_LONG)
                .show()
            finish()
            return false
        }
        return true
    }

    /// 장소 추가하는 함수인데, 일단 CurrentLocation클래스로부터 현재 위치 얻어옴!
    //post에서 쓴 거랑 똑같음.
    private fun addPlaces(anchorNode: AnchorNode) {
        lat = CurrentLocation(this).returnLocation().first
        lng = CurrentLocation(this).returnLocation().second
//        val currentLocation = currentLocation
//        if (currentLocation == null) {
//            Log.w(TAG, "Location has not been determined yet")
//            return
//        }
        val places = places
        if (places == null) {
            Log.w(TAG, "No places to put")
            return
        }

        for (place in places) {
            // Add the place in AR
            val placeNode = PlaceNode(this, place)
            placeNode.setParent(anchorNode)
            // TODO set localPosition
            placeNode.setOnTapListener { _, _ ->
                showInfoWindow(place)
            }

            // Add the place in maps
            map?.let {
                val marker = it.addMarker(
                    MarkerOptions()
                        .position(place.geometry.location.latLng)
                        .title(place.name)
                )
                marker.tag = place
                markers.add(marker)
            }
        }
    }

}