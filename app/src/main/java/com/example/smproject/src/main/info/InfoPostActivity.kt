package com.example.smproject.src.main.info

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smproject.databinding.PostListBinding
import com.example.smproject.src.main.MainActivity
import com.example.smproject.src.main.adapter.RecyclerViewAdapter
import com.example.smproject.src.main.getPostApi.GetPostListService
import com.example.smproject.src.main.getPostApi.GetPostListView
import com.example.smproject.src.main.getPostApi.models.Data
import com.example.smproject.src.main.getPostApi.models.GetPostListRequest
import com.example.smproject.src.main.getPostApi.models.GetPostListResonse
import com.example.smproject.src.main.posted.PostedRetrofitInterface
import com.example.smproject.src.main.posted.PostedService
import com.example.smproject.src.main.posted.PostedView
import com.example.smproject.src.main.posted.models.Info
import com.example.smproject.src.main.posted.models.PostedRequest
import com.example.smproject.src.main.posted.models.PostedResponse
import com.example.smproject.src.main.search.SearchFragment
import retrofit2.Call
import retrofit2.Retrofit

class InfoPostActivity:AppCompatActivity(), GetPostListView, PostedView{
    private lateinit var binding:PostListBinding
    private lateinit var adapter:RecyclerViewAdapter
    val postLists = ArrayList<Info>()
    // postLists : content, hashtag 담은 리스트
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)

        binding=PostListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializelist()
    }
    private fun initializelist(){
        val adapter=RecyclerViewAdapter(postLists)

        binding.recyclerView.adapter=adapter
        binding.recyclerView.layoutManager=LinearLayoutManager(this)
        GetPostListService(this).tryGetPostList(GetPostListRequest("getPostList", null,null,null,1,null))

    }
    override fun onGetPostListSuccess(response: GetPostListResonse) {
        Log.d("게시물 목록", "")

    }
    override fun onGetPostListFailure(message: String) {
        Log.d("게시물 목록 요청 실패",message)
    }
    override fun onPostedSuccess(response: PostedResponse) {
//        val postedId = response.data.info.id.toString()
//        val postedContent = response.data.info.content
//        val postedHashTag = response.data.info.hashtag
        Log.d("게시물 정보 요청","성공")
    }
    override fun onPostedFailure(message: String) {
        Log.d("게시물 정보 요청 실패",message)

    }
}
