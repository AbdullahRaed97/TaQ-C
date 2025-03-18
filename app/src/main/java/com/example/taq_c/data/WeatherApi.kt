package com.example.taq_c.data

import com.example.taq_c.data.model.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {

    @GET("weather")
    suspend fun getCurrentWeather(
        @Query("lat") lat:Double
       ,@Query("lon") lon :Double
       ,@Query("appid") apiKey:String ="fd4d75a2fe0a3c6c1bcf90187e30a7b0"
       ,@Query("units") units:String?=null):WeatherResponse

    @GET("forecast")
    suspend fun get5D_3HForecastData(
        @Query("lat") lat:Double
        ,@Query("lon") lon :Double
        ,@Query("appid") apiKey:String ="fd4d75a2fe0a3c6c1bcf90187e30a7b0"
        ,@Query("units") units:String?=null):WeatherResponse
}