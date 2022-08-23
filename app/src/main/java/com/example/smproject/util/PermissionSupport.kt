package com.example.smproject.util

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.annotation.NonNull
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class PermissionSupport(activity:Activity, context:Context) {     //생성자에서 Activity와 Context를 파라미터로 받는다.
    //요청할 권한 배열 저장
    private var activity = activity
    private var context = context
    private val permissions = arrayListOf<String>(

        android.Manifest.permission.ACCESS_COARSE_LOCATION,
        android.Manifest.permission.ACCESS_FINE_LOCATION,
        android.Manifest.permission.READ_EXTERNAL_STORAGE,
        android.Manifest.permission.CAMERA)
    private lateinit var permissionList:ArrayList<String>

    //권한 요청 시 발생하는 창에 대한 결과값을 받기 위해 지정해주는 int형
    //원하는 임의의 숫자 지정
    private val MULTIPLE_PERMISSIONS:Int = 1023 //요청에 대한 결과값 확인을 위해 RequestCode를 final로 정의

    //배열로 선언한 권한 중 허용되지 않은 권한 있는지 체크
    fun checkPermission():Boolean{
        var result:Int
        permissionList = arrayListOf()
        for(pm in permissions){ //권한 배열을 돌면서 검사
            result = ContextCompat.checkSelfPermission(context,pm)
            if(result!= PackageManager.PERMISSION_GRANTED){ //승인된 권한이 아니라면 해당 권한을 permissionList에 add
                permissionList.add(pm)
            }
        }
        if (permissionList.isNotEmpty()) { //승인되지 않은 권한이 존재한다면 false 반환
            return false
        }
        return true //모든 권한이 승인되었다면 true 반환
    }
    //배열로 선언한 권한에 대해 사용자에게 허용 요청
    fun requestPermission(){
        ActivityCompat.requestPermissions(activity,
            permissionList.toArray(arrayOfNulls<String>(permissionList.size)),MULTIPLE_PERMISSIONS)
    }
    //요청한 권한에 대한 결과값 판단 및 처리
    fun permissionResult(requestCode:Int, @NonNull permissions: Array<out String>, @NonNull grantResults: IntArray):Boolean{
        //우선 request가 아까 위에 선언한 MULTIPLE_PERMISSIONS와 숫자가 맞는지, 결과값의 길이가 0보다는 긴지를 체크
        if(requestCode == MULTIPLE_PERMISSIONS && (grantResults.isNotEmpty())){
            for(element in grantResults){
                //grantResults가 0이면 사용자가 허용한 것/ -1이면 거부한 것
                //-1이 있는지 체크하여 하나라도 -1이 나온다면 false를 리턴
                if(element ==-1){
                    return false
                }
            }
        }
        return true
    }
}