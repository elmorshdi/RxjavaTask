package com.elmorshdi.internTask.datasource.network

import android.content.SharedPreferences
import android.util.Log
import com.elmorshdi.internTask.view.util.SharedPreferencesManager.getToken
import okhttp3.Interceptor
import javax.inject.Inject

class MyInterceptor
@Inject constructor(private val sharedPreferences: SharedPreferences) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
       val token= getToken(sharedPreferences)
         return if (token.isNotEmpty()) {
             val request = chain.request()
                .newBuilder()
                .addHeader("Authorization","Bearer $token")
                .addHeader("Accept", "application/json")
                .build()
             chain.proceed(request)
        } else {
             val request = chain.request()
                .newBuilder()
                .addHeader("Accept", "application/json")
                .build()
            chain.proceed(request)
        }
    }
}