package com.example.smproject.src.main.info

import com.example.smproject.src.main.info.models.*

interface InfoFragmentLoadView {
    fun onPostInfoLoadSuccess(response: InfoLoadResponse)
    fun onPostInfoLoadFailure(message: String)
}