package com.example.taq_c.data.remote

import com.example.taq_c.data.model.ForecastResponse
import com.example.taq_c.data.model.WeatherResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class WeatherRemoteDataSource private constructor(val weatherApi: WeatherApi) : IWeatherRemoteDataSource
{

   override suspend fun getCurrentWeather(lat: Double, lon: Double, units:String , lang: String): Flow<WeatherResponse> {

        return flowOf(weatherApi.getCurrentWeather(lat=lat,lon=lon, units = units , lang = lang))
    }
   override suspend fun get5D_3HForecastData(lat: Double, lon: Double, units:String ,lang: String): Flow<ForecastResponse> {
        return flowOf(weatherApi.get5D_3HForecastData(lat = lat, lon = lon, units = units, lang = lang))
    }

companion object {
    @Volatile
    var remoteInstance: WeatherRemoteDataSource? = null
    fun getInstance(weatherApi: WeatherApi): WeatherRemoteDataSource {
            return remoteInstance ?: synchronized(this){
                val temp = WeatherRemoteDataSource(weatherApi)
                remoteInstance=temp
                temp
            }
        }
    }
}