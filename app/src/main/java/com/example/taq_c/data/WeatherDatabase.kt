package com.example.taq_c.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.taq_c.data.model.City
import com.example.taq_c.utilities.TypeConverter

@Database(entities = arrayOf(City::class) , version = 1)
@TypeConverters(TypeConverter::class)
abstract class WeatherDatabase:RoomDatabase() {
    abstract fun getWeatherDao():WeatherDao
    companion object{
        @Volatile
        private var instance : WeatherDatabase? = null
        fun getInstance(context:Context):WeatherDatabase{
            return instance ?: synchronized(this){
                val temp : WeatherDatabase = Room.databaseBuilder(context,WeatherDatabase::class.java
                    ,"WeatherDatabase").build()
                instance = temp
                temp
            }
        }
    }
}