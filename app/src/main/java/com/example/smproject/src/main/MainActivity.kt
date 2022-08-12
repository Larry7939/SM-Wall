package com.example.smproject.src.main


import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentManager
import com.example.smproject.R
import com.example.smproject.config.BaseActivity
import com.example.smproject.databinding.ActivityMainBinding
import com.example.smproject.src.main.ar.ArFragment
import com.example.smproject.src.main.info.InfoFragment
import com.example.smproject.src.main.post.PostFragment
import com.example.smproject.src.main.search.SearchFragment



class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate){
    private lateinit var fragmentManager:FragmentManager
    private var fragmentAr: ArFragment? = null //ArFragment 객체 생성
    private var fragmentSearch:SearchFragment? = null
    private var fragmentPost:PostFragment? = null
    private var fragmentInfo:InfoFragment? = null
    override fun onPostResume() {
        super.onPostResume()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentManager = supportFragmentManager
        initBottomNavigation()
    }

    private fun initBottomNavigation(){
        fragmentAr = ArFragment()
        //초기 Fragment 설정
        fragmentManager.beginTransaction().replace(binding.mainFrame.id, fragmentAr!!,).commit() //첫 Fragment는 ar로 설정
        //mainMenu관련 설정
        binding.mainMenu.setOnItemSelectedListener {
            //하단 메뉴가 선택되면, Fragment의 Transaction이 시작되고, 각 menu의 id에 따라 다른 Fragment로의 add및 commit이 이뤄진다.
            when(it.itemId){
                R.id.first_tab->{
                    if(fragmentAr==null){ //Fragment가 null이면 초기화해주고 add->commit
                        fragmentAr = ArFragment()
                        fragmentManager.beginTransaction().add(binding.mainFrame.id, fragmentAr!!).commit()
                    }
                    if(fragmentAr != null){ //null이 아니면 show
                        fragmentManager.beginTransaction().show(fragmentAr!!).commit()
                    }
                    if(fragmentSearch!=null){
                        fragmentManager.beginTransaction().hide(fragmentSearch!!).commit()
                        fragmentSearch!!.mapViewSearch.visibility = View.GONE //첫번째 탭을 누르면 search fragment의 지도는 Gone
                    }
                    if(fragmentPost!=null){
                        fragmentManager.beginTransaction().hide(fragmentPost!!).commit()
                    }
                    if(fragmentInfo!=null){
                        fragmentManager.beginTransaction().hide(fragmentInfo!!).commit()
                    }
                    true

                }
                R.id.second_tab->{
                    if(fragmentSearch==null){ //Fragment가 null이면 초기화해주고 add->commit
                        fragmentSearch = SearchFragment()
                        fragmentManager.beginTransaction().add(binding.mainFrame.id,fragmentSearch!!).commit()
                    }
                    if(fragmentSearch != null){ //null이 아니면 show
                        fragmentManager.beginTransaction().show(fragmentSearch!!).commit()
                    }
                    if(fragmentAr!=null){
                        fragmentManager.beginTransaction().hide(fragmentAr!!).commit()
                        fragmentAr!!.mapViewAr.visibility = View.GONE //두번째 탭을 누르면 ar fragment의 지도는 Gone
                    }
                    if(fragmentPost!=null){
                        fragmentManager.beginTransaction().hide(fragmentPost!!).commit()
                    }
                    if(fragmentInfo!=null){
                        fragmentManager.beginTransaction().hide(fragmentInfo!!).commit()
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
                    }
                    if(fragmentInfo!=null){
                        fragmentManager.beginTransaction().hide(fragmentInfo!!).commit()
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
                    }
                    if(fragmentPost!=null){
                        fragmentManager.beginTransaction().hide(fragmentPost!!).commit()
                    }
                    true
                }
                else -> false
            }
        }
    }


}