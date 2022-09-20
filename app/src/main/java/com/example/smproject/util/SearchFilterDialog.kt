package com.example.smproject.util

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.Toast
import com.example.smproject.config.ApplicationClass
import com.example.smproject.databinding.DialogFilterSearchBinding
import com.example.smproject.src.main.MainActivity
import com.example.smproject.src.main.getPostApi.GetPostListService
import com.example.smproject.src.main.getPostApi.GetPostListView
import com.example.smproject.src.main.getPostApi.models.GetPostListRequest
import com.example.smproject.src.main.getPostApi.models.GetPostListResonse
import com.example.smproject.src.main.search.SearchFragment

class SearchFilterDialog(context: Context) :Dialog(context){

    private lateinit var binding:DialogFilterSearchBinding
    private val editor = ApplicationClass.sSharedPreferences.edit()
    private var searchFilter = SearchFragment.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogFilterSearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //여기에서 days 바꿔가면서 getPostList 호출
        binding.searchDialogSeekbar.setOnSeekBarChangeListener(object:OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                // onProgressChange - Seekbar 값 변경될때마다 호출
                binding.searchDialogSeekbar.tag = p1
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
                // onStartTeackingTouch - SeekBar 값 변경위해 첫 눌림에 호출
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                //Seekbar를 멈추거 손을 뗏을 때 호출
                p0?.progress?.let { loadList(it) }
            }

        })

    }
    override fun show() {
        super.show()
    }
    override fun dismiss() {
        super.dismiss()
    }
    //SearchFragment의 search Days함수 호출
    fun loadList(days:Int){
        Toast.makeText(context,"${days}일 내에 작성된 게시물을 표시합니다.", Toast.LENGTH_SHORT).show()
        SearchFragment.searchDays = days
//        Toast.makeText(context, "필터 호출됨", Toast.LENGTH_SHORT).show()
//        searchFilter?.searchDays(days)
    }


}