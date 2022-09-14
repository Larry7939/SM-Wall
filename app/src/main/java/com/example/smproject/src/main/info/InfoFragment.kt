package com.example.smproject.src.main.info

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.smproject.BuildConfig
import com.example.smproject.R
import com.example.smproject.config.ApplicationClass
import com.example.smproject.config.BaseFragment
import com.example.smproject.databinding.FragmentInfoBinding
import com.example.smproject.src.main.MainActivity
import com.example.smproject.src.main.info.models.InfoChangeImgRequest
import com.example.smproject.src.main.info.models.InfoChangeImgResponse
import com.example.smproject.src.main.info.models.InfoLoadRequest
import com.example.smproject.src.main.info.models.InfoLoadResponse
import com.example.smproject.util.BitmapConverter
import com.example.smproject.util.LogoutDialog


class InfoFragment : BaseFragment<FragmentInfoBinding>(FragmentInfoBinding::bind,R.layout.fragment_info),InfoFragmentLoadView,InfoFragmentChangeImgView {
    lateinit var logoutDialog:LogoutDialog
    private lateinit var permissionLauncher:ActivityResultLauncher<String>
    private lateinit var launcher: ActivityResultLauncher<Intent>
    private lateinit var profileImageBase64:String
    private var bitmapConverter = BitmapConverter()
    fun newInstance(): Fragment {
        return InfoFragment()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadProfileImage() //프로필 가져오기
        context?.let { setProfileImage(it) } //갤러리에서 가져오기
        logOut() //로그아웃



    }

    private fun setProfileImage(context: Context){
        //launcher선언(앨범 열기용)
        launcher = registerForActivityResult(
            StartActivityForResult()
        ) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                // RESULT_OK일 때 실행할 코드...
                val uri: Uri? = data?.data
                Glide.with(context)
                    .load(uri)
                    .into(binding.infoProfile)
                //Uri -> Bitmap -> Base64인코딩
                profileImageBase64 = bitmapConverter.uriToBase64(context,uri!!)
                //tryPost함수 호출
                changeProfileImg(profileImageBase64)
            }
        }
        // 앨범 버튼 눌렀을 때 권한 확인하고 요청하기 및 갤러리 실행
        binding.infoProfile.setOnClickListener { v ->
//            if (ContextCompat.checkSelfPermission(
//                    requireContext(),
//                    android.Manifest.permission.READ_EXTERNAL_STORAGE
//                ) == PackageManager.PERMISSION_GRANTED
//            ) {
                // 권한 있는 경우 실행할 코드...
                // launcher를 이용해서 갤러리에 요청 보내고 갤러리 실행시키기
                val intent = Intent()
                intent.type = "image/*"
                intent.action = Intent.ACTION_GET_CONTENT
                launcher.launch(intent)
//        }
//            else {
//                // 권한 없는 경우, 권한 요청
//                permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
//            }
        }
    }
    private fun logOut(){
        //로그아웃 버튼을 누르면 sp내부의 토큰을 삭제
        logoutDialog = LogoutDialog(context as MainActivity)
        binding.infoLogout.setOnClickListener {
            logoutDialog.show()
        }
    }
    //프로필 load
    private fun loadProfileImage(){
        //프로필 가져오기(사진,별명,id)
        Log.d("발신 AccessToken","${ApplicationClass.sSharedPreferences.getString(ApplicationClass.X_ACCESS_TOKEN,null)}")
        InfoLoadService(this).tryPostInfoLoad(InfoLoadRequest("getUserInfo"))
    }
    override fun onPostInfoLoadSuccess(response: InfoLoadResponse) {
        if(response.data.result){
            Log.d("이미지 로드 성공 여부 - ","성공")
            if(response.data.info.imageUrl!=null){ //이미 업로드해놓은 이미지가 존재하는 경우에는 프로필에 이미지 set
                Glide.with(this).asBitmap().load(response.data.info.imageUrl.toString()).into(binding.infoProfile)
            }
            else{
                binding.infoProfile.setImageResource(R.drawable.info_profile)
            }
            //닉네임 set
            binding.infoNickname.text = response.data.info.nickname
        }
    }
    override fun onPostInfoLoadFailure(message: String) {
        Log.d("프로필 load 통신오류","$message")
    }

    //프로필 수정
    private fun changeProfileImg(profileBase64:String){
        InfoChangeImgService(this).tryPostInfoChangeImg(InfoChangeImgRequest("updateUserImage",
            "data:image/png;base64,$profileBase64"))
        }

    override fun onPostInfoChangeImgSuccess(response: InfoChangeImgResponse) {
        if(response.data.result){
            Log.d("이미지 수정 성공 여부 - ","성공")
        }
    }
    override fun onPostInfoChangeImgFailure(message: String) {
        Log.d("프로필 수정 통신오류","$message")
    }
}