package com.example.smproject.util

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.viewbinding.ViewBinding
import com.example.smproject.config.ApplicationClass
import com.example.smproject.databinding.ActivityIdSignupBinding.inflate
import com.example.smproject.databinding.DialogLogoutBinding
import com.example.smproject.src.login.idsignin.IdSignin
import com.example.smproject.src.main.MainActivity

class LogoutDialog(context: Context) :Dialog(context){

    private lateinit var binding:DialogLogoutBinding
    private val editor = ApplicationClass.sSharedPreferences.edit()
    private var activity = context as MainActivity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogLogoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun show() {
        super.show()
        //로그아웃 Dialog 전시 및 Yes or No 표시와 선택에 따른 기능
        binding.dialogBtnYes.setOnClickListener {
            editor.putString(ApplicationClass.X_ACCESS_TOKEN,"")
            editor.commit()
            //fragment에서 activity의 함수를 호출하기 위해 activity키워드 사용
            activity?.startActivity(Intent(activity, IdSignin::class.java))
            activity?.overridePendingTransition(0,0)
            activity?.finish()
            super.dismiss()
        }
        binding.dialogBtnNo.setOnClickListener {
            super.dismiss()
        }
    }

    override fun dismiss() {
        super.dismiss()
    }
}