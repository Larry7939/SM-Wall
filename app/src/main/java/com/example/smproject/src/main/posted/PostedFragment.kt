package com.example.smproject.src.main.posted

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.setFragmentResultListener
import com.example.smproject.R
import com.example.smproject.config.ApplicationClass
import com.example.smproject.config.BaseFragment
import com.example.smproject.databinding.FragmentInfoBinding
import com.example.smproject.databinding.FragmentPostedBinding
import com.example.smproject.src.main.MainActivity
import com.example.smproject.src.main.ar.MyArFragment
import com.example.smproject.util.CurrentLocation

class PostedFragment : BaseFragment<FragmentPostedBinding>(FragmentPostedBinding::bind,R.layout.fragment_posted) {

    fun newInstance(): Fragment {
        return PostedFragment()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //MyArFragment로부터 결과를 받는 쪽인 PostedFragment의 리스너 설정
        //Use the Kotlin extension in the fragment-ktx artifact
        //request키를 다르게 하면 여러개의 데이터를 줄 수 있다!!!!
        setFragmentResultListener("idKey"){requestKey, bundle ->
            //We use a String here but any type that can be put in a Bundle is supported
            var id = bundle.getString("bundleKey")

            //Do something with the result
            binding.postedId.text = "게시물 ID - ${id.toString()}"
        }
        setFragmentResultListener("contentKey"){requestKey, bundle ->
            //We use a String here but any type that can be put in a Bundle is supported
            var content = bundle.getString("bundleKey")
            //Do something with the result
            binding.postedContent.text = "글 내용 - ${content.toString()}"
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)
        binding.postedBackBtn.setOnClickListener {
            (activity as MainActivity).replaceFragment(MyArFragment())
        }
    }
}