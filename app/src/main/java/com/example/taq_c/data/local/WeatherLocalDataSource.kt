package com.example.taq_c.data.local

import com.example.taq_c.data.db.AlertDao
import com.example.taq_c.data.db.ResponsesDao
import com.example.taq_c.data.db.WeatherDao
import com.example.taq_c.data.model.Alert
import com.example.taq_c.data.model.City
import com.example.taq_c.data.model.LocalForecastResponse
import com.example.taq_c.data.model.LocalWeatherResponse
import kotlinx.coroutines.flow.Flow

class WeatherLocalDataSource private constructor(
    val weatherDao: WeatherDao,
    val alertDao: AlertDao,
    val responsesDao: ResponsesDao
) : IWeatherLocalDataSource {

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

    override suspend fun deleteAlertByTime(timeStamp: Long): Int {
       return alertDao.deleteAlertByTime(timeStamp)
    }

    override suspend fun insertWeatherResponse(weatherResponse: LocalWeatherResponse): Long {
        return responsesDao.insertWeatherResponse(weatherResponse)
    }

    override suspend fun insertForecastResponse(forecastResponse: LocalForecastResponse): Long {
        return responsesDao.insertForecastResponse(forecastResponse)
    }

    override fun getAllWeatherResponse(): Flow<LocalWeatherResponse> {
       return responsesDao.getAllWeatherResponse()
    }

    override fun getAllForecastResponse(): Flow<LocalForecastResponse> {
       return responsesDao.getAllForecastResponse()
    }

    companion object {
        @Volatile
        private var instance: WeatherLocalDataSource? = null
        fun getInstance(
            weatherDao: WeatherDao,
            alertDao: AlertDao,
            responsesDao: ResponsesDao
        ): WeatherLocalDataSource {
            return instance ?: synchronized(this) {
                val temp = WeatherLocalDataSource(
                    weatherDao,
                    alertDao,
                    responsesDao
                )
                instance = temp
                temp
            }
        }
    }
}