package com.example.myapplication.data.api

import com.example.myapplication.entity.Product
import retrofit2.Call
import retrofit2.http.GET

interface FakeStoreApiService {

    @GET("products")
    fun getProducts() : Call<List<Product>>
}

