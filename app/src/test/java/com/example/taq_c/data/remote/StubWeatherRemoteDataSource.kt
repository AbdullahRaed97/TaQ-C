package com.example.taq_c.data.remote

import com.example.taq_c.data.model.ForecastResponse
import com.example.taq_c.data.model.WeatherResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class StubWeatherRemoteDataSource(private val weatherResponse: WeatherResponse) : IWeatherRemoteDataSource{

    override suspend fun getCurrentWeather(
        lat: Double,
        lon: Double,
        units: String,
        lang: String
    ): Flow<WeatherResponse> {
       return flowOf(weatherResponse)
    }

    override suspend fun get5D_3HForecastData(
        lat: Double,
        lon: Double,
        units: String,
        lang: String
    ): Flow<ForecastResponse> {
        TODO("Not yet implemented")
    }

}