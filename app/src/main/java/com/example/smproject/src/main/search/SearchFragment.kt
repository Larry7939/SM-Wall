package com.example.smproject.src.main.search

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import com.example.smproject.R
import com.example.smproject.config.BaseFragment
import com.example.smproject.databinding.FragmentSearchBinding
import com.example.smproject.src.main.getPostApi.GetPostListService
import com.example.smproject.src.main.getPostApi.GetPostListView
import com.example.smproject.src.main.getPostApi.models.GetPostListRequest
import com.example.smproject.src.main.getPostApi.models.GetPostListResonse
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.MapView
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback

class SearchFragment : BaseFragment<FragmentSearchBinding>(FragmentSearchBinding::bind,R.layout.fragment_search), OnMapReadyCallback, GetPostListView {

    lateinit var mapViewSearch:MapView //레이아웃의 MapView와 연결
    private lateinit var naverMapSearch:NaverMap //네이버 맵관련 기능 구현 용도
    private var latitude = 37.602 //위도
    private var longtitude = 126.955 //경도
    private var zoom = 16.2 //줌 레벨
    private var tilt = 45.0//기울임
    private lateinit var cameraPos:CameraPosition
    fun newInstance(): Fragment {
        return SearchFragment()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
        cameraPos = CameraPosition(LatLng(latitude,longtitude), zoom,tilt, 0.0)
        naverMapSearch.cameraPosition = cameraPos
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

    override fun onGetPostListSuccess(response: GetPostListResonse) {
        Log.d("Search Fragment GetPostList-code","${response.data.code}")
        for(i in response.data.list.iterator()){
            Log.d("${i.id}","${i.locationObj.lat}, ${i.locationObj.lng}")
        }
    }

    override fun onGetPostListFailure(message: String) {
        Log.d("게시물 목록 요청 실패",message)
    }
}