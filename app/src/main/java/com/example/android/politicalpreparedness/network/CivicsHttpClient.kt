package com.example.android.politicalpreparedness.network

import com.example.android.politicalpreparedness.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

class CivicsHttpClient: OkHttpClient() {

    companion object {

        private const val API_KEY = BuildConfig.SERVICE_KEY

        fun getClient(): OkHttpClient {

            val okHttpBuilder = Builder()

            okHttpBuilder.connectTimeout(60, TimeUnit.SECONDS)
            okHttpBuilder.readTimeout(40, TimeUnit.SECONDS)
            okHttpBuilder.writeTimeout(40, TimeUnit.SECONDS)

            okHttpBuilder.addInterceptor { chain ->
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
            }

            if (BuildConfig.DEBUG) {
                //create http instance for logging
                val loggingInterceptor = HttpLoggingInterceptor()
                loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
                okHttpBuilder.addNetworkInterceptor(loggingInterceptor)
                ////////////////////////////////////////////
            }

            return okHttpBuilder.build()
        }

    }

}