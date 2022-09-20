package com.example.smproject.src.main.search

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.smproject.R
import com.example.smproject.config.ApplicationClass
import com.example.smproject.config.BaseFragment
import com.example.smproject.databinding.FragmentSearchBinding
import com.example.smproject.src.main.MainActivity
import com.example.smproject.src.main.getPostApi.GetPostListService
import com.example.smproject.src.main.getPostApi.GetPostListView
import com.example.smproject.src.main.getPostApi.models.GetPostListRequest
import com.example.smproject.src.main.getPostApi.models.GetPostListResonse
import com.example.smproject.src.main.getPostApi.models.Post
import com.example.smproject.src.main.posted.PostedView
import com.example.smproject.src.main.posted.models.PostedResponse
import com.example.smproject.util.CurrentLocation
import com.example.smproject.util.PostedDialog
import com.example.smproject.util.SearchFilterDialog
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.Overlay
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.util.FusedLocationSource


class SearchFragment : BaseFragment<FragmentSearchBinding>(FragmentSearchBinding::bind,R.layout.fragment_search), OnMapReadyCallback, GetPostListView, PostedView {

    lateinit var mapViewSearch:MapView //레이아웃의 MapView와 연결
    private var naverMapSearch:NaverMap?=null //네이버 맵관련 기능 구현 용도
    private var zoom = 16.2 //줌 레벨
    private var tilt = 0.0//기울임
    private lateinit var locationSource:FusedLocationSource
    private val LOCATION_PERMISSION_REQUEST_CODE = 1000
    private lateinit var cameraPos:CameraPosition
    //Post는 게시물 id,위치정보가 담긴 response model임.
    private lateinit var locationList:ArrayList<Post>
    private var markerList:ArrayList<Marker> = arrayListOf()
    private lateinit var postedDialog: PostedDialog
    private lateinit var searchFilterDialog: SearchFilterDialog


    fun newInstance(): Fragment {
        return SearchFragment()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        locationSource = FusedLocationSource(this,LOCATION_PERMISSION_REQUEST_CODE) //현재 위치 나타내기 위한 locationSource
        searchFilterDialog = SearchFilterDialog(context as MainActivity)

        //새로고침버튼을 누르면 게시물을 받아옴.
        binding.searchFabRefresh.setOnClickListener{
            //게시물 목록 API
            if(binding.searchBoxMy.isChecked){
                //클릭 한 결과 박스가 체크되어있으면, 내 게시물만 받아온다.(userCreatedPost가 1인 것) isPrivate은 상관없음.
                GetPostListService(this).tryGetPostList(GetPostListRequest("getPostList",null,null,null,1,null))
            }
            else{
                //전체공개(isPrivate 비공개 여부가 0인 것들만! userCreatedPost는 상관없음.
                GetPostListService(this).tryGetPostList(GetPostListRequest("getPostList",null,null,null,null,0))
            }
            showCustomToast("게시물 새로고침 완료")

        }
        //현위치 추적버튼을 누르면 현위치 갱신 및 지도 위치 변경
        binding.searchFabTracking.setOnClickListener {
            CurrentLocation(requireContext()).returnLocation()
            cameraPos = CameraPosition(LatLng(ApplicationClass.latitude, ApplicationClass.longtidute), zoom, tilt, 0.0)
            naverMapSearch?.cameraPosition  = cameraPos
        }

        mapViewSearch = binding.searchMap
        mapViewSearch.onCreate(savedInstanceState)
        mapViewSearch.getMapAsync(this)

        if(binding.searchBoxMy.isChecked){
            //클릭 한 결과 박스가 체크되어있으면, 내 게시물만 받아온다.(userCreatedPost가 1인 것) isPrivate은 상관없음.
            GetPostListService(this).tryGetPostList(GetPostListRequest("getPostList",null,null,null,1,null))
        }
        else{
            //전체공개(isPrivate 비공개 여부가 0인 것들만! userCreatedPost는 상관없음.
            GetPostListService(this).tryGetPostList(GetPostListRequest("getPostList",null,null,null,null,0))
        }
        checkBoxMy()
        searchPost()
        //검색 필터버튼 -> 다이얼로그
        binding.searchFilter.setOnClickListener {
            searchFilterDialog.create()
            searchFilterDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            searchFilterDialog.window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            searchFilterDialog.show()
        }
    }
    override fun onMapReady(p0: NaverMap) {
        naverMapSearch = p0
        //지도 타입 선택
        naverMapSearch!!.mapType = NaverMap.MapType.Basic
        //건물 표시
        naverMapSearch!!.setLayerGroupEnabled("Building" ,true)
        //위치 및 각도 조정
        cameraPos = CameraPosition(LatLng(ApplicationClass.latitude, ApplicationClass.longtidute), zoom,tilt, 0.0)
        naverMapSearch!!.cameraPosition = cameraPos

        naverMapSearch!!.locationSource = locationSource //현재위치 표시
        naverMapSearch!!.locationTrackingMode = LocationTrackingMode.Follow
        //게시물 목록 API
        GetPostListService(this).tryGetPostList(GetPostListRequest("getPostList",null,null,null,null,null))
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
        if(naverMapSearch!=null){
            marker.map = naverMapSearch
        }
    }
    override fun onGetPostListSuccess(response: GetPostListResonse) {
        postedDialog = PostedDialog(context as MainActivity)

        //새로고침하면 게시물 목록을 다시 받아오면서 map에서 marker를 지우고 markerList를 초기화
        if(markerList.isNotEmpty()){
            for(i in markerList){
                i.map = null
            }
            markerList.clear()
        }

        Log.d("게시물 목록","")
        locationList = response.data.list
//        for(i in locationList.iterator()){
//            Log.d("${i.id}","${i.locationObj.lat}, ${i.locationObj.lng}")
//        }
        for(i in locationList.iterator()){
            if(i.withImage){
                setMark(i.id,Marker(),i.locationObj.lat.toDouble(),i.locationObj.lng.toDouble(),R.drawable.ar_map_marker_text_custom)
            }
            else if(!i.withImage){
                setMark(i.id,Marker(),i.locationObj.lat.toDouble(),i.locationObj.lng.toDouble(),R.drawable.ar_map_marker_photo_custom)
            }

        }
        //각 marker에 리스너 설정
        for (i in 0 until markerList.size) {
            markerList[i].onClickListener = Overlay.OnClickListener {
                showCustomToast("${markerList[i].tag }")

                ApplicationClass.postedId = markerList[i].tag.toString()
                postedDialog.create()
                postedDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                postedDialog.window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)

                //        동작은 되지만 꺼질 때의 애니메이션이 미작동
                (postedDialog.window!!.decorView as ViewGroup)
                    .getChildAt(0).startAnimation(
                        AnimationUtils.loadAnimation(
                            context, R.anim.open
                        )
                    )
                postedDialog.show()
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
        Log.d("게시물 정보 요청","성공")
    }
    override fun onPostedFailure(message: String) {
        Log.d("게시물 정보 요청 실패",message)
    }

    //내 게시물 조회 체크박스
    private fun checkBoxMy(){
        binding.searchBoxMy.setOnClickListener {
            if(binding.searchBoxMy.isChecked){
                //클릭 한 결과 박스가 체크되어있으면, 내 게시물만 받아온다.(userCreatedPost가 1인 것) isPrivate은 상관없음.
                GetPostListService(this).tryGetPostList(GetPostListRequest("getPostList",null,null,null,1,null))
            }
            else{
                //전체공개(isPrivate 비공개 여부가 0인 것들만! userCreatedPost는 상관없음.
                GetPostListService(this).tryGetPostList(GetPostListRequest("getPostList",null,null,null,null,null))
            }
        }
    }
    //게시물 검색
    private fun searchPost(){
        //keyword(#이 붙어 있으면 해시태그에서만 검색, #이 붙지 않으면 내용과 해시태그에서 검색)
        binding.searchInputIv.setOnClickListener {
            showCustomToast("검색 완료")
            val keyword = binding.searchInputEt.text.toString()
            if(binding.searchBoxMy.isChecked) {
                GetPostListService(this).tryGetPostList(GetPostListRequest("getPostList", null, keyword, null, 1, null))
            }
            //전체공개로 되어있는 게시물만 검색
            else{
                GetPostListService(this).tryGetPostList(GetPostListRequest("getPostList", null, keyword, null, null, 0))
            }
            //검색이 끝나면 검색창의 텍스트 clear
            binding.searchInputEt.text?.clear()
        }
        binding.searchInputEt.setOnEditorActionListener{textView,action,event ->
            var handled = false
            if (event.keyCode == KeyEvent.KEYCODE_ENTER){
                val keyword = binding.searchInputEt.text.toString()
                if(binding.searchBoxMy.isChecked) {
                    GetPostListService(this).tryGetPostList(GetPostListRequest("getPostList", null, keyword, null, 1, null))
                }
                //전체공개로 되어있는 게시물만 검색
                else{
                    GetPostListService(this).tryGetPostList(GetPostListRequest("getPostList", null, keyword, null, null, 0))
                }
                //검색이 끝나면 검색창의 텍스트 clear
                binding.searchInputEt.text?.clear()
                showCustomToast("검색 완료")
                handled = true
            }
            handled
        }


    }

    //기간 설정
    fun searchDays(days:Int){
        if(binding.searchBoxMy.isChecked) {
            GetPostListService(this).tryGetPostList(GetPostListRequest("getPostList", days, null, null, 1, null))
        }
        else{
            GetPostListService(this).tryGetPostList(GetPostListRequest("getPostList", days, null, null, null, 0))
        }
    }


}