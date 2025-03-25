package com.example.taq_c.data.local

import android.content.Context
import com.example.taq_c.data.model.City
import kotlinx.coroutines.flow.Flow

class WeatherLocalDataSource private constructor(val weatherDao: WeatherDao) {

    suspend fun getAllFavCities():Flow<List<City>>{
        return weatherDao.getAllFavCities()
    }
    suspend fun insertFavCity(city: City): Long{
        return weatherDao.insertFavCity(city)
    }
    suspend fun deleteFavCity(city: City):Int{
        return weatherDao.deleteFavCity(city)
    }


companion object{
    @Volatile
    private var instance : WeatherLocalDataSource? = null
    fun getInstance(context: Context): WeatherLocalDataSource{
        return instance?:synchronized(this) {
            val temp = WeatherLocalDataSource(WeatherDatabase.getInstance(context).getWeatherDao())
            instance = temp
            temp
        }
    }
}
}