package com.example.smproject.util

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.smproject.R
import com.example.smproject.config.ApplicationClass
import com.example.smproject.databinding.DialogPostedBinding
import com.example.smproject.src.main.ar.MyArFragment
import com.example.smproject.src.main.info.InfoFragmentLoadView
import com.example.smproject.src.main.info.InfoLoadService
import com.example.smproject.src.main.info.models.InfoLoadRequest
import com.example.smproject.src.main.info.models.InfoLoadResponse
import com.example.smproject.src.main.posted.PostedService
import com.example.smproject.src.main.posted.PostedView
import com.example.smproject.src.main.posted.models.PostedRequest
import com.example.smproject.src.main.posted.models.PostedResponse


class PostedDialog(context: Context) : Dialog(context), PostedView  {
    private lateinit var binding: DialogPostedBinding
    private var hashTag: String =""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogPostedBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun create() {
        super.create()
        PostedService(this).tryGetPosted(PostedRequest("getPostInfo",ApplicationClass.postedId))
    }

    override fun show() {
        super.show()
    }
    override fun dismiss() {
        super.dismiss()

    }
    override fun onPostedSuccess(response: PostedResponse) {
        Log.d("게시물 정보 요청","성공")

        //게시자 정보(닉네임, 프사)
        if(response.data.info.userObj.nickname != null){
            binding.postedProfileNickname.text = response.data.info.userObj.nickname
        }
        else{
            binding.postedProfileNickname.text = "nickname"
        }

        if(response.data.info.userObj.imageUrl!=null){ //이미 업로드해놓은 이미지가 존재하는 경우에는 프로필에 이미지 set
            Glide.with(context).asBitmap().load(response.data.info.userObj.imageUrl.toString()).into(binding.postedProfileImg)
        }
        else{
            binding.postedProfileImg.setImageResource(R.drawable.info_profile)
        }

        //게시물 이미지
        if(response.data.info.imageUrlList!=null){
            if((response.data.info.imageUrlList).size==1){
                binding.postedContentImg1.visibility = View.VISIBLE
                Glide.with(context).asBitmap().load(response.data.info.imageUrlList[0]).into(binding.postedContentImg1)
            }
            else if((response.data.info.imageUrlList).size==2){
                binding.postedContentImg2.visibility = View.VISIBLE
                binding.postedContentImg3.visibility = View.VISIBLE
                Glide.with(context).asBitmap().load(response.data.info.imageUrlList[0]).into(binding.postedContentImg2)
                Glide.with(context).asBitmap().load(response.data.info.imageUrlList[1]).into(binding.postedContentImg3)
            }


            //게시물 내용
            binding.postedContentTextText.visibility = View.INVISIBLE
            binding.postedContentTextImg.visibility = View.VISIBLE
            binding.postedContentTextImg.text = response.data.info.content
            //해시태그
            binding.postedHashTagImg.visibility = View.VISIBLE
            binding.postedHashTagText.visibility = View.INVISIBLE
            hashTag = response.data.info.hashtag
            hashTag.replace(',','#')
            hashTag = '#'.plus(hashTag) //char #과 해시태그 문자열을 붙임.
            binding.postedHashTagImg.text=hashTag //여기에는 맨 앞에는 #을 붙이고, 각 ,콤마는 #으로 replace한 문자열을 넣어야함.
        }
        //게시물 이미지가 없는 경우 모두 Gone
        else{
            binding.postedContentImg1.visibility = View.GONE
            binding.postedContentImg2.visibility = View.GONE
            binding.postedContentImg3.visibility = View.GONE
            binding.postedContentTextText.visibility = View.VISIBLE
            binding.postedContentTextImg.visibility = View.INVISIBLE
            binding.postedContentTextText.text = response.data.info.content
            binding.postedHashTagImg.visibility = View.INVISIBLE
            binding.postedHashTagText.visibility = View.VISIBLE
            binding.postedHashTagText.text=hashTag //여기에는 맨 앞에는 #을 붙이고, 각 ,콤마는 #으로 replace한 문자열을 넣어야함.
        }

    }
    override fun onPostedFailure(message: String) {
        Log.d("게시물 정보 요청 실패",message)
    }
}