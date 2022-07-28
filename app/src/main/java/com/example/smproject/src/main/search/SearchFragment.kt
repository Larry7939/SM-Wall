package com.example.smproject.src.main.search

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.smproject.R
import com.example.smproject.config.BaseFragment
import com.example.smproject.databinding.FragmentInfoBinding
import com.example.smproject.databinding.FragmentSearchBinding
import com.example.smproject.src.main.info.InfoFragment

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SearchFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SearchFragment : BaseFragment<FragmentSearchBinding>(FragmentSearchBinding::bind,R.layout.fragment_search) {

    fun newInstance(): Fragment {
        return InfoFragment()
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}