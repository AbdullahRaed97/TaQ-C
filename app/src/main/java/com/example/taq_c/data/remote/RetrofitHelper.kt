package com.example.taq_c.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitHelper {
    private val BASE_URL ="https://api.openweathermap.org/data/2.5/"
    private val myRetrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    val weatherService = myRetrofit.create(WeatherApi::class.java)
}