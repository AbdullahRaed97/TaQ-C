package com.example.taq_c.data.remote

import com.example.taq_c.data.model.ForecastResponse
import com.example.taq_c.data.model.WeatherResponse
import kotlinx.coroutines.flow.Flow

class FakeWeatherRemoteDataSource : IWeatherRemoteDataSource{

    override suspend fun getCurrentWeather(
        lat: Double,
        lon: Double,
        units: String,
        lang: String
    ): Flow<WeatherResponse> {
        TODO("Not yet implemented")
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