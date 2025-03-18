package com.example.taq_c.data

import com.example.taq_c.data.model.WeatherResponse

class WeatherRemoteDataSource private constructor(val weatherApi: WeatherApi) {

    suspend fun getCurrentWeather(lat:Double,lon:Double,units:String): WeatherResponse{
        return weatherApi.getCurrentWeather(lat,lon,units)
    }
    suspend fun get5D_3HForecastData(lat:Double,lon:Double,units:String): WeatherResponse{
        return weatherApi.get5D_3HForecastData(lat,lon,units)
    }

companion object {
    @Volatile
    var remoteInstance: WeatherRemoteDataSource? = null
    fun getInstance(): WeatherRemoteDataSource {
            return remoteInstance ?: synchronized(this){
                val temp = WeatherRemoteDataSource(RetrofitHelper.weatherService)
                remoteInstance=temp
                temp
            }
        }
    }
}