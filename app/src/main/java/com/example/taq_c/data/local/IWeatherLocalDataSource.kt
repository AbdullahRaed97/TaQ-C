package com.example.taq_c.data.local

import com.example.taq_c.data.model.Alert
import com.example.taq_c.data.model.City
import kotlinx.coroutines.flow.Flow

interface IWeatherLocalDataSource {

    fun getAllFavCities(): Flow<List<City>>

    suspend fun insertFavCity(city: City): Long

    suspend fun deleteFavCity(city: City): Int

    fun getAllAlerts(): Flow<List<Alert>>

    suspend fun insertAlert(alert: Alert): Long

    suspend fun deleteAlert(alert: Alert): Int

    suspend fun deleteAlertByTime(timeStamp: Long) : Int
}