package com.example.smproject.src.login.idsignup

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.example.smproject.config.BaseActivity
import com.example.smproject.databinding.ActivityIdSignupBinding
import com.example.smproject.src.login.idsignin.IdSignin
import com.example.smproject.src.login.idsignup.models.IdSignupRequest
import com.example.smproject.src.login.idsignup.models.IdSignupResponse

class IdSignup : BaseActivity<ActivityIdSignupBinding>(ActivityIdSignupBinding::inflate),IdSignupView {
    private var idInput =""
    private var pwInput =""
    private var pwInputRe=""
    private var nickname =""
    private var cellphone =""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //회원가입 성공 시 로그인 창으로 이동!! 여기에 api post랑 조건 추가해야함
        binding.btnCancel.setOnClickListener {
            startActivity(Intent(this,IdSignin::class.java))
            overridePendingTransition(0,0)
            finish()
        }
        binding.btnRegister2.setOnClickListener {
            idInput = binding.editId.text.toString()
            pwInput = binding.editPw.text.toString()
            pwInputRe = binding.editPwRe.text.toString()
            nickname = binding.editNickname.text.toString()
            cellphone = binding.editPhoneNumber.text.toString()
            //이 부분에서 id 중복여부와 pw랑 확인 pw의 일치여부, nickname중복 여부를 체크해야한다. 가능하다면 cellphone적합성 여부도!
            if(registerWarning()){
                showLoadingDialog(this,1) //1은 회원가입 중입니다 표시
                IdSignupService(this).tryPostIdSignup(IdSignupRequest("signup",cellphone,idInput,nickname,pwInput)) //try에서 On~Success함수를 호출해준다.
            }
        }
    }

    private fun registerWarning(): Boolean {
        if(idInput.length<6){
            showCustomToast("ID는 최소 6자 이상 입력해주세요!")
            return false
        }
        else if(pwInput!=pwInputRe){
            showCustomToast("비밀번호가 일치하지 않습니다.")
            return false
        }
        else if(nickname.length<4){
            showCustomToast("별명은 최소 4자 이상 입력해주세요!")
            return false
        }
        else if(cellphone.length!=11){
            showCustomToast("올바른 전화번호를 입력해주세요")
            return false
        }
        else{
            return true
        }

    }
    override fun onPostSignUpSuccess(response: IdSignupResponse) {
        dismissLoadingDialog()
        if(response.data.result){
            Log.d("회원가입 성공 여부","성공")
            showCustomToast("회원가입 성공")
            //회원가입 성공 시 로그인 화면으로 이동
            startActivity(Intent(this, IdSignin::class.java))
            overridePendingTransition(0,0)
            finish()
        }
        else {
            Log.d("회원가입 성공 여부","실패")
            if(response.data.code==105){
                showCustomToast("이미 존재하는 ID입니다.")
            }
        }


    }
    override fun onPostSignUpFailure(message: String) {
        dismissLoadingDialog()
        showCustomToast("오류 : $message")
    }
}