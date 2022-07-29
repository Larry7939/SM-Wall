package com.example.smproject.src.main.info

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import com.example.smproject.R
import com.example.smproject.config.ApplicationClass
import com.example.smproject.config.BaseFragment
import com.example.smproject.databinding.FragmentInfoBinding
import com.example.smproject.src.login.idsignin.IdSignin
import com.example.smproject.src.main.MainActivity
import com.example.smproject.util.LogoutDialog
import kotlin.math.log


class InfoFragment : BaseFragment<FragmentInfoBinding>(FragmentInfoBinding::bind,R.layout.fragment_info) {

    lateinit var logoutDialog:LogoutDialog
    fun newInstance(): Fragment {
        return InfoFragment()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        logoutDialog = LogoutDialog(context as MainActivity)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //로그아웃 버튼을 누르면 sp내부의 토큰을 삭제

        binding.infoLogout.setOnClickListener {
            logoutDialog.show()
        }


    }

}