package com.example.smproject.src.main

import android.content.Intent
import android.os.Bundle
import com.example.smproject.R
import com.example.smproject.config.ApplicationClass
import com.example.smproject.config.BaseActivity
import com.example.smproject.databinding.ActivityMainBinding
import com.example.smproject.src.login.idsignin.IdSignin
import com.example.smproject.src.main.ar.ArFragment
import com.example.smproject.src.main.info.InfoFragment
import com.example.smproject.src.main.post.PostFragment
import com.example.smproject.src.main.search.SearchFragment


class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {

    private var fragmentManager = supportFragmentManager
    private var fragmentAr = ArFragment() //ArFragment 객체 생성


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var fragmentTransaction = fragmentManager.beginTransaction() //초기 Fragment 설정
        fragmentTransaction.add(binding.mainFrame.id,fragmentAr).commitAllowingStateLoss()

        binding.mainMenu.setOnItemSelectedListener {
            //하단 메뉴가 선택되면, Fragment의 Transaction이 시작되고, 각 menu의 id에 따라 다른 Fragment로의 replace및 commit이 이뤄진다.
            when(it.itemId){
                R.id.first_tab->{
                    fragmentManager.beginTransaction().replace(binding.mainFrame.id,ArFragment()).commitAllowingStateLoss()
                    true
                }
                R.id.second_tab->{
                    fragmentManager.beginTransaction().replace(binding.mainFrame.id, SearchFragment()).commitAllowingStateLoss()
                    true
                }
                R.id.third_tab->{
                    fragmentManager.beginTransaction().replace(binding.mainFrame.id, PostFragment()).commitAllowingStateLoss()
                    true
                }
                R.id.fourth_tab->{
                    fragmentManager.beginTransaction().replace(binding.mainFrame.id, InfoFragment()).commitAllowingStateLoss()
                    true
                }
                else -> false
            }
        }

    }
}