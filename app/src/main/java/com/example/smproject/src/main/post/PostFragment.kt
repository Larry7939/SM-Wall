package com.example.smproject.src.main.post

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.children
import androidx.exifinterface.media.ExifInterface
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.smproject.R
import com.example.smproject.config.ApplicationClass
import com.example.smproject.config.BaseFragment
import com.example.smproject.databinding.FragmentPostBinding
import com.example.smproject.src.main.post.models.PostPostingRequest
import com.example.smproject.src.main.post.models.PostPostingResponse
import com.example.smproject.util.BitmapConverter
import com.example.smproject.util.CurrentLocation
import com.google.android.material.chip.Chip
import java.io.IOException


class PostFragment : BaseFragment<FragmentPostBinding>(FragmentPostBinding::bind,R.layout.fragment_post),PostFragmentPostingView {
    private lateinit var swShare:SwitchCompat
    private lateinit var tvShare:AppCompatTextView
    private lateinit var launcher: ActivityResultLauncher<Intent>
    private var bitmapConverter = BitmapConverter()
    //해시태그 문자열
    private lateinit var hashString:String
    private var hashList:ArrayList<String> = ArrayList() //chip들의 text가 추가/삭제되는 리스트
    private  var hashStringToPost:String ="" //hashList를 String으로 바꿔서 서버에 보내기 위함
    //이미지 배열
    private var postImages:ArrayList<String> = ArrayList()
    //공개여부
    private var isPrivate:Int = 0
    //위도 경도
    private var lat:String=""
    private var lng:String=""
    //게시물 내용
    private var contents:String = ""
    //칩의 개수 카운트
    private var chipNum:Int =0

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
                isPrivate = 0
            } else {
                //체크된 상태 취소 시 코드
                tvShare.text = "비공개"
                isPrivate = 1
            }
        }
        context?.let { setPostedImage(it) }
        hashTag()

        //서버 통신 성공 시 이 정보들을 보내야함.
        binding.postFinish.setOnClickListener {
            postPosting() //포스팅
        }
        imgCancel() // 이미지 업로드 취소
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
                    binding.postPostedCancel1.visibility = View.VISIBLE
                    Glide.with(context)
                        .load(uri)
                        .into(binding.postPostedIv1)
                    //Uri -> Bitmap -> Base64인코딩
                    if (uri != null) {
                        // 사진 가져오기
                        val bitmap = BitmapFactory.decodeStream(context.contentResolver.openInputStream(uri))
                        // 사진의 회전 정보 가져오기
                        val orientation = getOrientationOfImage(uri).toFloat()
                        // 이미지 회전하기
                        val newBitmap = getRotatedBitmap(bitmap, orientation)
//                        // 회전된 이미지로 imaView 설정
//                        binding.postPostedIv1.setImageBitmap(newBitmap)
                        postImages.add("data:image/png;base64,${bitmapConverter.bitmapToBase64(newBitmap)}")
                    }
                }
                else if(binding.postPostedIv1.drawable!=null){
                    if(binding.postPostedIv2.drawable!=null){
                        showCustomToast("사진은 최대 2장까지만 첨부할 수 있습니다.")
                    }
                    else {
                        binding.postPostedCancel2.visibility = View.VISIBLE
                        Glide.with(context)
                            .load(uri)
                            .into(binding.postPostedIv2)
                        //Uri -> Bitmap -> Base64인코딩
                        if (uri != null) {
                            // 사진 가져오기
                            val bitmap = BitmapFactory.decodeStream(context.contentResolver.openInputStream(uri))
                            // 사진의 회전 정보 가져오기
                            val orientation = getOrientationOfImage(uri).toFloat()
                            // 이미지 회전하기
                            val newBitmap = getRotatedBitmap(bitmap, orientation)
//                            // 회전된 이미지로 imaView 설정
//                            binding.postPostedIv2.setImageBitmap(newBitmap)
                            postImages.add("data:image/png;base64,${bitmapConverter.bitmapToBase64(newBitmap)}")
                        }
                    }
                }
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

    private fun imgCancel(){
        binding.postPostedCancel1.setOnClickListener {
            //사진 1개 & 첫번째 취소버튼
            if(postImages.size==1){
                postImages.removeAt(0)
                binding.postPostedIv1.setImageDrawable(null)
                binding.postPostedCancel1.visibility = View.GONE
                binding.postPostedCancel2.visibility = View.GONE
            }
            //사진 2개 & 첫번째 취소버튼
            else if(postImages.size==2){
                postImages.removeAt(0)
                binding.postPostedIv1.setImageDrawable(binding.postPostedIv2.drawable)
                binding.postPostedIv2.setImageDrawable(null)
                binding.postPostedCancel1.visibility = View.VISIBLE
                binding.postPostedCancel2.visibility = View.GONE
            }
            for(i in postImages){
                showCustomToast("포스팅 이미지 ${i}")
            }
        }
        binding.postPostedCancel2.setOnClickListener {
            if(postImages.size==2){
                postImages.removeAt(1)
                binding.postPostedIv2.setImageDrawable(null)
                binding.postPostedCancel1.visibility = View.VISIBLE
                binding.postPostedCancel2.visibility = View.GONE
            }
            for(i in postImages){
                showCustomToast("포스팅 이미지 ${i}")
            }
        }
    }

    // 이미지 회전 정보 가져오기
    @RequiresApi(Build.VERSION_CODES.N)
    private fun getOrientationOfImage(uri: Uri): Int {
        // uri -> inputStream
        val inputStream = context?.contentResolver?.openInputStream(uri)
        val exif: ExifInterface? = try {
            inputStream?.let { ExifInterface(it) }
        } catch (e: IOException) {
            e.printStackTrace()
            return -1
        }
        inputStream?.close()

        // 회전된 각도 알아내기
        val orientation = exif?.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
        if (orientation != -1) {
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> return 90
                ExifInterface.ORIENTATION_ROTATE_180 -> return 180
                ExifInterface.ORIENTATION_ROTATE_270 -> return 270
            }
        }
        return 0
    }

    // 이미지 회전하기
    @Throws(Exception::class)
    private fun getRotatedBitmap(bitmap: Bitmap?, degrees: Float): Bitmap? {
        if (bitmap == null) return null
        if (degrees == 0F) return bitmap
        val m = Matrix()
        m.setRotate(degrees, bitmap.width.toFloat() / 2, bitmap.height.toFloat() / 2)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, m, true)
    }

    private fun hashTag(){
        binding.postHashEt.addTextChangedListener(
            object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int
                ) {

                }
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    hashString = "#"+binding.postHashEt.text.toString()
                    if(hashString[hashString.length-1]==' '){
                        if(chipNum >4){
                            showCustomToast("해시태그는 최대 5개까지 남길 수 있습니다.")
                        }
                        else if(hashString[0]=='#' && hashString.length>2){ //해시태그 입력창에 입력된 문자열 검사 - 맨 앞이 #이고 길이가 1이상이어야함.
                            var chip = Chip(context)
                            hashString = hashString.substring(0,hashString.length-1)//replace로는 맨 끝 공백이 사라지지 않아서, substring으로 공백 없애주기!
                            chip.text = hashString
                            chip.setTextColor(resources.getColor(R.color.black))
                            chip.setChipBackgroundColorResource(R.color.basicBackground)
                            chip.setTextAppearance(R.style.TextAppearance_MaterialComponents_Chip)
                            chip.isCloseIconVisible = true
                            chip.setOnCloseIconClickListener {
                                chip.visibility = View.GONE
                                //chip을 삭제하면 hashStringSum에서 삭제된 chip의 text를 삭제함.

                                hashList.remove(chip.text.toString())
                                chipNum-=1

                            }
                            chipNum+=1
                            binding.postChipGroup.addView(chip)
                            binding.postHashEt.text = null
                            //chip을 추가하면서 List에 String추가
                            hashList.add(chip.text.toString())
                        }
                    }
                }
                override fun afterTextChanged(s: Editable?) {

                }

            }
        )
    }
    private fun postPosting(){
        showLoadingDialog(context!!,2)
        //내용
        contents = binding.postEt.text.toString()
        //해시태그 joinToString
        if(hashList.isNotEmpty()){
            hashStringToPost = hashList.joinToString(",")
            hashStringToPost = hashStringToPost.replace("#","")
        }
        CurrentLocation(requireContext()).returnLocation()
        //위도,경도
        Log.d("Post - 현재 위치","${ApplicationClass.latitude},${ApplicationClass.longtidute}")

        //error code 301 - 이미지와 내용 둘다 없는 경우
//        Log.d("Posted","이미지 추가 완료 ${postImages[0]}, ${postImages[1]}")

        PostPostingService(this).tryPostPosting(PostPostingRequest("createPost", contents, postImages, hashStringToPost, isPrivate, ApplicationClass.latitude.toString(),ApplicationClass.longtidute.toString()))
        binding.postPostedCancel1.visibility = View.GONE
        binding.postPostedCancel2.visibility = View.GONE
    }

    override fun onPostPostingSuccess(response: PostPostingResponse) {
        if(response.data.result){
            Log.d("포스팅 성공여부 ","성공")
        }
        if(response.data.code==200){
            binding.postEt.text = null
            binding.postPostedIv1.setImageDrawable(null)
            binding.postPostedIv2.setImageDrawable(null)
            chipNum=0
            hashList.clear()
            for(i in binding.postChipGroup.children.iterator()){
                i.visibility = View.GONE
            }
            isPrivate = 0
            tvShare.text = "전체 공개"
            swShare.isChecked = true
            showCustomToast("작성 완료")
            dismissLoadingDialog()
        }
        else if(response.data.code==301){
            showCustomToast("내용을 입력하세요")
            dismissLoadingDialog()
        }
        else{
            dismissLoadingDialog()
        }
    }
    override fun onPostPostingFailure(message: String) {
        Log.d("포스팅 성공여부 ","실패 $message")
        dismissLoadingDialog()
    }
}