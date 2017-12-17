package com.tacksman.todomanage.repository

import com.tacksman.todomanage.BuildConfig
import com.tacksman.todomanage.repository.interceptor.LoggingInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class TodoManageApi {

    private val logging = LoggingInterceptor().setLevel(LoggingInterceptor.Level.BODY)

    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
            .addNetworkInterceptor(logging)
            .build()

    /**
     * アクセストークンを必要としないリクエストを行うときにコールする.
     */
    fun <T> retrofit(): Retrofit {

        return Retrofit.Builder()
                .baseUrl(BuildConfig.API_URL_BASE)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()

    }

}