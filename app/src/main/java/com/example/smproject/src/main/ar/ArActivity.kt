package com.example.smproject.src.main.ar

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.smproject.R
import com.example.smproject.config.BaseActivity
import com.example.smproject.databinding.ActivityArBinding
import com.example.smproject.src.main.MainActivity
import com.example.smproject.src.main.ar.models.NearbyPlacesResponse
import com.example.smproject.src.main.ar.models.Place
import com.example.smproject.src.main.getPostApi.GetPostListService
import com.example.smproject.src.main.getPostApi.GetPostListView
import com.example.smproject.src.main.getPostApi.models.GetPostListRequest
import com.example.smproject.src.main.getPostApi.models.GetPostListResonse
import com.example.smproject.util.CurrentLocation
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.ux.ArFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ArActivity : BaseActivity<ActivityArBinding>(ActivityArBinding::inflate), GetPostListView {
    private val TAG = "ArActivity" //로그 찍을 때 쓰려고 만든 태그
    var lat:Double =0.0
    var lng:Double = 0.0

    //Ar기능 관련 변수들
    private lateinit var arFragment:ArFragment
    private lateinit var placesService: PlacesService

    private var anchorNode: AnchorNode? = null
    private var markers: MutableList<Marker> = emptyList<Marker>().toMutableList()
    private var places: List<Place>? = null//models에 보면 Place data class생성했음.
    private var currentLocation: Location? = null
    private var map: GoogleMap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //OpenGL ES 지원기기인지 체크
//        if(!isSupportedDevice()){
//            return
//        }

        // Load model.glb from assets folder or http url
//        findFragmentById(R.id.arFragment) as ArFragment?)?.setOnTapPlaneGlbModel()
//        (findFragmentById() as ArFragment).setOnTapPlaneGlbModel("model.glb")
        arFragment = (supportFragmentManager.findFragmentById(R.id.arFragment) as ArFragment)
        placesService = PlacesService.create()
        setUpAr()

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
    //일단은 게시물 목록 로그만 찍는 역할
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

    ///장소 추가하는 함수인데, 일단 내가 만든 CurrentLocation 클래스로부터 현재 위치 얻어옴!
    //post에서 쓴 거랑 똑같음.
    private fun addPlaces(anchorNode: AnchorNode) {
        lat = CurrentLocation(this).returnLocation().first
        lng = CurrentLocation(this).returnLocation().second
//        val currentLocation = currentLocation
//        if (currentLocation == null) {
//            Log.w(TAG, "Location has not been determined yet")
//            return
//        }
        //배열 places가 비어있는지 체크
        //참고로 이 places는 getNearbyPlaces함수를 이용해서 가져온 것이다.
        //getNearbyPlaces에서는 Place API를 통해서 위도 경도를 주고 응답으로 Place를 받는다.
        val places = places
        if (places == null) {
            Log.w(TAG, "No places to put")
            return
        }
        //Place API를 통해서 받아온 places배열을 돌면서 각각을 placeNode로 만들어서 anchorNode를 parent로 한 다음, showInfoWindow함수를 호출한다.
        //showInfoWindow함수에서는 place를 인자로 받아서 각각 PlaceNode.kt의 showInfoWindow를 호출한다.
        //PlaceNode.kt의 showInfoWindow에서는 뷰의 visibility를 조정한다.
        for (place in places) {
            // Add the place in AR
            val placeNode = PlaceNode(this, place)
            placeNode.setParent(anchorNode)
            // t odo set localPosition
            placeNode.setOnTapListener { _, _ ->
                showInfoWindow(place)
            }

            // Add the place in maps 지도는 이미 했으니까 이부분은 주석처리하자.
//            map?.let {
//                val marker = it.addMarker(
//                    MarkerOptions()
//                        .position(place.geometry.location.latLng)
//                        .title(place.name)
//                )
//                marker.tag = place
//                markers.add(marker)
//            }
        }
    }
    private fun showInfoWindow(place: Place) {
        // Show in AR
        val matchingPlaceNode = anchorNode?.children?.filter {
            it is PlaceNode
        }?.first {
            val otherPlace = (it as PlaceNode).place ?: return@first false
            return@first otherPlace == place
        } as? PlaceNode
        matchingPlaceNode?.showInfoWindow()

        // Show as marker
        val matchingMarker = markers.firstOrNull {
            val placeTag = (it.tag as? Place) ?: return@firstOrNull false
            return@firstOrNull placeTag == place
        }
        matchingMarker?.showInfoWindow()
    }
//    이건 예제에서 나온 구글 지도랑 AR을 함께 보여주기 위해서 사용하는 건데, 나는 이미 MyArFragment에서 네이버 지도를 쓰고 있으니까 주석처리 해놓자.
//    private fun setUpMaps() {
//        mapFragment.getMapAsync { googleMap ->
//
//            getCurrentLocation {
//                val pos = CameraPosition.fromLatLngZoom(it.latLng, 13f)
//                googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(pos))
//                getNearbyPlaces(it)
//            }
//            googleMap.setOnMarkerClickListener { marker ->
//                val tag = marker.tag
//                if (tag !is Place) {
//                    return@setOnMarkerClickListener false
//                }
//                showInfoWindow(tag)
//                return@setOnMarkerClickListener true
//            }
//            map = googleMap
//        }
//    }


    private fun setUpAr() {
        Log.d("setUpAr 호출 - ","성공")
        getNearbyPlaces(LatLng(lat,lng))
        arFragment.setOnTapArPlaneListener { hitResult, _, _ ->
            val anchor = hitResult.createAnchor()
            val anchorNode = AnchorNode(anchor)
            anchorNode.setParent(arFragment.arSceneView.scene)
            addPlaces(anchorNode!!)
        }
    }

    //  서버랑 통신해서 위치를 받아오는 부분
//  PlaceApi에 위도 경도를 보내면 Place를 반환해준다!!!
    private fun getNearbyPlaces(latLng: LatLng) {
        Log.d("getNearbyPlaces 호출 - ","성공")
        val apiKey = this.getString(R.string.google_maps_key)
      placesService.nearbyPlaces(
        apiKey = apiKey,
        location = "${latLng.latitude},${latLng.longitude}",
        radiusInMeters = 2000,
        placeType = "park"
      ).enqueue(
           object : Callback<NearbyPlacesResponse> {
            override fun onFailure(call: Call<NearbyPlacesResponse>, t: Throwable) {
                Log.e(TAG, "Failed to get nearby places", t)
            }

            override fun onResponse(
                call: Call<NearbyPlacesResponse>,
                response: Response<NearbyPlacesResponse>
            ) {
                if (!response.isSuccessful) {
                    Log.e(TAG, "Failed to get nearby places")
                    return
                }

                val places = response.body()?.results ?: emptyList()
                this@ArActivity.places = places
            }
        }
    )
}
}