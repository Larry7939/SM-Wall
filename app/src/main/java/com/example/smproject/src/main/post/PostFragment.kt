package com.example.smproject.src.main.post

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.CompoundButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.SwitchCompat
import com.example.smproject.R
import com.example.smproject.config.BaseFragment
import com.example.smproject.databinding.FragmentPostBinding


class PostFragment : BaseFragment<FragmentPostBinding>(FragmentPostBinding::bind,R.layout.fragment_post) {
    private lateinit var swShare:SwitchCompat
    private lateinit var tvShare:AppCompatTextView
    fun newInstance(): Fragment {
        return PostFragment()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        swShare = binding.postSwitch
        tvShare = binding.postSwitchTv

        swShare.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                //체크된 상태로 만들 시 코드
                tvShare.text = "전체 공개"
            } else {
                //체크된 상태 취소 시 코드
                tvShare.text = "비공개"
            }
        }
    }
}