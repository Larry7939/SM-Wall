package com.example.smproject.src.main.ar

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import com.example.smproject.R
import com.example.smproject.config.BaseFragment
import com.example.smproject.databinding.FragmentMyarBinding
import com.example.smproject.src.main.getPostApi.GetPostListService
import com.example.smproject.src.main.getPostApi.GetPostListView
import com.example.smproject.src.main.getPostApi.models.GetPostListRequest
import com.example.smproject.src.main.getPostApi.models.GetPostListResonse
import com.example.smproject.src.main.getPostApi.models.Post
import com.example.smproject.util.CurrentLocation
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.MapView
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.unity3d.player.UnityPlayerActivity


class MyArFragment : BaseFragment<FragmentMyarBinding>(FragmentMyarBinding::bind,R.layout.fragment_myar), OnMapReadyCallback,GetPostListView {
    lateinit var mapViewAr: MapView //레이아웃의 MapView와 연결
    private lateinit var naverMapAr:NaverMap //네이버 맵관련 기능 구현 용도
    private lateinit var currentLocation: CurrentLocation
    private var latitude:Double=0.0//위도
    private var longtitude:Double=0.0 //경도
    private var zoom = 16.2 //줌 레벨
    private var tilt = 45.0//기울임

    private lateinit var cameraPos: CameraPosition

    //Post는 게시물 id,위치정보가 담긴 response model임.
    private lateinit var locationList:ArrayList<Post>

    fun newInstance(): Fragment {
        return MyArFragment()
    }
    //게시물 사진:base64, 본문:String, 공유여부:Boolean, 위도,경도 : String
    override fun onAttach(context: Context) {
        super.onAttach(context)
        currentLocation = CurrentLocation(context)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        latitude = currentLocation.returnLocation().first
        longtitude = currentLocation.returnLocation().second

        //네이버 지도 생성
        mapViewAr = binding.arMap
        mapViewAr.onCreate(savedInstanceState)
        mapViewAr.getMapAsync(this)
        binding.arMapBtn.setOnClickListener {
            binding.arCamBtn.visibility = View.VISIBLE
            binding.arMapBtn.visibility = View.GONE
        }
        //cam버튼 누르면 unity실행
        binding.arCamBtn.setOnClickListener {
            binding.arMapBtn.visibility = View.VISIBLE
            binding.arCamBtn.visibility = View.GONE
            startActivity(Intent(activity, UnityPlayerActivity::class.java))
        }

    }
    override fun onMapReady(p1: NaverMap) {


        naverMapAr = p1
        //지도 타입 선택
        naverMapAr.mapType = NaverMap.MapType.Basic
        //건물 표시
        naverMapAr.setLayerGroupEnabled("Building", true)
        //위치 및 각도 조정
        cameraPos = CameraPosition(LatLng(latitude, longtitude), zoom, tilt, 0.0)
        naverMapAr.cameraPosition = cameraPos

        //게시물 목록 API
        GetPostListService(this).tryGetPostList(GetPostListRequest("getPostList"))


    }
    override fun onResume() {
        super.onResume()
        mapViewAr.onResume()
    }
    override fun onPause() {
        super.onPause()
        mapViewAr.onPause()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        mapViewAr.onDestroy()
    }
    private fun setMark(marker: Marker, lat:Double, lng:Double, resourceId:Int){
        //원근감 표시
        marker.isIconPerspectiveEnabled = true
        //아이콘 지정
        marker.icon = OverlayImage.fromResource(resourceId)
        //마커의 투명도
        marker.alpha = 0.8f
        //마커 위치
        marker.position = LatLng(lat,lng)
        //마커 우선순위
        marker.zIndex = 10
        //마커 표시
        marker.map = naverMapAr
    }

    override fun onGetPostListSuccess(response: GetPostListResonse) {
        Log.d("게시물 목록","")
        locationList = response.data.list
        for(i in locationList.iterator()){
            Log.d("${i.id}","${i.locationObj.lat}, ${i.locationObj.lng}")
        }
        for(i in locationList.iterator()){
            setMark(Marker(),i.locationObj.lat.toDouble(),i.locationObj.lng.toDouble(),R.drawable.ar_map_marker_test)
        }
    }
    override fun onGetPostListFailure(message: String) {
        Log.d("게시물 목록 요청 실패",message)
    }
}
