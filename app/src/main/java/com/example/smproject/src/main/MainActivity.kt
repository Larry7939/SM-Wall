package com.example.smproject.src.main

import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.smproject.R
import com.example.smproject.config.BaseActivity
import com.example.smproject.databinding.ActivityMainBinding
import com.example.smproject.src.main.ar.MyArFragment
import com.example.smproject.src.main.info.InfoFragment
import com.example.smproject.src.main.post.PostFragment
import com.example.smproject.src.main.posted.PostedFragment
import com.example.smproject.src.main.search.SearchFragment
import com.example.smproject.util.CurrentLocation
import com.example.smproject.util.PermissionSupport


class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate){
    lateinit var fragmentManager:FragmentManager
    private var fragmentAr: MyArFragment? = null //ArFragment 객체 생성
    private var fragmentSearch:SearchFragment? = null
    var fragmentPost:PostFragment? = null
    private var fragmentInfo:InfoFragment? = null
    private var fragmentPosted: PostedFragment? = null
    private lateinit var permission: PermissionSupport

    override fun onPostResume() {
        super.onPostResume()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        permissionCheck()
        CurrentLocation(this).returnLocation()

        fragmentManager = supportFragmentManager
        initBottomNavigation()

    }
    //권한 체크
    private fun permissionCheck(){
        //PermissionSupport 클래스 객체 생성
        permission = PermissionSupport(this,this)
        //권한 체크 후 리턴이 false로 들어오면,
        if(!permission.checkPermission()){
            //권한 요청
            permission.requestPermission()
        }
    }

    //Request Permission에 대한 결과 값 받아와
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        //여기서도 리턴이 false로 들어온다면(사용자가 권한 거부)
        if(!permission.permissionResult(requestCode,permissions,grantResults)){
            //다시 permission요청
            permission.requestPermission()
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun initBottomNavigation(){
        fragmentAr = MyArFragment()
        //초기 Fragment 설정
        fragmentManager.beginTransaction().replace(binding.mainFrame.id, fragmentAr!!).commit() //첫 Fragment는 ar로 설정
        //mainMenu관련 설정
        binding.mainMenu.setOnItemSelectedListener {
            //하단 메뉴가 선택되면, Fragment의 Transaction이 시작되고, 각 menu의 id에 따라 다른 Fragment로의 add및 commit이 이뤄진다.
            when(it.itemId){
                R.id.first_tab->{
                    if(fragmentAr==null){ //Fragment가 null이면 초기화해주고 add->commit
                        fragmentAr = MyArFragment()
                        fragmentManager.beginTransaction().add(binding.mainFrame.id, fragmentAr!!).commit()
                    }
                    else if(fragmentAr != null){ //null이 아니면 show
                        fragmentManager.beginTransaction().hide(fragmentAr!!).commit()
                        fragmentManager.beginTransaction().show(fragmentAr!!).commit()
                    }
                    if(fragmentSearch!=null){
                        fragmentManager.beginTransaction().hide(fragmentSearch!!).commit()
                        fragmentManager.beginTransaction().remove(fragmentSearch!!).commit()
                        fragmentSearch = null
                    }
                    if(fragmentPost!=null){
                        fragmentManager.beginTransaction().hide(fragmentPost!!).commit()
                    }
                    if(fragmentInfo!=null){
                        fragmentManager.beginTransaction().hide(fragmentInfo!!).commit()
                    }
                    if(fragmentPosted !=null){
                        fragmentManager.beginTransaction().hide(fragmentPosted!!).commit()
                    }
                    true

                }
                R.id.second_tab->{
                    if(fragmentSearch==null){ //Fragment가 null이면 초기화해주고 add->commit
                        fragmentSearch = SearchFragment()
                        fragmentManager.beginTransaction().add(binding.mainFrame.id,fragmentSearch!!).commit()
                    }
                    else if(fragmentSearch != null){ //null이 아니면 show
                        fragmentSearch = SearchFragment()
                        fragmentManager.beginTransaction().show(fragmentSearch!!).commit()
                    }
                    if(fragmentAr!=null){
                        fragmentManager.beginTransaction().hide(fragmentAr!!).commit()
                    }
                    if(fragmentPost!=null){
                        fragmentManager.beginTransaction().hide(fragmentPost!!).commit()
                    }
                    if(fragmentInfo!=null){
                        fragmentManager.beginTransaction().hide(fragmentInfo!!).commit()
                    }
                    if(fragmentPosted !=null){
                        fragmentManager.beginTransaction().hide(fragmentPosted!!).commit()
                    }
                    true
                }
                R.id.third_tab->{
                    if(fragmentPost==null){ //Fragment가 null이면 초기화해주고 add->commit
                        fragmentPost = PostFragment()
                        fragmentManager.beginTransaction().add(binding.mainFrame.id,fragmentPost!!).commit()
                    }
                    if(fragmentPost != null){ //null이 아니면 show
                        fragmentManager.beginTransaction().show(fragmentPost!!).commit()
                    }
                    if(fragmentAr!=null){
                        fragmentManager.beginTransaction().hide(fragmentAr!!).commit()
                    }
                    if(fragmentSearch!=null){
                        fragmentManager.beginTransaction().hide(fragmentSearch!!).commit()
                        fragmentManager.beginTransaction().remove(fragmentSearch!!).commit()
                        fragmentSearch = null
                    }
                    if(fragmentInfo!=null){
                        fragmentManager.beginTransaction().hide(fragmentInfo!!).commit()
                    }
                    if(fragmentPosted !=null){
                        fragmentManager.beginTransaction().hide(fragmentPosted!!).commit()
                    }
                    true
                }
                R.id.fourth_tab->{
                    if(fragmentInfo==null){ //Fragment가 null이면 초기화해주고 add->commit
                        fragmentInfo = InfoFragment()
                        fragmentManager.beginTransaction().add(binding.mainFrame.id,fragmentInfo!!).commit()
                    }
                    if(fragmentInfo != null){ //null이 아니면 show
                        fragmentManager.beginTransaction().show(fragmentInfo!!).commit()
                    }
                    if(fragmentAr!=null){
                        fragmentManager.beginTransaction().hide(fragmentAr!!).commit()
                    }
                    if(fragmentSearch!=null){
                        fragmentManager.beginTransaction().hide(fragmentSearch!!).commit()
                        fragmentManager.beginTransaction().remove(fragmentSearch!!).commit()
                        fragmentSearch = null
                    }
                    if(fragmentPost!=null){
                        fragmentManager.beginTransaction().hide(fragmentPost!!).commit()
                    }
                    if(fragmentPosted !=null){
                        fragmentManager.beginTransaction().hide(fragmentPosted!!).commit()
                    }
                    true
                }
                else -> false
            }
        }
    }

    //하단 탭을 제외한 fragment화면 전환
    fun replaceFragment(fragment: Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        if(fragmentPosted==null){ //Fragment가 null이면 초기화해주고 add->commit
            fragmentPosted = PostedFragment()
            fragmentManager.beginTransaction().add(binding.mainFrame.id,fragmentPosted!!).commit()
        }
        if(fragmentPosted != null){ //null이 아니면 show
            fragmentManager.beginTransaction().show(fragmentPosted!!).commit()
        }

        if(fragmentAr!=null){
            fragmentManager.beginTransaction().hide(fragmentAr!!).commit()
        }
        if(fragmentSearch!=null){
            fragmentManager.beginTransaction().hide(fragmentSearch!!).commit()
            fragmentSearch = null
        }
        if(fragmentPost!=null){
            fragmentManager.beginTransaction().hide(fragmentPost!!).commit()
        }
        if(fragmentInfo!=null){
            fragmentManager.beginTransaction().hide(fragmentInfo!!).commit()
        }
    }

}