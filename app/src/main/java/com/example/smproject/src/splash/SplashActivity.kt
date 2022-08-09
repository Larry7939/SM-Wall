package com.example.smproject.src.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.example.smproject.config.ApplicationClass
import com.example.smproject.config.BaseActivity
import com.example.smproject.databinding.ActivitySplashBinding
import com.example.smproject.src.login.idsignin.IdSignin
import com.example.smproject.src.main.MainActivity
import com.example.smproject.src.main.info.InfoFragment

class SplashActivity : BaseActivity<ActivitySplashBinding>(ActivitySplashBinding::inflate) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // sp내부의 jwt유무를 판별하여 로그인 페이지 또는 메인 페이지로 전환
        // jwt는 로그인 성공 시 sp에 저장된다.
        if(ApplicationClass.sSharedPreferences.getString(ApplicationClass.X_ACCESS_TOKEN,"")!=""){
            Handler(Looper.getMainLooper()).postDelayed({
                showCustomToast("자동로그인 되었습니다.")
                startActivity(Intent(this, MainActivity::class.java))
                overridePendingTransition(0,0)
                finish()
            },3500)
        }
        else{
            Handler(Looper.getMainLooper()).postDelayed({
                startActivity(Intent(this, IdSignin::class.java))
                overridePendingTransition(0,0)
                finish()
            },3500)
        }



    }
}