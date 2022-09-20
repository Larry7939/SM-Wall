package com.example.smproject.src.main.ar

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import com.example.smproject.R
import com.example.smproject.ar.codelabs.hellogeospatial.HelloGeoActivity
import com.example.smproject.config.ApplicationClass
import com.example.smproject.config.BaseFragment
import com.example.smproject.databinding.FragmentMyarBinding
import com.example.smproject.src.main.MainActivity
import com.example.smproject.src.main.getPostApi.GetPostListService
import com.example.smproject.src.main.getPostApi.GetPostListView
import com.example.smproject.src.main.getPostApi.models.GetPostListRequest
import com.example.smproject.src.main.getPostApi.models.GetPostListResonse
import com.example.smproject.src.main.getPostApi.models.Post
import com.example.smproject.util.CurrentLocation
import com.example.smproject.util.PostedDialog
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.Overlay
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.util.FusedLocationSource



class MyArFragment : BaseFragment<FragmentMyarBinding>(FragmentMyarBinding::bind,R.layout.fragment_myar), OnMapReadyCallback,GetPostListView {
    lateinit var mapViewAr: MapView //레이아웃의 MapView와 연결
    private lateinit var naverMapAr:NaverMap //네이버 맵관련 기능 구현 용도
    private var zoom = 16.2 //줌 레벨
    private var tilt = 0.0//기울임
    private lateinit var locationSource:FusedLocationSource
    private val LOCATION_PERMISSION_REQUEST_CODE = 1000
    private lateinit var cameraPos: CameraPosition
    private lateinit var postedDialog:PostedDialog
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
            GetPostListService(this).tryGetPostList(GetPostListRequest("getPostList",null,null,null,null,null))
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

        //cam버튼 누르면 ar카메라 모드로 변경
        binding.arCamBtn.setOnClickListener {
            binding.arMapBtn.visibility = View.VISIBLE
            binding.arCamBtn.visibility = View.GONE
            startActivity(Intent(activity, HelloGeoActivity::class.java))
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
        GetPostListService(this).tryGetPostList(GetPostListRequest("getPostList",null,null,null,null,0))


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
        postedDialog = PostedDialog(context as MainActivity)

        //새로고침하면 게시물 목록을 다시 받아오면서 map에서 marker를 지우고 markerList를 초기화
        if (markerList.isNotEmpty()) {
            for (i in markerList) {
                i.map = null
            }
            markerList.clear()
        }

        Log.d("게시물 목록", "")
        locationList = response.data.list
//        for(i in locationList.iterator()){
//            Log.d("${i.id}","${i.locationObj.lat}, ${i.locationObj.lng}")
//        }
        for (i in locationList.iterator()) {
            if (i.withImage) {
                setMark(
                    i.id,
                    Marker(),
                    i.locationObj.lat.toDouble(),
                    i.locationObj.lng.toDouble(),
                    R.drawable.ar_map_marker_text_custom
                )
            } else if (!i.withImage) {
                setMark(
                    i.id,
                    Marker(),
                    i.locationObj.lat.toDouble(),
                    i.locationObj.lng.toDouble(),
                    R.drawable.ar_map_marker_photo_custom
                )
            }

            //각 marker에 리스너 설정
            for (i in 0 until markerList.size) {
                markerList[i].onClickListener = Overlay.OnClickListener {
                    showCustomToast("${markerList[i].tag}")

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
    }
    override fun onGetPostListFailure(message: String) {
        Log.d("게시물 목록 요청 실패",message)
    }

}
