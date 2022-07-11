package com.example.smproject.util

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.view.Window
import android.view.animation.AnimationUtils
import com.example.smproject.R
import com.example.smproject.databinding.DialogLoadingBinding

class LoadingDialog(context: Context, loadingType:Int) : Dialog(context) {

    private lateinit var binding: DialogLoadingBinding

    val handler = Handler()
    var loadingtype = loadingType //loadingType으로 상황에 따라 다른 로딩 창 구현 가능(추후 추가 예정)
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        binding = DialogLoadingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setCanceledOnTouchOutside(false)
        setCancelable(false)
        window!!.setBackgroundDrawable(ColorDrawable())
        window!!.setDimAmount(0.2f)
        if(loadingtype==0){
            binding.loadingTv.text = R.string.loading_login.toString() //로그인 중입니다.
        }
        else if(loadingtype==1){
            binding.loadingTv.text = R.string.loading_signup.toString() // 회원가입 중입니다.
        }
    }
    override fun show() {
        if(!this.isShowing) super.show()
        handler.post {
            var rotate = AnimationUtils.loadAnimation(context, R.anim.loading_anim_rotate)
            binding.loadingIcon.startAnimation(rotate)
        }
    }
}