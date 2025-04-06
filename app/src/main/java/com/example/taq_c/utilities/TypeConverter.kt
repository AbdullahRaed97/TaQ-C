package com.example.taq_c.utilities

import androidx.room.TypeConverter
import com.example.taq_c.data.model.Coordinates
import com.example.taq_c.data.model.Forecast
import com.example.taq_c.data.model.ForecastResponse
import com.example.taq_c.data.model.Weather
import com.example.taq_c.data.model.WeatherResponse
import com.google.common.reflect.TypeToken
import com.google.gson.Gson

class TypeConverter {

    var gson = Gson()
    @TypeConverter
    fun fromCoordinates(coordinates: Coordinates?): String? {
        return "${coordinates?.lat},${coordinates?.lon}"
    }

    @TypeConverter
    fun toCoordinates(str: String?): Coordinates? {
        if (str == null)
            return null
        val coordinates = str.split(",")
        if (coordinates.size != 2) {
            throw IllegalArgumentException("Invalid Coordinates String values : $coordinates")
        }
        val lat = coordinates.get(0).toDoubleOrNull()
        val lon = coordinates.get(1).toDoubleOrNull()
        if (lat == null || lon == null) {
            throw IllegalArgumentException("Invalid Coordinates values : $coordinates")
        }
        return Coordinates(lat = lat, lon = lon)
    }

    @TypeConverter
    fun fromWeatherResponse(response: WeatherResponse?): String? {
        return gson.toJson(response)
    }

    @TypeConverter
    fun toWeatherResponse(json: String?): WeatherResponse? {
        if (json.isNullOrEmpty()) return null
        return gson.fromJson(json, WeatherResponse::class.java)
    }

    @TypeConverter
    fun fromForecastResponse(response: ForecastResponse?): String? {
        return gson.toJson(response)
    }

    @TypeConverter
    fun toForecastResponse(json: String?): ForecastResponse? {
        if (json.isNullOrEmpty()) return null
        return gson.fromJson(json, ForecastResponse::class.java)
    }
}