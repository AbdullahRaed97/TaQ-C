package com.example.taq_c.data.repository

import com.example.taq_c.data.model.Alert
import com.example.taq_c.data.model.City
import com.example.taq_c.data.model.ForecastResponse
import com.example.taq_c.data.model.WeatherResponse
import kotlinx.coroutines.flow.Flow

interface IWeatherRepository {

    fun getAllFavCities(): Flow<List<City>?>

    suspend fun insertFavCity(city: City): Long

    suspend fun deleteFavCity(city: City): Int

    suspend fun getCurrentWeatherData(
        lat: Double,
        lon: Double,
        units: String,
        lang: String
    ): Flow<WeatherResponse?>

    suspend fun get5D_3HForeCastData(
        lat: Double,
        lon: Double,
        units: String,
        lang: String
    ): Flow<ForecastResponse?>

    fun getAllAlerts(): Flow<List<Alert>>

    suspend fun insertAlert(alert: Alert): Long

    suspend fun deleteAlert(alert: Alert): Int

    suspend fun deleteAlertByTime(timeStamp: Long) : Int
}