package com.example.smproject.src.main.post

import android.app.Activity
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.content.Intent
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.smproject.R
import com.example.smproject.config.BaseFragment
import com.example.smproject.databinding.FragmentPostBinding
import com.example.smproject.util.BitmapConverter
import com.example.smproject.util.CurrentLocation
import com.google.android.material.chip.Chip


class PostFragment : BaseFragment<FragmentPostBinding>(FragmentPostBinding::bind,R.layout.fragment_post) {
    private lateinit var swShare:SwitchCompat
    private lateinit var tvShare:AppCompatTextView
    private lateinit var launcher: ActivityResultLauncher<Intent>
    private lateinit var postedImageBase64:String
    private var bitmapConverter = BitmapConverter()
    //해시태그 문자열
    private lateinit var hashString:String
    //위도 경도
    private var lat:Double = 0.0
    private var lng:Double = 0.0


    fun newInstance(): Fragment {
        return PostFragment()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        swShare = binding.postSwitch
        tvShare = binding.postSwitchTv

        swShare.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                //체크된 상태로 만들 시 코드
                tvShare.text = "전체 공개"
            } else {
                //체크된 상태 취소 시 코드
                tvShare.text = "비공개"
            }
        }
        context?.let { setPostedImage(it) }
        hashTag()

        binding.postFinish.setOnClickListener {
            CurrentLocation(requireContext()).returnLocation()
        }
    }
    private fun setPostedImage(context: Context){
        //launcher선언(앨범 열기용)
        launcher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                // RESULT_OK일 때 실행할 코드...
                val uri: Uri? = data?.data
                //iv1이 비어있으면 iv1에 이미지 set
                //iv1이 차있고 iv2도 차있으면 toast 메시지
                //iv1이 차있고 iv2가 비어있으면 iv2에 이미지 set
                if(binding.postPostedIv1.drawable==null){
                    Glide.with(context)
                        .load(uri)
                        .into(binding.postPostedIv1)
                    Log.d("비어있는 이미지 데이터 여부","${binding.postPostedIv1.drawable}")
                }
                else if(binding.postPostedIv1.drawable!=null){
                    if(binding.postPostedIv2.drawable!=null){
                        showCustomToast("사진은 최대 2장까지만 첨부할 수 있습니다.")
                    }
                    else {
                        Glide.with(context)
                            .load(uri)
                            .into(binding.postPostedIv2)
                    }
                }

                //Uri -> Bitmap -> Base64인코딩
                postedImageBase64 = bitmapConverter.uriToBase64(context,uri!!)
                //tryPost함수 호출
//                changeProfileImg(profileImageBase64)
            }
        }
        // 이미지 추가 버튼 눌렀을 때 갤러리 실행
        binding.postAddImg.setOnClickListener { v ->
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            launcher.launch(intent)
        }
    }
    private fun hashTag(){
        var chipNum:Int =0 //칩의 개수 카운트
        binding.postHashEt.addTextChangedListener(
            object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int
                ) {

                }
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    hashString = '#'+binding.postHashEt.text.toString()
                    if(hashString[hashString.length-1]==' '){
                        if(chipNum >4){
                            showCustomToast("해시태그는 최대 5개까지 남길 수 있습니다.")
                        }
                        else if(hashString[0]=='#' && hashString.length>1){ //해시태그 입력창에 입력된 문자열 검사 - 맨 앞이 #이고 길이가 1이상이어야함.
                            var chip = Chip(context)
                            chip.text = hashString
                            chip.setTextColor(R.color.black)
                            chip.setChipBackgroundColorResource(R.color.basicBackground)
                            chip.setTextAppearance(R.style.TextAppearance_MaterialComponents_Chip)
                            chip.isCloseIconVisible = true
                            chip.setOnCloseIconClickListener {
                                chip.visibility = View.GONE
                                chipNum-=1
                            }
                            chipNum+=1
                            binding.postChipGroup.addView(chip)
                            binding.postHashEt.text = null
                        }
                    }
                }
                override fun afterTextChanged(s: Editable?) {

                }

            }
        )
    }

}