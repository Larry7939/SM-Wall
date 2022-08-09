package com.example.smproject.src.main.search

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.smproject.R
import com.example.smproject.config.BaseFragment
import com.example.smproject.databinding.FragmentInfoBinding
import com.example.smproject.databinding.FragmentSearchBinding
import com.example.smproject.src.main.info.InfoFragment

class SearchFragment : BaseFragment<FragmentSearchBinding>(FragmentSearchBinding::bind,R.layout.fragment_search) {

    fun newInstance(): Fragment {
        return SearchFragment()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}