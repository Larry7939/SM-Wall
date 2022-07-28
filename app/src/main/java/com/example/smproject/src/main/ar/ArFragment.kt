package com.example.smproject.src.main.ar

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.smproject.R
import com.example.smproject.config.BaseFragment
import com.example.smproject.databinding.FragmentArBinding

class ArFragment : BaseFragment<FragmentArBinding>(FragmentArBinding::bind,R.layout.fragment_ar) {

    fun newInstance(): Fragment {
        return ArFragment()
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }


}