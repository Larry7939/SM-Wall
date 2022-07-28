package com.example.smproject.src.main.info

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import com.example.smproject.R
import com.example.smproject.config.ApplicationClass
import com.example.smproject.config.BaseFragment
import com.example.smproject.databinding.FragmentInfoBinding
import com.example.smproject.src.login.idsignin.IdSignin


class InfoFragment : BaseFragment<FragmentInfoBinding>(FragmentInfoBinding::bind,R.layout.fragment_info) {
    private val editor = ApplicationClass.sSharedPreferences.edit()
    fun newInstance(): Fragment {
        return InfoFragment()
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        //로그아웃 버튼을 누르면 sp내부의 토큰을 삭제
        binding.infoLogout.setOnClickListener {
            editor.putString(ApplicationClass.X_ACCESS_TOKEN,"")
            editor.commit()
            //fragment에서 activity의 함수를 호출하기 위해 activity키워드 사용
            activity?.startActivity(Intent(activity, IdSignin::class.java))
            activity?.overridePendingTransition(0,0)
            activity?.finish()
        }
    }

}