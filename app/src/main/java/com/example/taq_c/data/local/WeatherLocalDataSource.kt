package com.example.taq_c.data.local

import android.content.Context
import com.example.taq_c.data.model.Alert
import com.example.taq_c.data.model.City
import kotlinx.coroutines.flow.Flow

class WeatherLocalDataSource private constructor(
    val weatherDao: WeatherDao,
    val alertDao: AlertDao
) {

    fun getAllFavCities(): Flow<List<City>> {
        return weatherDao.getAllFavCities()
    }

    suspend fun insertFavCity(city: City): Long {
        return weatherDao.insertFavCity(city)
    }

    suspend fun deleteFavCity(city: City): Int {
        return weatherDao.deleteFavCity(city)
    }

    fun getAllAlerts(): Flow<List<Alert>> {
        return alertDao.getAllAlert()
    }

    suspend fun insertAlert(alert: Alert): Long {
        return alertDao.insertAlert(alert)
    }

    suspend fun deleteAlert(alert: Alert): Int {
        return alertDao.deleteAlert(alert)
    }

    companion object {
        @Volatile
        private var instance: WeatherLocalDataSource? = null
        fun getInstance(context: Context): WeatherLocalDataSource {
            return instance ?: synchronized(this) {
                val temp = WeatherLocalDataSource(
                    WeatherDatabase.getInstance(context).getWeatherDao(),
                    WeatherDatabase.getInstance(context).getAlertDao()
                )
                instance = temp
                temp
            }
        }
    }
}