package com.example.smproject.src.login.idsignin

import android.content.Intent
import android.os.Bundle
import com.example.smproject.config.BaseActivity
import com.example.smproject.databinding.ActivitySigninBinding
import com.example.smproject.src.login.idsignup.IdSignup

class IdSignin : BaseActivity<ActivitySigninBinding>(ActivitySigninBinding::inflate) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //회원가입 창으로 이동
        binding.btnRegister.setOnClickListener {
            startActivity(Intent(this, IdSignup::class.java))
            overridePendingTransition(0,0)
            finish()
        }


    }
}