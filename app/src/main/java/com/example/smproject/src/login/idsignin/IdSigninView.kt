package com.example.smproject.src.login.idsignin

import com.example.smproject.src.login.idsignin.models.IdSigninResponse

interface IdSigninView {
    fun onPostSigninSuccess(response: IdSigninResponse)
    fun onPostSigninFailure(message:String)
}