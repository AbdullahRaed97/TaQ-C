package com.example.taq_c.data.local

import android.content.Context
import com.example.taq_c.data.db.AlertDao
import com.example.taq_c.data.db.WeatherDao
import com.example.taq_c.data.db.WeatherDatabase
import com.example.taq_c.data.model.Alert
import com.example.taq_c.data.model.City
import kotlinx.coroutines.flow.Flow

class WeatherLocalDataSource private constructor(
    val weatherDao: WeatherDao,
    val alertDao: AlertDao
) : IWeatherLocalDataSource
{

   override fun getAllFavCities(): Flow<List<City>> {
        return weatherDao.getAllFavCities()
    }

    override suspend fun insertFavCity(city: City): Long {
        return weatherDao.insertFavCity(city)
    }

    override suspend fun deleteFavCity(city: City): Int {
        return weatherDao.deleteFavCity(city)
    }

    override fun getAllAlerts(): Flow<List<Alert>> {
        return alertDao.getAllAlert()
    }

    override suspend fun insertAlert(alert: Alert): Long {
        return alertDao.insertAlert(alert)
    }

    override suspend fun deleteAlert(alert: Alert): Int {
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