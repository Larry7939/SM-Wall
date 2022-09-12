package com.example.smproject.src.main.search

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import com.example.smproject.R
import com.example.smproject.config.ApplicationClass
import com.example.smproject.config.BaseFragment
import com.example.smproject.databinding.FragmentSearchBinding
import com.example.smproject.src.main.getPostApi.GetPostListService
import com.example.smproject.src.main.getPostApi.GetPostListView
import com.example.smproject.src.main.getPostApi.models.GetPostListRequest
import com.example.smproject.src.main.getPostApi.models.GetPostListResonse
import com.example.smproject.src.main.getPostApi.models.Post
import com.example.smproject.util.CurrentLocation
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.util.FusedLocationSource

class SearchFragment : BaseFragment<FragmentSearchBinding>(FragmentSearchBinding::bind,R.layout.fragment_search), OnMapReadyCallback, GetPostListView {

    lateinit var mapViewSearch:MapView //레이아웃의 MapView와 연결
    private lateinit var naverMapSearch:NaverMap //네이버 맵관련 기능 구현 용도
    private var zoom = 16.2 //줌 레벨
    private var tilt = 0.0//기울임
    private lateinit var locationSource:FusedLocationSource
    private val LOCATION_PERMISSION_REQUEST_CODE = 1000
    private lateinit var cameraPos:CameraPosition
    //Post는 게시물 id,위치정보가 담긴 response model임.
    private lateinit var locationList:ArrayList<Post>
    fun newInstance(): Fragment {
        return SearchFragment()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        locationSource = FusedLocationSource(this,LOCATION_PERMISSION_REQUEST_CODE) //현재 위치 나타내기 위한 locationSource

        //새로고침버튼을 누르면 게시물을 받아옴.
        binding.searchFabRefresh.setOnClickListener{
            //게시물 목록 API
            showCustomToast("게시물 새로고침 완료")
            GetPostListService(this).tryGetPostList(GetPostListRequest("getPostList"))
        }
        //현위치 추적버튼을 누르면 현위치 갱신 및 지도 위치 변경
        binding.searchFabTracking.setOnClickListener {
            CurrentLocation(requireContext()).returnLocation()
            cameraPos = CameraPosition(LatLng(ApplicationClass.latitude, ApplicationClass.longtidute), zoom, tilt, 0.0)
            naverMapSearch.cameraPosition = cameraPos
        }

        mapViewSearch = binding.searchMap
        mapViewSearch.onCreate(savedInstanceState)
        mapViewSearch.getMapAsync(this)

        GetPostListService(this).tryGetPostList(GetPostListRequest("getPostList"))
    }

    override fun onMapReady(p0: NaverMap) {
        naverMapSearch = p0
        //지도 타입 선택
        naverMapSearch.mapType = NaverMap.MapType.Basic
        //건물 표시
        naverMapSearch.setLayerGroupEnabled("Building" ,true)
        //위치 및 각도 조정
        cameraPos = CameraPosition(LatLng(ApplicationClass.latitude, ApplicationClass.longtidute), zoom,tilt, 0.0)
        naverMapSearch.cameraPosition = cameraPos

        naverMapSearch.locationSource = locationSource //현재위치 표시
        naverMapSearch.locationTrackingMode = LocationTrackingMode.Follow

        //게시물 목록 API
        GetPostListService(this).tryGetPostList(GetPostListRequest("getPostList"))
    }
    //MapView를 Fragment에서 사용할 때에는 생명주기에 맞춰 각각 onViewCreated와 onDestroy에서 super함수를 호출해줘야한다.
    override fun onResume() {
        super.onResume()
        mapViewSearch.onResume()
    }
    override fun onPause() {
        super.onPause()
        mapViewSearch.onPause()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        mapViewSearch.onDestroy()
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
        marker.map = naverMapSearch
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