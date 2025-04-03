package com.example.taq_c.favourite.viewModel

import com.example.taq_c.data.model.Alert
import com.example.taq_c.data.model.City
import com.example.taq_c.data.model.ForecastResponse
import com.example.taq_c.data.model.WeatherResponse
import com.example.taq_c.data.repository.IWeatherRepository
import kotlinx.coroutines.flow.Flow

class StubRepo : IWeatherRepository {
    override fun getAllFavCities(): Flow<List<City>?> {
        TODO("Not yet implemented")
    }

    override suspend fun insertFavCity(city: City): Long {
        return 1
    }

    override suspend fun deleteFavCity(city: City): Int {
        return 1
    }

    override suspend fun getCurrentWeatherData(
        lat: Double,
        lon: Double,
        units: String,
        lang: String
    ): Flow<WeatherResponse?> {
        TODO("Not yet implemented")
    }

    override suspend fun get5D_3HForeCastData(
        lat: Double,
        lon: Double,
        units: String,
        lang: String
    ): Flow<ForecastResponse?> {
        TODO("Not yet implemented")
    }

    override fun getAllAlerts(): Flow<List<Alert>> {
        TODO("Not yet implemented")
    }

    override suspend fun insertAlert(alert: Alert): Long {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAlert(alert: Alert): Int {
        TODO("Not yet implemented")
    }
}