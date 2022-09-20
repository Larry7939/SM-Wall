package com.example.smproject.util

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.viewbinding.ViewBinding
import com.example.smproject.config.ApplicationClass
import com.example.smproject.databinding.ActivityIdSignupBinding.inflate
import com.example.smproject.databinding.DialogFilterSearchBinding
import com.example.smproject.databinding.DialogLogoutBinding
import com.example.smproject.src.login.idsignin.IdSignin
import com.example.smproject.src.main.MainActivity

class SearchFilterDialog(context: Context) :Dialog(context){

    private lateinit var binding:DialogFilterSearchBinding
    private val editor = ApplicationClass.sSharedPreferences.edit()
    private var activity = context as MainActivity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogFilterSearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        binding.searchDialogSeekbar.setOnSeekBarChangeListener() //여기에서 days 바꿔가면서 getPostList 호출
    }
    override fun show() {
        super.show()
    }
    override fun dismiss() {
        super.dismiss()
    }
}