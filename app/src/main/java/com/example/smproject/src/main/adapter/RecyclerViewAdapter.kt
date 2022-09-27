package com.example.smproject.src.main.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.smproject.R
import com.example.smproject.databinding.ItemListBinding
import com.example.smproject.databinding.PostListBinding
import com.example.smproject.src.main.getPostApi.GetPostListView
import com.example.smproject.src.main.getPostApi.models.GetPostListResonse
import com.example.smproject.src.main.posted.PostedView
import com.example.smproject.src.main.posted.models.Info
import com.example.smproject.src.main.posted.models.PostedResponse
import kotlinx.coroutines.NonDisposableHandle.parent
import org.w3c.dom.Text

class RecyclerViewAdapter()
    : RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder>(){
    var dataList = mutableListOf<Info>()

    inner class MyViewHolder(private val binding:ItemListBinding):RecyclerView.ViewHolder(binding.root) {


        fun bind(postedResponse: Info) {
            Glide.with(this.itemView).asBitmap().load(postedResponse.imageUrlList?.get(0))
                .into(binding.postPostedImg)
            binding.postContent.text = postedResponse.content

        }
    }

    //만들어진 뷰홀더 없을 때 뷰홀더(레이아웃) 생성하는 함수
    override fun onCreateViewHolder(parent:ViewGroup,viewType:Int):MyViewHolder{
        val binding=ItemListBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return MyViewHolder(binding)
    }

    override fun getItemCount():Int=dataList.size

    //recyclerview가 viewholder를 가져와 데이터 연결할 때 호출
//적절한 데이터를 가져와서 그 데이터를 사용하여 뷰홀더의 레이아웃 채움
    override fun onBindViewHolder(holder:MyViewHolder,position:Int){
        holder.bind(dataList[position])


    }
}
