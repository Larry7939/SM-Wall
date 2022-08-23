package com.example.smproject.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*

class CurrentLocation(context: Context){
    private var context = context
    private var mFusedLocationProviderClient: FusedLocationProviderClient? = null // 현재 위치를 가져오기 위한 변수
    lateinit var mLastLocation: Location // 위치 값을 가지고 있는 객체
    private var lng:Double = 0.0
    private var lat:Double = 0.0
    private lateinit var mLocationRequest: LocationRequest // 위치 정보 요청의 매개변수를 저장하는 변수
//    private val REQUEST_PERMISSION_LOCATION = 10
    fun returnLocation(): Pair<Double, Double> {
        mLocationRequest =  LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        startLocationUpdates()
        return Pair(lat,lng)
    }

    private fun startLocationUpdates() {
        //FusedLocationProviderClient의 인스턴스를 생성.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
        //위치권한 체크
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(context,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        // 기기의 위치에 관한 정기 업데이트를 요청하는 메서드 실행
        // 지정한 루퍼 스레드(Looper.myLooper())에서 콜백(mLocationCallback)으로 위치 업데이트를 요청
        mFusedLocationProviderClient!!.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper())
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
        Toast.makeText(context, "위도 : ${mLastLocation.latitude}\n경도 : ${mLastLocation.longitude}", Toast.LENGTH_SHORT).show()
        lat = mLastLocation.latitude
        lng = mLastLocation.longitude
    }


//    // 위치 권한이 있는지 확인하는 메서드
//    private fun checkPermissionForLocation(context: Context): Boolean {
//        // Android 6.0 Marshmallow 이상에서는 위치 권한에 추가 런타임 권한이 필요
//        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//                true
//            } else {
//                // 권한이 없으므로 권한 요청 알림 보내기
//                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_PERMISSION_LOCATION)
//                false
//            }
//        } else {
//            true
//        }
//    }
    // 사용자에게 권한 요청 후 결과에 대한 처리 로직
//    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (requestCode == REQUEST_PERMISSION_LOCATION) {
//            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                startLocationUpdates()
//
//            } else {
//                Log.d("ttt", "onRequestPermissionsResult() _ 권한 허용 거부")
//                Toast.makeText(this, "권한이 없어 해당 기능을 실행할 수 없습니다.", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }


































//        context: Context, serviceClass: Class<*>){
//        val locationManager = getSystemService(context,serviceClass) as LocationManager?
//        //GPS 프로바이더 사용가능 여부(정확하지만 실내에선 X)
//        val isGPSEnabled = locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER)
//        //네트워크 프로바이더(부정확하지만 실내에서도 O)
//        val isNetworkEnabled = locationManager?.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
//        Log.d("GPS사용가능 여부", "isGPSEnabled=$isGPSEnabled");
//        Log.d("Network사용가능 여부", "isNetworkEnabled=$isNetworkEnabled");
//
//        Toast.makeText(context,"GPS사용가능 여부: isGPSEnabled=$isGPSEnabled + Network사용가능 여부: isGPSEnabled=$isNetworkEnabled", Toast.LENGTH_SHORT).show()
//
//
//
//        val locationListener:LocationListener = object:LocationListener {
//            override fun onLocationChanged(location:Location){
//                var lat:Double = location.latitude
//                var lng:Double = location.longitude
//                Log.d("위도 경도 변화: ","latitude:${lat}, longtitude:${lng}")
//            }
//            override fun onStatusChanged(provider:String, status:Int, extras: Bundle) {
//                Log.d("","onStatusChanged")
//            }
//
//            override fun onProviderEnabled(provider:String ) {
//                Log.d("","onProviderEnabled")
//            }
//
//            override fun onProviderDisabled(provider:String ) {
//                Log.d("","onProviderDisabled")
//            }
//        }
//
//        if (ActivityCompat.checkSelfPermission(
//                context,
//                Manifest.permission.ACCESS_FINE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
//                context,
//                Manifest.permission.ACCESS_COARSE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return
//        }
//
//        //LocationManager 에 로케이션 모니터링을 요청
//        locationManager?.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0,0.0f,locationListener)
//        locationManager?.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0.0f,locationListener)
//
//
//        val locationProvider = LocationManager.GPS_PROVIDER
//        val lastKnownLocation = locationManager?.getLastKnownLocation(locationProvider)
//        if(lastKnownLocation!=null){
//            var lng:Double = lastKnownLocation.longitude
//            var lat:Double = lastKnownLocation.latitude
//            Log.d("Main","longtitude=$lng latitude=$lat")
//
//        }
//    }
//
//

}