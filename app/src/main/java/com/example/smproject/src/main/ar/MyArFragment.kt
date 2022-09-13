package com.example.smproject.src.main.ar

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import com.example.smproject.R
import com.example.smproject.config.ApplicationClass
import com.example.smproject.config.BaseFragment
import com.example.smproject.databinding.FragmentMyarBinding
import com.example.smproject.src.main.MainActivity
import com.example.smproject.src.main.getPostApi.GetPostListService
import com.example.smproject.src.main.getPostApi.GetPostListView
import com.example.smproject.src.main.getPostApi.models.GetPostListRequest
import com.example.smproject.src.main.getPostApi.models.GetPostListResonse
import com.example.smproject.src.main.getPostApi.models.Post
import com.example.smproject.src.main.posted.PostedFragment
import com.example.smproject.src.main.posted.PostedService
import com.example.smproject.src.main.posted.PostedView
import com.example.smproject.src.main.posted.models.PostedRequest
import com.example.smproject.src.main.posted.models.PostedResponse
import com.example.smproject.util.CurrentLocation
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.Overlay
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.util.FusedLocationSource
import com.unity3d.player.UnityPlayerActivity


class MyArFragment : BaseFragment<FragmentMyarBinding>(FragmentMyarBinding::bind,R.layout.fragment_myar), OnMapReadyCallback,GetPostListView,PostedView {
    lateinit var mapViewAr: MapView //레이아웃의 MapView와 연결
    private lateinit var naverMapAr:NaverMap //네이버 맵관련 기능 구현 용도
    private var zoom = 16.2 //줌 레벨
    private var tilt = 0.0//기울임
    private lateinit var locationSource:FusedLocationSource
    private val LOCATION_PERMISSION_REQUEST_CODE = 1000
    private lateinit var cameraPos: CameraPosition

    //Post는 게시물 id,위치정보가 담긴 response model임.
    private lateinit var locationList:ArrayList<Post>
    //set된 Marker들을 모아놓은 MarkerList
    private var markerList:ArrayList<Marker> = arrayListOf()


    fun newInstance(): Fragment {
        return MyArFragment()
    }
    //게시물 사진:base64, 본문:String, 공유여부:Boolean, 위도,경도 : String
    override fun onAttach(context: Context) {
        super.onAttach(context)
        CurrentLocation(requireContext()).returnLocation()
        Log.d("Ar 현재 위치","${ApplicationClass.latitude},${ApplicationClass.longtidute}")
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        locationSource = FusedLocationSource(this,LOCATION_PERMISSION_REQUEST_CODE) //현재 위치 나타내기 위한 locationSource

        //새로고침버튼을 누르면 게시물을 받아옴.
        binding.arFabRefresh.setOnClickListener{
            //게시물 목록 API
            showCustomToast("게시물 새로고침 완료")
            GetPostListService(this).tryGetPostList(GetPostListRequest("getPostList"))
            Log.d("MarkerList","$markerList")
            Log.d("MarkerList 첫번째 원소 id","${markerList[0].tag}")
            for(i in 0 until markerList.size){
                Log.d("게시물 id목록 ="," ${markerList[i].tag}}")
            }
        }

        //현위치 추적버튼을 누르면 현위치 갱신 및 지도 위치 변경
        binding.arFabTracking.setOnClickListener {
            CurrentLocation(requireContext()).returnLocation()
            cameraPos = CameraPosition(LatLng(ApplicationClass.latitude, ApplicationClass.longtidute), zoom, tilt, 0.0)
            naverMapAr.cameraPosition = cameraPos
        }
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
        CurrentLocation(requireContext()).returnLocation()

        naverMapAr = p1
        //지도 타입 선택
        naverMapAr.mapType = NaverMap.MapType.Basic
        //건물 표시
        naverMapAr.setLayerGroupEnabled("Building", true)
        //위치 및 각도 조정
        cameraPos = CameraPosition(LatLng(ApplicationClass.latitude, ApplicationClass.longtidute), zoom, tilt, 0.0)
        naverMapAr.cameraPosition = cameraPos
        naverMapAr.locationSource = locationSource //현재위치 표시
        naverMapAr.locationTrackingMode = LocationTrackingMode.Follow
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
    private fun setMark(id:Int,marker: Marker, lat:Double, lng:Double, resourceId:Int){
        //서버에서 받아온 게시물의 id를 tag에 넣음.
        marker.tag = id
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

        //위에서 만든 마커를 markerList에 추가
        markerList.add(marker)

        //마커 표시
        marker.map = naverMapAr
    }

    override fun onGetPostListSuccess(response: GetPostListResonse) {
        //새로고침하면 게시물 목록을 다시 받아오면서 map에서 marker를 지우고 markerList를 초기화
        if(markerList.isNotEmpty()){
            for(i in markerList){
                i.map = null
            }
            markerList.clear()
        }

        Log.d("게시물 목록","")
        locationList = response.data.list
        for(i in locationList.iterator()){
            Log.d("${i.id}","${i.locationObj.lat}, ${i.locationObj.lng}")
        }
        for(i in locationList.iterator()){
            setMark(i.id,Marker(),i.locationObj.lat.toDouble(),i.locationObj.lng.toDouble(),R.drawable.ar_map_marker_test)
        }

        //각 marker에 리스너 설정
        for (i in 0 until markerList.size) {
            markerList[i].onClickListener = Overlay.OnClickListener {
                showCustomToast("${markerList[i].tag }")
                PostedService(this).tryGetPosted(PostedRequest("getPostInfo",markerList[i].tag.toString()))
                false
            }
        }
    }
    override fun onGetPostListFailure(message: String) {
        Log.d("게시물 목록 요청 실패",message)
    }

    //tryGetPosted 호출 성공 시 PostedFragment로 화면 전환
    //같은 상위 Activity를 갖고있는 Fragment들간의 데이터 공유방법 -> Fragment Result API 활용
    override fun onPostedSuccess(response: PostedResponse) {

        val id = response.data.info.id.toString()
        val content = response.data.info.content
        //request키를 다르게 하면 여러개의 데이터를 줄 수 있다!!!!
        setFragmentResult("idKey",bundleOf("bundleKey" to id))
        setFragmentResult("contentKey",bundleOf("bundleKey" to content))

        //Fragment에서 Activity의 함수 호출방법, replaceFragment로 다른 Fragment로의 화면 전환
        (activity as MainActivity?)?.replaceFragment(PostedFragment())

        Log.d("게시물 정보 요청","성공")
    }
    override fun onPostedFailure(message: String) {
        Log.d("게시물 정보 요청 실패",message)

    }
}
