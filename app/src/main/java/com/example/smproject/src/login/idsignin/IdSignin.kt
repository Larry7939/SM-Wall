package com.example.smproject.src.login.idsignin

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import com.example.smproject.config.ApplicationClass
import com.example.smproject.config.BaseActivity
import com.example.smproject.databinding.ActivitySigninBinding
import com.example.smproject.src.login.idsignin.models.*
import com.example.smproject.src.main.home.HomeActivity
import com.example.smproject.src.login.idsignup.IdSignup


class IdSignin : BaseActivity<ActivitySigninBinding>(ActivitySigninBinding::inflate),IdSigninView {
    private var idInput =""
    private var pwInput =""
    private val editor = ApplicationClass.sSharedPreferences.edit()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //회원가입 창으로 이동
        binding.btnRegister.setOnClickListener {
            startActivity(Intent(this, IdSignup::class.java))
            overridePendingTransition(0,0)
            finish()
        }
        //로그인
        binding.btnLogin.setOnClickListener {
            if(loginWarning()){
                showLoadingDialog(this,0) //로그인 중입니다.
                IdSigninService(this).tryGetIdSignin(IdSigninRequest("login",idInput,pwInput)) //try에서 On~Success함수를 호출해준다.
            }
        }

    }
    override fun onPostSigninSuccess(response: IdSigninResponse) {
        dismissLoadingDialog()
        if(response.data.result){
            Log.d("로그인 성공 여부", "성공")
            //회원가입 성공 시 로그인 화면으로 이동
            showCustomToast("로그인 성공")
            editor.putString(ApplicationClass.X_ACCESS_TOKEN,response.data.accessToken)
            editor.commit()
            startActivity(Intent(this, HomeActivity::class.java))
            overridePendingTransition(0, 0)
            finish()
        }
        else {
            Log.d("로그인 성공 여부","실패")
            if(response.data.code == 106){
                showCustomToast("ID 또는 PW가 일치하지 않습니다.")
            }
        }

    }
    override fun onPostSigninFailure(message: String) {
        dismissLoadingDialog()
        showCustomToast("오류 : $message")
    }
    fun loginWarning():Boolean{
        idInput = binding.editId.text.toString()
        pwInput = binding.editPw.text.toString()
        if(idInput.isEmpty()){
            showCustomToast("ID를 입력해주세요!")
            return false
        }
        else if(pwInput.isEmpty()){
            showCustomToast("PW를 입력해주세요!")
            return false
        }
        else{
            return true
        }
    }

}