package com.example.smproject.src.main.home

import android.content.Intent
import android.os.Bundle
import com.example.smproject.config.ApplicationClass
import com.example.smproject.config.BaseActivity
import com.example.smproject.databinding.ActivityHomeBinding
import com.example.smproject.src.login.idsignin.IdSignin

class HomeActivity : BaseActivity<ActivityHomeBinding>(ActivityHomeBinding::inflate) {
    private val editor = ApplicationClass.sSharedPreferences.edit()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        //로그아웃 버튼을 누르면 sp내부의 토큰을 삭제
        binding.homeLogout.setOnClickListener {
            editor.putString(ApplicationClass.X_ACCESS_TOKEN,"")
            editor.commit()

            startActivity(Intent(this,IdSignin::class.java))
            overridePendingTransition(0,0)
            finish()
        }

    }
}