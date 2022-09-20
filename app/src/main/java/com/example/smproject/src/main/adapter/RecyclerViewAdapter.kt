package com.example.smproject.src.main.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.smproject.databinding.ItemListBinding
import com.example.smproject.src.main.getPostApi.GetPostListService
import com.example.smproject.src.main.getPostApi.models.GetPostListRequest
import com.example.smproject.src.main.posted.models.Info

class RecyclerViewAdapter(val datalist : ArrayList<Info>)
    : RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder>(){

    inner class MyViewHolder(private val binding:ItemListBinding):RecyclerView.ViewHolder(binding.root){
        fun bind(postedResponse: Info){
//            binding.postPostedImg.text= postedResponse.imageUrlList
            binding.postHashTag.text = postedResponse.hashtag
            binding.postContent.text = postedResponse.content
        }
    }


    //만들어진 뷰홀더 없을 때 뷰홀더(레이아웃) 생성하는 함수
    override fun onCreateViewHolder(parent:ViewGroup,viewType:Int):MyViewHolder{
        val binding=ItemListBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return MyViewHolder(binding)
    }

    override fun getItemCount():Int=datalist.size

    //recyclerview가 viewholder를 가져와 데이터 연결할 때 호출
//적절한 데이터를 가져와서 그 데이터를 사용하여 뷰홀더의 레이아웃 채움
    override fun onBindViewHolder(holder:MyViewHolder,position:Int){
        holder.bind(datalist[position])
    }
}
