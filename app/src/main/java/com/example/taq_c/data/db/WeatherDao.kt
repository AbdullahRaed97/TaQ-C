package com.example.taq_c.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.taq_c.data.model.City
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherDao {
    @Insert(onConflict = OnConflictStrategy.Companion.IGNORE)
    suspend fun insertFavCity(city: City): Long

    @Delete
    suspend fun deleteFavCity(city: City): Int

    @Query("select * from Cities")
    fun getAllFavCities(): Flow<List<City>>
}