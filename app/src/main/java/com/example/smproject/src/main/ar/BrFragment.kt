package com.example.smproject.src.main.ar

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import com.example.smproject.R
import com.example.smproject.config.BaseFragment
import com.example.smproject.databinding.FragmentArBinding
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.MapView
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback


class BrFragment : BaseFragment<FragmentArBinding>(FragmentArBinding::bind,R.layout.fragment_ar), OnMapReadyCallback {
    lateinit var mapViewAr: MapView //레이아웃의 MapView와 연결
    private lateinit var naverMapAr:NaverMap //네이버 맵관련 기능 구현 용도
    private var latitude = 37.602 //위도
    private var longtitude = 126.955 //경도
    private var zoom = 16.2 //줌 레벨
    private var tilt = 45.0//기울임
    private lateinit var cameraPos: CameraPosition

    var arFragment: BrFragment? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


    fun newInstance(): Fragment {
        return BrFragment()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arFragment = BrFragment()
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
            //arFragment!!.setOnTapPlaneGlbModel("Cappuccino_cup.glb")
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
}