package com.example.smproject.src.main.post

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.smproject.R
import com.example.smproject.config.BaseFragment
import com.example.smproject.databinding.FragmentInfoBinding
import com.example.smproject.databinding.FragmentPostBinding
import com.example.smproject.src.main.info.InfoFragment

class PostFragment : BaseFragment<FragmentPostBinding>(FragmentPostBinding::bind,R.layout.fragment_post) {

    fun newInstance(): Fragment {
        return InfoFragment()
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}