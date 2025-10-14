package com.samkit.swipeassignment.di

import com.samkit.swipeassignment.data.remote.api.SwipeApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

val networkModule = module {

    single {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    single {
        Retrofit.Builder()
            .baseUrl("https://app.getswipe.in/api/")
            .client(get())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    single<SwipeApiService> {
        get<Retrofit>().create(SwipeApiService::class.java)
    }
}