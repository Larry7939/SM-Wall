package com.example.smproject.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.smproject.config.ApplicationClass
import com.google.android.gms.location.*

class CurrentLocation(context: Context) {
    private var context = context
    private var mFusedLocationProviderClient: FusedLocationProviderClient? =
        null // 현재 위치를 가져오기 위한 변수
    lateinit var mLastLocation: Location // 위치 값을 가지고 있는 객체
    private var lng: Double = 0.0
    private var lat: Double = 0.0
    private lateinit var mLocationRequest: LocationRequest // 위치 정보 요청의 매개변수를 저장하는 변수

    //    private val REQUEST_PERMISSION_LOCATION = 10
    fun returnLocation() {
        mLocationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        startLocationUpdates()
    }

    private fun startLocationUpdates() {
        //FusedLocationProviderClient의 인스턴스를 생성.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
        //위치권한 체크
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        // 기기의 위치에 관한 정기 업데이트를 요청하는 메서드 실행
        // 지정한 루퍼 스레드(Looper.myLooper())에서 콜백(mLocationCallback)으로 위치 업데이트를 요청
        mFusedLocationProviderClient!!.requestLocationUpdates(
            mLocationRequest,
            mLocationCallback,
            Looper.myLooper()
        )
    }

    // 시스템으로부터 위치 정보를 콜백으로 받음
    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            // 시스템에서 받은 location 정보를 onLocationChanged()에 전달
            locationResult.lastLocation
            onLocationChanged(locationResult.lastLocation)
        }
    }

    // 시스템으로 부터 받은 위치정보를 화면에 갱신해주는 메소드
    fun onLocationChanged(location: Location) {
        mLastLocation = location
//        Toast.makeText(context, "위도 : ${mLastLocation.latitude}\n경도 : ${mLastLocation.longitude}", Toast.LENGTH_SHORT).show()
        ApplicationClass.latitude = mLastLocation.latitude
        ApplicationClass.longtidute = mLastLocation.longitude
        Log.d(
            "CurrentLocation 현재 위치",
            "${ApplicationClass.latitude},${ApplicationClass.longtidute}"
        )

//        Log.d("CurrentLocation 현재 위치","${lat},${lng}")

    }
}





