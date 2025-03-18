package com.example.taq_c.data

import android.content.Context
import com.example.taq_c.data.model.City
import com.example.taq_c.data.model.WeatherResponse

class WeatherRepository private constructor(val localDataSource: WeatherLocalDataSource
            ,val remoteDataSource: WeatherRemoteDataSource)
{
    suspend fun getAllFavCities(): List<City>{
        return localDataSource.getAllFavCities()
    }
    suspend fun insertFavCity(city: City):Long{
        return localDataSource.insertFavCity(city)
    }
    suspend fun deleteFavCity(city: City):Int{
        return localDataSource.deleteFavCity(city)
    }
    suspend fun getCurrentWeatherData(lat: Double, lon: Double, units:String): WeatherResponse{
        return remoteDataSource.getCurrentWeather(lat,lon,units)
    }
    suspend fun get5D_3HForeCastData(lat: Double, lon: Double, units:String): WeatherResponse{
        return remoteDataSource.get5D_3HForecastData(lat,lon,units)
    }

    companion object{
        @Volatile
        var instance : WeatherRepository?=null
        fun getInstance(context:Context): WeatherRepository{
            return instance?:synchronized(this) {
                val temp = WeatherRepository(WeatherLocalDataSource.getInstance(context)
                , WeatherRemoteDataSource.getInstance())
                instance = temp
                temp
            }
        }
    }
}