package com.example.smproject.config

import com.google.gson.annotations.SerializedName

// 반복되는 리스폰스 내용 중복을 줄이기 위해 사용. 리스폰스 데이터 클래스를 만들때 상속해서 사용
// API response 구성에 따라 임의로 변경해야함. 모든 RESPONSE에 공통적으로 들어가는 요소들을 여기에 적으면 된다.
open class BaseResponse(
    @SerializedName("result") val result: Boolean = false,
    @SerializedName("message") val message: String? = null
)