package com.example.smproject.src.login.idsignup

import com.example.smproject.src.login.idsignup.models.IdSignupResponse

interface IdSignupView {
    fun onPostSignUpSuccess(response: IdSignupResponse)
    fun onPostSignUpFailure(message:String)
}