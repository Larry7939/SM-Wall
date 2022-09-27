package com.example.smproject.src.main.info

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smproject.databinding.ItemListBinding
import com.example.smproject.databinding.PostListBinding
import com.example.smproject.src.main.adapter.RecyclerViewAdapter
import com.example.smproject.src.main.getPostApi.GetPostListService
import com.example.smproject.src.main.getPostApi.GetPostListView
import com.example.smproject.src.main.getPostApi.models.GetPostListRequest
import com.example.smproject.src.main.getPostApi.models.GetPostListResonse
import com.example.smproject.src.main.getPostApi.models.Post
import com.example.smproject.src.main.posted.PostedService
import com.example.smproject.src.main.posted.PostedView
import com.example.smproject.src.main.posted.models.Info
import com.example.smproject.src.main.posted.models.PostedRequest
import com.example.smproject.src.main.posted.models.PostedResponse

class InfoPostActivity:AppCompatActivity(), GetPostListView, PostedView{
    private lateinit var binding:PostListBinding
    private lateinit var adapter:RecyclerViewAdapter

    var postLists = mutableListOf<Info>()
    var getPostLists = mutableListOf<Post>()
    var postAdd = mutableListOf<Info>()

    // postLists : image, content, hashtag 담은 리스트
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = PostListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        GetPostListService(this).tryGetPostList(
            GetPostListRequest(
                "getPostList",
                null,
                null,
                null,
                1,
                null
            )
        )
    }

    override fun onGetPostListSuccess(response: GetPostListResonse) {
        getPostLists = response.data.list
        for (i in 0 until getPostLists.size){
            Log.d("게시물 id 목록 ", "${getPostLists[i].id}")
            PostedService(this).tryGetPosted(PostedRequest("getPostInfo","${getPostLists[i].id}"))
        }
        Log.d("getPostList size ", "${getPostLists.size}")


    }
    override fun onGetPostListFailure(message: String) {
        Log.d("게시물 목록 요청 실패",message)
    }

    override fun onPostedSuccess(response: PostedResponse) {

        postLists.add(response.data.info)

        Log.d("adapter size", "${postLists.size}")

        val adapter=RecyclerViewAdapter()
        adapter.dataList=postLists
        binding.recyclerView.adapter=adapter
        binding.recyclerView.layoutManager=LinearLayoutManager(this)
    }

    override fun onPostedFailure(message: String) {
        Log.d("게시물 정보 요청 실패",message)

    }
    fun addPost(){

    }
}

