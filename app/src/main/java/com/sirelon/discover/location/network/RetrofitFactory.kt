package com.sirelon.discover.location.network

import android.content.Context
import com.google.gson.Gson
import com.readystatesoftware.chuck.ChuckInterceptor
import okhttp3.OkHttpClient
import okhttp3.Request
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
        .addInterceptor {
            val request: Request = it.request()

            val httpUrl = request.url.newBuilder()
                .addQueryParameter("app_id", "vaFWljLqAKNdCjUs1B0g")
                .addQueryParameter("app_code", "8YtJbph7-_yJLpBgugDbcg")
                .build()

            val newRequest =
                request.newBuilder().header("Accept", "application/json").url(httpUrl).build()
            it.proceed(newRequest)
        }
        .build()

fun createGson() = Gson()