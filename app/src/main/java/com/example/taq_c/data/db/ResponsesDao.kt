package com.example.taq_c.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.taq_c.data.model.City
import com.example.taq_c.data.model.LocalForecastResponse
import com.example.taq_c.data.model.LocalWeatherResponse
import kotlinx.coroutines.flow.Flow

@Dao
interface ResponsesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeatherResponse(weatherResponse: LocalWeatherResponse): Long
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertForecastResponse(forecastResponse: LocalForecastResponse): Long
    @Query("SELECT * FROM WeatherResponse")
    fun getAllWeatherResponse():Flow<LocalWeatherResponse>
    @Query("SELECT * FROM ForecastResponse")
    fun getAllForecastResponse():Flow<LocalForecastResponse>
}