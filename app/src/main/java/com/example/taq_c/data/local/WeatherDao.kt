package com.example.taq_c.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.taq_c.data.model.City

@Dao
interface WeatherDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertFavCity(city: City):Long
    @Delete
    suspend fun deleteFavCity(city:City):Int
    @Query("select * from Cities")
    suspend fun getAllFavCities():List<City>
}