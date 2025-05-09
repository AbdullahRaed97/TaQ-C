package com.example.taq_c.data.repository

import com.example.taq_c.data.local.IWeatherLocalDataSource
import com.example.taq_c.data.local.WeatherLocalDataSource
import com.example.taq_c.data.model.Alert
import com.example.taq_c.data.model.City
import com.example.taq_c.data.model.ForecastResponse
import com.example.taq_c.data.model.LocalForecastResponse
import com.example.taq_c.data.model.LocalWeatherResponse
import com.example.taq_c.data.model.WeatherResponse
import com.example.taq_c.data.remote.IWeatherRemoteDataSource
import com.example.taq_c.data.remote.WeatherRemoteDataSource
import kotlinx.coroutines.flow.Flow

class WeatherRepository private constructor(
    val localDataSource: IWeatherLocalDataSource, val remoteDataSource: IWeatherRemoteDataSource
) : IWeatherRepository {
    override fun getAllFavCities(): Flow<List<City>?> {
        return localDataSource.getAllFavCities()
    }

    override suspend fun insertFavCity(city: City): Long {
        return localDataSource.insertFavCity(city)
    }

    override suspend fun deleteFavCity(city: City): Int {
        return localDataSource.deleteFavCity(city)
    }

    override suspend fun getCurrentWeatherData(
        lat: Double,
        lon: Double,
        units: String,
        lang: String
    ): Flow<WeatherResponse?> {
        return remoteDataSource.getCurrentWeather(lat = lat, lon = lon, units = units, lang = lang)
    }

    override suspend fun get5D_3HForeCastData(
        lat: Double,
        lon: Double,
        units: String,
        lang: String
    ): Flow<ForecastResponse?> {
        return remoteDataSource.get5D_3HForecastData(
            lat = lat,
            lon = lon,
            units = units,
            lang = lang
        )
    }

    override fun getAllAlerts(): Flow<List<Alert>> {
        return localDataSource.getAllAlerts()
    }

    override suspend fun insertAlert(alert: Alert): Long {
        return localDataSource.insertAlert(alert)
    }

    override suspend fun deleteAlert(alert: Alert): Int {
        return localDataSource.deleteAlert(alert)
    }

    override suspend fun deleteAlertByTime(timeStamp: Long): Int {
        return localDataSource.deleteAlertByTime(timeStamp)
    }

    override suspend fun insertWeatherResponse(weatherResponse: LocalWeatherResponse): Long {
        return localDataSource.insertWeatherResponse(weatherResponse)
    }

    override suspend fun insertForecastResponse(forecastResponse: LocalForecastResponse): Long {
        return localDataSource.insertForecastResponse(forecastResponse)
    }

    override fun getAllWeatherResponse(): Flow<LocalWeatherResponse> {
        return localDataSource.getAllWeatherResponse()
    }

    override fun getAllForecastResponse(): Flow<LocalForecastResponse> {
        return localDataSource.getAllForecastResponse()
    }

    companion object {
        @Volatile
        var instance: WeatherRepository? = null
        fun getInstance(
            localDataSource: IWeatherLocalDataSource,
            remoteDataSource: IWeatherRemoteDataSource
        ):
                WeatherRepository {
            return instance ?: synchronized(this) {
                val temp = WeatherRepository(
                    localDataSource,
                    remoteDataSource
                )
                instance = temp
                temp
            }
        }
    }
}