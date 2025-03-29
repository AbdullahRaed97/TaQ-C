package com.example.taq_c.data.repository

import android.content.Context
import com.example.taq_c.data.local.WeatherLocalDataSource
import com.example.taq_c.data.model.Alert
import com.example.taq_c.data.model.City
import com.example.taq_c.data.model.ForecastResponse
import com.example.taq_c.data.model.WeatherResponse
import com.example.taq_c.data.remote.WeatherRemoteDataSource
import kotlinx.coroutines.flow.Flow

class WeatherRepository private constructor(val localDataSource: WeatherLocalDataSource
            ,val remoteDataSource: WeatherRemoteDataSource
)
{
    fun getAllFavCities(): Flow<List<City>?>{
        return localDataSource.getAllFavCities()
    }
    suspend fun insertFavCity(city: City):Long{
        return localDataSource.insertFavCity(city)
    }
    suspend fun deleteFavCity(city: City):Int{
        return localDataSource.deleteFavCity(city)
    }
    suspend fun getCurrentWeatherData(lat: Double, lon: Double, units:String , lang: String): Flow<WeatherResponse?> {
        return remoteDataSource.getCurrentWeather(lat = lat , lon = lon , units = units , lang = lang)
    }
    suspend fun get5D_3HForeCastData(lat: Double, lon: Double, units:String , lang: String): Flow<ForecastResponse?> {
        return remoteDataSource.get5D_3HForecastData( lat = lat , lon = lon , units = units , lang = lang)
    }

    fun getAllAlerts(): Flow<List<Alert>>{
        return localDataSource.getAllAlerts()
    }

    suspend fun insertAlert(alert: Alert): Long{
        return localDataSource.insertAlert(alert)
    }

    suspend fun deleteAlert(alert: Alert): Int{
        return localDataSource.deleteAlert(alert)
    }

    companion object{
        @Volatile
        var instance : WeatherRepository?=null
        fun getInstance(context: Context): WeatherRepository{
            return instance?:synchronized(this) {
                val temp = WeatherRepository(
                    WeatherLocalDataSource.getInstance(context)
                , WeatherRemoteDataSource.getInstance())
                instance = temp
                temp
            }
        }
    }
}