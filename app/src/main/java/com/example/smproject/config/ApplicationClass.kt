package com.example.smproject.config

import android.app.Application
import android.content.SharedPreferences
import com.example.smproject.R
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class ApplicationClass: Application() {

    val API_URL = "" // 서버 주소
    //코틀린 전역변수 문법
    companion object{
        lateinit var sSharedPreferences: SharedPreferences
        //JWT Token Header 키값
        var X_ACCESS_TOKEN = "${R.string.jwt_token_header}"
        lateinit var sRetrofit: Retrofit

    }
    //앱 최초 생성 시에 sp를 새로만들어주고 레트로핏 인스턴스 생성
    override fun onCreate() {
        super.onCreate()
        sSharedPreferences = applicationContext.getSharedPreferences("${R.string.sp_name}", MODE_PRIVATE)
        initRetrofitInstance()
    }

    private fun initRetrofitInstance() {
        val client:OkHttpClient = OkHttpClient.Builder()
            .readTimeout(5000,TimeUnit.MILLISECONDS)
            .connectTimeout(5000,TimeUnit.MILLISECONDS)
            // 로그캣에 okhttp.OkHttpClient로 검색하면 http 통신 내용을 전시
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .addNetworkInterceptor(XAccessTokenInterceptor())
            .build()
        // sRetrofit 이라는 전역변수에 API url, 인터셉터, Gson을 넣어주고 빌드해주는 코드
        // 이 전역변수로 http 요청을 서버로 전송
        // client를 만들어서 레트로핏의 설정값을 지정해주고, 레트로핏 객체 만들어서 서버로 보내준다.
        sRetrofit = Retrofit.Builder()
            .baseUrl(API_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}