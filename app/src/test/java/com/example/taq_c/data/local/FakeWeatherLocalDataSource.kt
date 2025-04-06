package com.example.taq_c.data.local

import com.example.taq_c.data.model.Alert
import com.example.taq_c.data.model.City
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeWeatherLocalDataSource(private val cityList : MutableList<City> = mutableListOf()) : IWeatherLocalDataSource {

    override fun getAllFavCities(): Flow<List<City>> {
        return flowOf( cityList)
    }

    override suspend fun insertFavCity(city: City): Long {
        cityList.add(city)
        return 1
    }

    override suspend fun deleteFavCity(city: City): Int {
        cityList.remove(city)
        return 1
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

    override suspend fun deleteAlertByTime(timeStamp: Long): Int {
        TODO("Not yet implemented")
    }
}