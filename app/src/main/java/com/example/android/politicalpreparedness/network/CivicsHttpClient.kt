package com.example.android.politicalpreparedness.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

class CivicsHttpClient: OkHttpClient() {

    companion object {
        private const val API_KEY = "AIzaSyDh8M-MKBR2Xz_wwlyaDYk7jcjMBT74h70" //DONE: Place your API Key Here

        private val interceptor = HttpLoggingInterceptor()
                .apply {
                    level = HttpLoggingInterceptor.Level.BODY
                }

        fun getClient(): OkHttpClient {
            return Builder()
                    .addInterceptor { chain ->
                        val original = chain.request()
                        val url = original
                                .url
                                .newBuilder()
                                .addQueryParameter("key", API_KEY)
                                .build()
                        val request = original
                                .newBuilder()
                                .url(url)
                                .build()
                        chain.proceed(request)
                    }.addInterceptor(interceptor)
                    .build()
        }
    }
}