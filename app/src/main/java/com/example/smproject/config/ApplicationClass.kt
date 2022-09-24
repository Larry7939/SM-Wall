package com.example.smproject.config

import android.app.Application
import android.content.SharedPreferences
import android.util.Log
import com.example.smproject.R
import com.example.smproject.src.main.getPostApi.models.GetPostListRetrofitInterface
import com.example.smproject.src.main.posted.PostedRetrofitInterface
import com.example.smproject.util.BitmapConverter
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class ApplicationClass: Application() {

    val API_URL = "https://gjv43xpql0.execute-api.ap-northeast-2.amazonaws.com/default/smwall/" // 서버 주소

    //코틀린 전역변수 문법
    companion object{
        lateinit var sSharedPreferences: SharedPreferences
        //JWT Token Header 키값
        var X_ACCESS_TOKEN = "${R.string.jwt_token_header}"
        lateinit var sRetrofit: Retrofit
        var latitude:Double=0.0
        var longtidute:Double=0.0
        var postedId:String = "" //Dialog에서 사용하기 위한 게시물 id
        val bitmapConverter = BitmapConverter()
        lateinit var getPostListRetrofitInterface:GetPostListRetrofitInterface //게시물 리스트 load API
        lateinit var postedRetrofitInterface:PostedRetrofitInterface //게시물 정보 load API
    }
    //앱 최초 생성 시에 sp를 새로만들어주고 레트로핏 인스턴스 생성
    override fun onCreate() {
        super.onCreate()



        Log.d("sp 생성 여부","-생성됨")
        sSharedPreferences = applicationContext.getSharedPreferences("${R.string.sp_name}", MODE_PRIVATE)
        initRetrofitInstance()
    }

    private fun initRetrofitInstance() {
        val client:OkHttpClient = OkHttpClient.Builder()
            .readTimeout(5000,TimeUnit.MILLISECONDS)
            .connectTimeout(5000,TimeUnit.MILLISECONDS)
            // 로그캣에 okhttp.OkHttpClient로 검색하면 http 통신 내용을 전시
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .addNetworkInterceptor(XAccessTokenInterceptor()) //Interceptor를 이용하여 모든 네트워크 요청에 Header를 붙여준다!
            .build()
        Log.d("레트로핏 초기화 여부","-초기화됨")
        // sRetrofit 이라는 전역변수에 API url, 인터셉터, Gson을 넣어주고 빌드해주는 코드
        // 이 전역변수로 http 요청을 서버로 전송
        // client를 만들어서 레트로핏의 설정값을 지정해주고, 레트로핏 객체 만들어서 서버로 보내준다.
        sRetrofit = Retrofit.Builder()
            .baseUrl(API_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        getPostListRetrofitInterface = sRetrofit.create(GetPostListRetrofitInterface::class.java)
        postedRetrofitInterface =sRetrofit.create(PostedRetrofitInterface::class.java)
    }
}