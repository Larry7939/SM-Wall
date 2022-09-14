package com.example.smproject.util

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import com.example.smproject.config.ApplicationClass
import com.example.smproject.databinding.DialogPostedBinding
import com.example.smproject.src.main.posted.PostedService
import com.example.smproject.src.main.posted.PostedView
import com.example.smproject.src.main.posted.models.PostedRequest
import com.example.smproject.src.main.posted.models.PostedResponse


class PostedDialog(context: Context) : Dialog(context), PostedView {
    private lateinit var binding: DialogPostedBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogPostedBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }

    override fun show() {
        super.show()
        PostedService(this).tryGetPosted(PostedRequest("getPostInfo",ApplicationClass.postedId))

    }

    override fun dismiss() {
        super.dismiss()
    }
    //tryGetPosted 호출 성공 시 PostedFragment로 화면 전환
    //같은 상위 Activity를 갖고있는 Fragment들간의 데이터 공유방법 -> Fragment Result API 활용
    override fun onPostedSuccess(response: PostedResponse) {
        Log.d("게시물 정보 요청","성공")
        binding.postedHashTag.text="${response.data.info.id}" //여기에는 맨 앞에는 #을 붙이고, 각 ,콤마는 #으로 replace한 문자열을 넣어야함.

    }
    override fun onPostedFailure(message: String) {
        Log.d("게시물 정보 요청 실패",message)
    }
}