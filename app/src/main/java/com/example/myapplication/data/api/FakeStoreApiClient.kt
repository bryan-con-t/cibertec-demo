package com.example.myapplication.data.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object FakeStoreApiClient {
    private val BASE_URL = "https://fakestoreapi.com/"

    val apiService : FakeStoreApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(FakeStoreApiService::class.java)
    }
}

