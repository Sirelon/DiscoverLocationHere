package com.sirelon.discover.location.network

import android.content.Context
import com.google.gson.Gson
import com.readystatesoftware.chuck.ChuckInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

fun createSimpleRetrofit(context: Context, baseUrl: String): Retrofit {
    val gson = createGson()
    val client = createOkkHttp(context)

    return Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()
}

fun createOkkHttp(context: Context) =
    OkHttpClient.Builder()
        .addInterceptor(ChuckInterceptor(context))
//        .addInterceptor {
//            val request: Request
//
//            it.proceed(request)
//        }
        .build()

fun createGson() = Gson()