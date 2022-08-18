package com.example.smproject.src.main.ar

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.smproject.R
import com.example.smproject.config.BaseFragment
import com.example.smproject.databinding.FragmentArBinding
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.MapView
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.Overlay
import com.naver.maps.map.overlay.OverlayImage


class ArFragment : BaseFragment<FragmentArBinding>(FragmentArBinding::bind,R.layout.fragment_ar), OnMapReadyCallback {
    lateinit var mapViewAr: MapView //레이아웃의 MapView와 연결
    private lateinit var naverMapAr:NaverMap //네이버 맵관련 기능 구현 용도
    private var latitude = 37.602 //위도
    private var longtitude = 126.955 //경도
    private var zoom = 16.2 //줌 레벨
    private var tilt = 45.0//기울임
    private val marker1:Marker = Marker()
    private val marker2:Marker = Marker()
    private val marker3:Marker = Marker()
    private lateinit var cameraPos: CameraPosition
    fun newInstance(): Fragment {
        return ArFragment()
    }
    //게시물 사진:base64, 본문:String, 공유여부:Boolean, 위도,경도 : String

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //네이버 지도 생성
        mapViewAr = binding.arMap
        mapViewAr.onCreate(savedInstanceState)
        mapViewAr.getMapAsync(this)
        binding.arMapBtn.setOnClickListener {
            binding.arCamBtn.visibility = View.VISIBLE
            binding.arMapBtn.visibility = View.GONE
        }
        binding.arCamBtn.setOnClickListener {
            binding.arMapBtn.visibility = View.VISIBLE
            binding.arCamBtn.visibility = View.GONE
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

        setMark(marker1, latitude, longtitude, R.drawable.ar_map_marker_test)
        marker1.onClickListener = Overlay.OnClickListener {
            showCustomToast("마커 1 클릭")
            false
        }
        setMark(marker2, latitude+0.0002, longtitude+0.0002, R.drawable.ar_map_marker_photo)
        marker2.onClickListener = Overlay.OnClickListener {
            showCustomToast("마커 2 클릭")
            false
        }
        setMark(marker3, latitude+0.0001, longtitude+0.0001, R.drawable.ar_map_marker_test)
        marker3.onClickListener = Overlay.OnClickListener {
            showCustomToast("마커 3 클릭")
            false
        }
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
}
